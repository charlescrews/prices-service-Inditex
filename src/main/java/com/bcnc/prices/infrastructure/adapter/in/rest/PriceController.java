package com.bcnc.prices.infrastructure.adapter.in.rest;

import com.bcnc.prices.domain.model.PriceQuery;
import com.bcnc.prices.domain.port.in.GetApplicablePriceUseCase;
import com.bcnc.prices.infrastructure.adapter.in.rest.dto.PriceResponse;
import com.bcnc.prices.infrastructure.adapter.in.rest.mapper.PriceResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Tag(name = "Prices", description = "Consulta de tarifas aplicables")
@RestController
@RequestMapping("/api/v1/prices")
public class PriceController {

    private final GetApplicablePriceUseCase getApplicablePriceUseCase;
    private final PriceResponseMapper mapper;

    public PriceController(GetApplicablePriceUseCase getApplicablePriceUseCase, PriceResponseMapper mapper) {
        this.getApplicablePriceUseCase = getApplicablePriceUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "Obtiene la tarifa aplicable",
            description = "Devuelve la tarifa vigente de mayor prioridad para un producto y una cadena en una fecha.")
    @ApiResponse(responseCode = "200", description = "Tarifa encontrada")
    @ApiResponse(responseCode = "400", description = "Parámetros ausentes o inválidos",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "No existe tarifa aplicable",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping
    public ResponseEntity<PriceResponse> getApplicablePrice(
            @Parameter(description = "Fecha de aplicación (ISO-8601)", example = "2020-06-14T16:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate,
            @Parameter(description = "Identificador del producto", example = "35455")
            @RequestParam @Positive Long productId,
            @Parameter(description = "Identificador de la cadena", example = "1")
            @RequestParam @Positive Long brandId) {

        var price = getApplicablePriceUseCase.getApplicablePrice(
                new PriceQuery(applicationDate, productId, brandId));
        return ResponseEntity.ok(mapper.toResponse(price));
    }
}
