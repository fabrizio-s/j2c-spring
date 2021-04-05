package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.ShippingCountry;
import com.neovisionaries.i18n.CountryCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ShippingCountrySDJRepository
        extends JpaRepository<ShippingCountry, Long> {

    Optional<ShippingCountry> findByCode(CountryCode code);

    @Query("SELECT T FROM ShippingCountry T WHERE T.zone = null")
    Page<ShippingCountry> findAllUnused(Pageable pageable);

    Page<ShippingCountry> findAllByZoneId(Long shippingZoneId, Pageable pageable);

    List<ShippingCountry> findAllByCodeIn(Set<CountryCode> codes);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE ShippingCountry T SET T.zone = null WHERE T.zone.id = :zoneId")
    void dereferenceShippingZone(Long zoneId);

}
