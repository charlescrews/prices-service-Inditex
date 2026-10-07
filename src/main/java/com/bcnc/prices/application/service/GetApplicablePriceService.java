package com.bcnc.prices.application.service;

import com.bcnc.prices.domain.exception.PriceNotFoundException;
import com.bcnc.prices.domain.model.Price;
import com.bcnc.prices.domain.model.PriceQuery;
import com.bcnc.prices.domain.port.in.GetApplicablePriceUseCase;
import com.bcnc.prices.domain.port.out.PriceRepositoryPort;

/**
 * Caso de uso: devuelve la tarifa vigente de mayor prioridad o lanza
 * {@link PriceNotFoundException} si no hay ninguna.
 */
public class GetApplicablePriceService implements GetApplicablePriceUseCase {

    private final PriceRepositoryPort priceRepositoryPort;

    public GetApplicablePriceService(PriceRepositoryPort priceRepositoryPort) {
        this.priceRepositoryPort = priceRepositoryPort;
    }

    @Override
    public Price getApplicablePrice(PriceQuery query) {
        return priceRepositoryPort.findApplicablePrice(query)
                .orElseThrow(() -> new PriceNotFoundException(query));
    }
}
