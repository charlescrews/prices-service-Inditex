# Prices Service

Servicio REST en Spring Boot que devuelve el precio final (PVP) y la tarifa aplicables a un producto de una cadena en una fecha concreta.

## Stack

- Java 21 y Spring Boot 3.5 (Web, Data JPA, Validation, Actuator)
- H2 en memoria, inicializada al arrancar con los datos del enunciado
- springdoc-openapi (Swagger UI)
- JUnit 5, MockMvc, AssertJ, Mockito, ArchUnit y JaCoCo
- Maven Wrapper, Docker y GitHub Actions

## Ejecución

No necesitas tener Maven instalado: el wrapper lo descarga.

```bash
./mvnw spring-boot:run        # Linux / macOS
mvnw.cmd spring-boot:run      # Windows
```

La aplicación queda disponible en `http://localhost:8080`:

| Recurso        | URL                                          |
|----------------|----------------------------------------------|
| Swagger UI     | http://localhost:8080/swagger-ui.html        |
| OpenAPI (JSON) | http://localhost:8080/v3/api-docs            |
| Health check   | http://localhost:8080/actuator/health        |
| Consola H2     | http://localhost:8080/h2-console             |

Para la consola H2, la URL JDBC es `jdbc:h2:mem:pricesdb`, el usuario `sa` y no hay contraseña.

### Con Docker

```bash
docker build -t prices-service .
docker run -p 8080:8080 prices-service
```

## Tests

```bash
./mvnw verify
```

Ejecuta todos los tests y genera el informe de cobertura en `target/site/jacoco/index.html`. El mismo comando se ejecuta en GitHub Actions con cada push a `main`.

## API

### `GET /api/v1/prices`

| Parámetro         | Tipo                             | Descripción                |
|-------------------|----------------------------------|----------------------------|
| `applicationDate` | ISO-8601 (`yyyy-MM-ddTHH:mm:ss`) | Fecha de aplicación        |
| `productId`       | Long (> 0)                       | Identificador del producto |
| `brandId`         | Long (> 0)                       | Identificador de la cadena |

**Ejemplo**

```bash
curl "http://localhost:8080/api/v1/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1"
```

```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 2,
  "startDate": "2020-06-14T15:00:00",
  "endDate": "2020-06-14T18:30:00",
  "price": 25.45,
  "currency": "EUR"
}
```

**Respuestas**

| Código | Situación                                                                       |
|--------|---------------------------------------------------------------------------------|
| 200    | Tarifa encontrada. Siempre se devuelve un único resultado.                      |
| 400    | Falta un parámetro, la fecha tiene un formato inválido o un id no es positivo   |
| 404    | No existe ninguna tarifa aplicable para esos parámetros                         |

Los errores siguen el estándar RFC 7807 (`application/problem+json`).

## Arquitectura

El servicio sigue una arquitectura hexagonal (puertos y adaptadores):

```
com.bcnc.prices
├── domain                      # Núcleo: sin dependencias de framework
│   ├── model                   # Price, PriceQuery
│   ├── exception               # PriceNotFoundException
│   └── port
│       ├── in                  # GetApplicablePriceUseCase
│       └── out                 # PriceRepositoryPort
├── application
│   └── service                 # GetApplicablePriceService
└── infrastructure
    ├── config                  # Registro de casos de uso y OpenAPI
    └── adapter
        ├── in/rest             # Controlador, DTO, mapper y manejo de errores
        └── out/persistence     # Entidad JPA, repositorio y adaptador
```

Las dependencias apuntan siempre hacia el dominio. Esta regla no se queda en el diagrama: `HexagonalArchitectureTest` (ArchUnit) la comprueba en cada build y falla si alguna capa interior depende de la infraestructura o de Spring.

## Decisiones técnicas

**1. La selección de la tarifa se resuelve en la base de datos.**
La consulta filtra por cadena, producto y vigencia, ordena por prioridad y pide una sola fila. La alternativa era traer todas las tarifas candidatas y elegir en el dominio con un `max()`. Habría dejado la regla de prioridad más visible en el dominio, pero a costa de transferir y materializar filas que se descartan. Como el criterio de evaluación prima la eficiencia de la extracción, la regla vive en la consulta, documentada en el puerto de salida (`PriceRepositoryPort`) y cubierta por tests del adaptador.

**2. Desempate determinista.**
El enunciado no define qué ocurre si dos tarifas con la misma prioridad se solapan. Sin un criterio explícito, la base de datos podría devolver cualquiera de ellas. Se ordena por `PRIORITY DESC, START_DATE DESC`: a igual prioridad gana la tarifa que empezó más tarde, la más específica. Este caso tiene su propio test.

**3. Límites de vigencia inclusivos.**
`START_DATE <= fecha <= END_DATE`, coherente con los datos del ejemplo, donde `END_DATE` termina en `23:59:59`. Los dos extremos están cubiertos por tests.

**4. Tipos de datos.**
- `BigDecimal` / `DECIMAL(10,2)` para el precio, para evitar errores de redondeo de coma flotante.
- `LocalDateTime` para las fechas, porque el enunciado no indica zona horaria. Ver mejoras futuras.

**5. Modelos separados por capa.**
La entidad JPA, el modelo de dominio y el DTO de respuesta son clases distintas con mappers explícitos. Así un cambio en la tabla o en el contrato de la API no se propaga al dominio. El dominio usa `record` inmutables con validación en el constructor.

**6. Dominio y aplicación sin framework.**
El caso de uso no lleva `@Service`: se registra en `BeanConfiguration`. Esto permite probarlo con un test unitario puro y cambiar de framework sin tocar el núcleo.

**7. Rendimiento en la base de datos.**
Índice compuesto `(BRAND_ID, PRODUCT_ID, START_DATE, END_DATE)` sobre las columnas del filtro, y una restricción `CHECK (END_DATE >= START_DATE)` para garantizar la integridad de los rangos.

## Testing

| Test                              | Tipo        | Qué valida                                                                 |
|-----------------------------------|-------------|----------------------------------------------------------------------------|
| `PriceControllerIntegrationTest`  | Integración | Los 5 casos del enunciado (test parametrizado) y las respuestas 400/404    |
| `PriceJpaAdapterTest`             | Persistencia| Prioridad con solapamiento, límites del rango, desempate y filtrado        |
| `GetApplicablePriceServiceTest`   | Unitario    | Caso de uso aislado con Mockito                                            |
| `HexagonalArchitectureTest`       | Arquitectura| Dirección de las dependencias y dominio libre de framework                 |

Resultados esperados de los casos del enunciado (producto 35455, cadena 1):

| Test | Fecha            | Tarifa | Precio  |
|------|------------------|--------|---------|
| 1    | 2020-06-14 10:00 | 1      | 35.50 € |
| 2    | 2020-06-14 16:00 | 2      | 25.45 € |
| 3    | 2020-06-14 21:00 | 1      | 35.50 € |
| 4    | 2020-06-15 10:00 | 3      | 30.50 € |
| 5    | 2020-06-16 21:00 | 4      | 38.95 € |

## Mejoras futuras

Fuera del alcance de la prueba, pero necesarias para producción:

- **Zona horaria:** recibir la fecha como `OffsetDateTime` y almacenar en UTC, para que una cadena con tiendas en varios países no aplique tarifas con desfase horario.
- **Caché:** los precios son de lectura intensiva y cambian poco. Una caché (Caffeine o Redis) con clave cadena + producto + franja horaria reduciría la carga, invalidándola al publicar tarifas.
- **Migraciones versionadas:** sustituir `schema.sql` por Flyway o Liquibase al pasar a una base de datos real.
- **Contract testing y API-first:** generar el controlador desde la especificación OpenAPI para que el contrato sea la fuente de verdad.
- **Observabilidad:** métricas de latencia y de consultas sin resultado con Micrometer, y trazas distribuidas.
