package com.bcnc.prices.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Tarifa de precio aplicable a un producto de una cadena en un rango de fechas.
 */
public record Price(
        Long brandId,
        Long productId,
        Integer priceList,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer priority,
        BigDecimal amount,
        String currency) {

    public Price {
        Objects.requireNonNull(brandId, "brandId must not be null");
        Objects.requireNonNull(productId, "productId must not be null");
        Objects.requireNonNull(priceList, "priceList must not be null");
        Objects.requireNonNull(startDate, "startDate must not be null");
        Objects.requireNonNull(endDate, "endDate must not be null");
        Objects.requireNonNull(priority, "priority must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate must not be before startDate");
        }
    }
}
