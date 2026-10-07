package com.bcnc.prices.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Tarifa aplicable a un producto de una cadena")
public record PriceResponse(
        @Schema(description = "Identificador del producto", example = "35455")
        Long productId,

        @Schema(description = "Identificador de la cadena", example = "1")
        Long brandId,

        @Schema(description = "Tarifa aplicada", example = "2")
        Integer priceList,

        @Schema(description = "Inicio de vigencia de la tarifa", example = "2020-06-14T15:00:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startDate,

        @Schema(description = "Fin de vigencia de la tarifa", example = "2020-06-14T18:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endDate,

        @Schema(description = "Precio final de venta", example = "25.45")
        BigDecimal price,

        @Schema(description = "Moneda (ISO 4217)", example = "EUR")
        String currency) {
}
