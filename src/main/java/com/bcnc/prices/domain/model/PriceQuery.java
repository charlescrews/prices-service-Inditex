package com.bcnc.prices.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Criterios de búsqueda del precio aplicable.
 */
public record PriceQuery(LocalDateTime applicationDate, Long productId, Long brandId) {

    public PriceQuery {
        Objects.requireNonNull(applicationDate, "applicationDate must not be null");
        Objects.requireNonNull(productId, "productId must not be null");
        Objects.requireNonNull(brandId, "brandId must not be null");
    }
}
