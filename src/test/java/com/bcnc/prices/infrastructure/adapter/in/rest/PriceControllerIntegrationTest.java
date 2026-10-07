package com.bcnc.prices.infrastructure.adapter.in.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PriceControllerIntegrationTest {

    private static final String PRICES_URL = "/api/v1/prices";
    private static final String PRODUCT_ID = "35455";
    private static final String BRAND_ID = "1";

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest(name = "{0}: {1} -> tarifa {2}, precio {5}")
    @CsvSource({
            "Test 1, 2020-06-14T10:00:00, 1, 2020-06-14T00:00:00, 2020-12-31T23:59:59, 35.50",
            "Test 2, 2020-06-14T16:00:00, 2, 2020-06-14T15:00:00, 2020-06-14T18:30:00, 25.45",
            "Test 3, 2020-06-14T21:00:00, 1, 2020-06-14T00:00:00, 2020-12-31T23:59:59, 35.50",
            "Test 4, 2020-06-15T10:00:00, 3, 2020-06-15T00:00:00, 2020-06-15T11:00:00, 30.50",
            "Test 5, 2020-06-16T21:00:00, 4, 2020-06-15T16:00:00, 2020-12-31T23:59:59, 38.95"
    })
    @DisplayName("Devuelve la tarifa aplicable de los casos del enunciado")
    void shouldReturnApplicablePriceWhenRequestMatchesStatementCases(
            String testName, String applicationDate, int expectedPriceList,
            String expectedStartDate, String expectedEndDate, double expectedPrice) throws Exception {

        mockMvc.perform(get(PRICES_URL)
                        .param("applicationDate", applicationDate)
                        .param("productId", PRODUCT_ID)
                        .param("brandId", BRAND_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(expectedPriceList))
                .andExpect(jsonPath("$.startDate").value(expectedStartDate))
                .andExpect(jsonPath("$.endDate").value(expectedEndDate))
                .andExpect(jsonPath("$.price").value(expectedPrice))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void shouldReturnNotFoundWhenNoPriceApplies() throws Exception {
        mockMvc.perform(get(PRICES_URL)
                        .param("applicationDate", "2019-01-01T10:00:00")
                        .param("productId", PRODUCT_ID)
                        .param("brandId", BRAND_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Price not found"));
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        mockMvc.perform(get(PRICES_URL)
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "99999")
                        .param("brandId", BRAND_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenParameterIsMissing() throws Exception {
        mockMvc.perform(get(PRICES_URL)
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", PRODUCT_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Missing parameter"));
    }

    @Test
    void shouldReturnBadRequestWhenDateFormatIsInvalid() throws Exception {
        mockMvc.perform(get(PRICES_URL)
                        .param("applicationDate", "2020-06-14-10.00.00")
                        .param("productId", PRODUCT_ID)
                        .param("brandId", BRAND_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid parameter"));
    }

    @Test
    void shouldReturnBadRequestWhenIdentifierIsNotPositive() throws Exception {
        mockMvc.perform(get(PRICES_URL)
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "-1")
                        .param("brandId", BRAND_ID))
                .andExpect(status().isBadRequest());
    }
}
