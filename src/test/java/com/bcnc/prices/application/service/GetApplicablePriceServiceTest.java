package com.bcnc.prices.application.service;

import com.bcnc.prices.domain.exception.PriceNotFoundException;
import com.bcnc.prices.domain.model.Price;
import com.bcnc.prices.domain.model.PriceQuery;
import com.bcnc.prices.domain.port.out.PriceRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetApplicablePriceServiceTest {

    private static final PriceQuery QUERY =
            new PriceQuery(LocalDateTime.of(2020, 6, 14, 10, 0), 35455L, 1L);

    @Mock
    private PriceRepositoryPort priceRepositoryPort;

    @InjectMocks
    private GetApplicablePriceService service;

    @Test
    void shouldReturnPriceWhenRepositoryFindsOne() {
        var price = new Price(1L, 35455L, 1,
                LocalDateTime.of(2020, 6, 14, 0, 0), LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                0, new BigDecimal("35.50"), "EUR");
        when(priceRepositoryPort.findApplicablePrice(QUERY)).thenReturn(Optional.of(price));

        assertThat(service.getApplicablePrice(QUERY)).isEqualTo(price);
    }

    @Test
    void shouldThrowPriceNotFoundExceptionWhenNoPriceApplies() {
        when(priceRepositoryPort.findApplicablePrice(QUERY)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getApplicablePrice(QUERY))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessageContaining("35455");
    }
}
