package com.bcnc.prices.infrastructure.config;

import com.bcnc.prices.application.service.GetApplicablePriceService;
import com.bcnc.prices.domain.port.in.GetApplicablePriceUseCase;
import com.bcnc.prices.domain.port.out.PriceRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra los casos de uso como beans, manteniendo dominio y aplicación
 * libres de dependencias del framework.
 */
@Configuration
public class BeanConfiguration {

    @Bean
    public GetApplicablePriceUseCase getApplicablePriceUseCase(PriceRepositoryPort priceRepositoryPort) {
        return new GetApplicablePriceService(priceRepositoryPort);
    }
}
