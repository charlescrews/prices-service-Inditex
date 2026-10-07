package com.bcnc.prices.infrastructure.adapter.in.rest.mapper;

import com.bcnc.prices.domain.model.Price;
import com.bcnc.prices.infrastructure.adapter.in.rest.dto.PriceResponse;
import org.springframework.stereotype.Component;

@Component
public class PriceResponseMapper {

    public PriceResponse toResponse(Price price) {
        return new PriceResponse(
                price.productId(),
                price.brandId(),
                price.priceList(),
                price.startDate(),
                price.endDate(),
                price.amount(),
                price.currency());
    }
}
