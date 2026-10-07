package com.bcnc.prices.domain.port.in;

import com.bcnc.prices.domain.model.Price;
import com.bcnc.prices.domain.model.PriceQuery;

/**
 * Puerto de entrada: obtiene la tarifa aplicable para un producto, cadena y fecha.
 */
public interface GetApplicablePriceUseCase {

    Price getApplicablePrice(PriceQuery query);
}
