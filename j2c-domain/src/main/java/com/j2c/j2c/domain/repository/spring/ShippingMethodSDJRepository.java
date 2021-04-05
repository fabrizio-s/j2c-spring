package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.ShippingMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShippingMethodSDJRepository
        extends JpaRepository<ShippingMethod, Long> {

    Page<ShippingMethod> findAllByZoneId(Long zoneId, Pageable pageable);

    @Query("SELECT T FROM ShippingMethod T WHERE T.zone.id = :zoneId AND "
            + "((T.type = 'Price' AND T.min <= :price AND T.max >= :price) "
            + "OR (T.type = 'Weight' AND T.min <= :totalMass AND T.max >= :totalMass))")
    Page<ShippingMethod> findAllInZoneByPriceOrWeight(Long zoneId, Long price, Long totalMass, Pageable pageable);

}
