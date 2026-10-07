package com.bcnc.prices.domain.port.out;

import com.bcnc.prices.domain.model.Price;
import com.bcnc.prices.domain.model.PriceQuery;

import java.util.Optional;

/**
 * Puerto de salida: acceso a las tarifas persistidas.
 */
public interface PriceRepositoryPort {

    /**
     * Devuelve la tarifa vigente en la fecha indicada para el producto y la cadena.
     * Si varias tarifas se solapan, devuelve la de mayor prioridad y, a igual
     * prioridad, la de inicio más reciente.
     */
    Optional<Price> findApplicablePrice(PriceQuery query);
}
