package com.bcnc.prices.domain.exception;

import com.bcnc.prices.domain.model.PriceQuery;

public class PriceNotFoundException extends RuntimeException {

    public PriceNotFoundException(PriceQuery query) {
        super("No applicable price found for product %d, brand %d at %s"
                .formatted(query.productId(), query.brandId(), query.applicationDate()));
    }
}
