package com.bcnc.prices.infrastructure.adapter.out.persistence;

import com.bcnc.prices.domain.model.Price;
import com.bcnc.prices.domain.model.PriceQuery;
import com.bcnc.prices.domain.port.out.PriceRepositoryPort;
import com.bcnc.prices.infrastructure.adapter.out.persistence.mapper.PriceEntityMapper;
import com.bcnc.prices.infrastructure.adapter.out.persistence.repository.SpringDataPriceRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class PriceJpaAdapter implements PriceRepositoryPort {

    private static final Pageable FIRST_RESULT = PageRequest.of(0, 1);

    private final SpringDataPriceRepository repository;
    private final PriceEntityMapper mapper;

    public PriceJpaAdapter(SpringDataPriceRepository repository, PriceEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Price> findApplicablePrice(PriceQuery query) {
        return repository
                .findApplicablePrices(query.brandId(), query.productId(), query.applicationDate(), FIRST_RESULT)
                .stream()
                .findFirst()
                .map(mapper::toDomain);
    }
}
