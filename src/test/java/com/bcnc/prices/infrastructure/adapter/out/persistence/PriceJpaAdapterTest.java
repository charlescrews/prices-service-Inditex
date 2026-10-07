package com.bcnc.prices.infrastructure.adapter.out.persistence;

import com.bcnc.prices.domain.model.PriceQuery;
import com.bcnc.prices.infrastructure.adapter.out.persistence.mapper.PriceEntityMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({PriceJpaAdapter.class, PriceEntityMapper.class})
class PriceJpaAdapterTest {

    private static final long PRODUCT_ID = 35455L;
    private static final long BRAND_ID = 1L;

    @Autowired
    private PriceJpaAdapter adapter;

    @Test
    void shouldReturnHighestPriorityPriceWhenRangesOverlap() {
        var query = new PriceQuery(LocalDateTime.of(2020, 6, 14, 16, 0), PRODUCT_ID, BRAND_ID);

        var result = adapter.findApplicablePrice(query);

        assertThat(result).hasValueSatisfying(price -> {
            assertThat(price.priceList()).isEqualTo(2);
            assertThat(price.amount()).isEqualByComparingTo(new BigDecimal("25.45"));
        });
    }

    @Test
    void shouldIncludeStartBoundaryWhenDateMatchesStartDate() {
        var query = new PriceQuery(LocalDateTime.of(2020, 6, 14, 15, 0), PRODUCT_ID, BRAND_ID);

        assertThat(adapter.findApplicablePrice(query))
                .hasValueSatisfying(price -> assertThat(price.priceList()).isEqualTo(2));
    }

    @Test
    void shouldIncludeEndBoundaryWhenDateMatchesEndDate() {
        var query = new PriceQuery(LocalDateTime.of(2020, 6, 14, 18, 30), PRODUCT_ID, BRAND_ID);

        assertThat(adapter.findApplicablePrice(query))
                .hasValueSatisfying(price -> assertThat(price.priceList()).isEqualTo(2));
    }

    @Test
    void shouldReturnBaseTariffWhenDateIsJustAfterHigherPriorityRange() {
        var query = new PriceQuery(LocalDateTime.of(2020, 6, 14, 18, 30, 1), PRODUCT_ID, BRAND_ID);

        assertThat(adapter.findApplicablePrice(query))
                .hasValueSatisfying(price -> assertThat(price.priceList()).isEqualTo(1));
    }

    @Test
    @Sql(statements = {
            "INSERT INTO PRICES (BRAND_ID, START_DATE, END_DATE, PRICE_LIST, PRODUCT_ID, PRIORITY, PRICE, CURR) "
                    + "VALUES (2, '2021-01-01 00:00:00', '2021-12-31 23:59:59', 10, 500, 1, 10.00, 'EUR')",
            "INSERT INTO PRICES (BRAND_ID, START_DATE, END_DATE, PRICE_LIST, PRODUCT_ID, PRIORITY, PRICE, CURR) "
                    + "VALUES (2, '2021-06-01 00:00:00', '2021-06-30 23:59:59', 11, 500, 1, 8.00, 'EUR')"
    })
    void shouldReturnMostRecentlyStartedPriceWhenPrioritiesAreEqual() {
        var query = new PriceQuery(LocalDateTime.of(2021, 6, 15, 12, 0), 500L, 2L);

        assertThat(adapter.findApplicablePrice(query))
                .hasValueSatisfying(price -> assertThat(price.priceList()).isEqualTo(11));
    }

    @Test
    void shouldReturnEmptyWhenBrandDoesNotMatch() {
        var query = new PriceQuery(LocalDateTime.of(2020, 6, 14, 10, 0), PRODUCT_ID, 2L);

        assertThat(adapter.findApplicablePrice(query)).isEmpty();
    }
}
