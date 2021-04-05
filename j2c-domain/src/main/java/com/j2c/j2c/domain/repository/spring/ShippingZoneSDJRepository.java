package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.ShippingZone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingZoneSDJRepository
        extends JpaRepository<ShippingZone, Long> {
}
