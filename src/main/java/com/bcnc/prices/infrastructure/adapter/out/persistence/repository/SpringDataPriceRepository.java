package com.bcnc.prices.infrastructure.adapter.out.persistence.repository;

import com.bcnc.prices.infrastructure.adapter.out.persistence.entity.PriceEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SpringDataPriceRepository extends JpaRepository<PriceEntity, Long> {

    /**
     * Tarifas vigentes ordenadas por relevancia: mayor prioridad primero y, como
     * desempate determinista, la de inicio más reciente. Se invoca con un
     * {@link Pageable} de tamaño 1 para que la base de datos devuelva una sola fila.
     */
    @Query("""
            SELECT p FROM PriceEntity p
            WHERE p.brandId = :brandId
              AND p.productId = :productId
              AND :applicationDate BETWEEN p.startDate AND p.endDate
            ORDER BY p.priority DESC, p.startDate DESC
            """)
    List<PriceEntity> findApplicablePrices(@Param("brandId") Long brandId,
                                           @Param("productId") Long productId,
                                           @Param("applicationDate") LocalDateTime applicationDate,
                                           Pageable pageable);
}
