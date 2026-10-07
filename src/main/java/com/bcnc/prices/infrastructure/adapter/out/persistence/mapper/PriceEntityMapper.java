package com.bcnc.prices.infrastructure.adapter.out.persistence.mapper;

import com.bcnc.prices.domain.model.Price;
import com.bcnc.prices.infrastructure.adapter.out.persistence.entity.PriceEntity;
import org.springframework.stereotype.Component;

@Component
public class PriceEntityMapper {

    public Price toDomain(PriceEntity entity) {
        return new Price(
                entity.getBrandId(),
                entity.getProductId(),
                entity.getPriceList(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getPriority(),
                entity.getPrice(),
                entity.getCurrency());
    }
}
