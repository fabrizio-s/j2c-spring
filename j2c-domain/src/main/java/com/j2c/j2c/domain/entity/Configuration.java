package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.enums.MassUnit;
import com.j2c.j2c.domain.enums.Profile;
import com.neovisionaries.i18n.CurrencyCode;
import lombok.Getter;

import javax.persistence.*;

import static com.j2c.j2c.domain.entity.MaxLengths.*;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

@javax.persistence.Entity
@Table(name = "configuration")
public class Configuration extends BaseEntity<Long> {

    @Id
    @Getter
    @Column(name = "id")
    private Long id;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "profile", nullable = false, updatable = false, unique = true,
            length = CONFIGURATION_PROFILE_MAXLENGTH)
    private Profile profile;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false,
            length = CONFIGURATION_CURRENCY_MAXLENGTH)
    private CurrencyCode currency;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "mass_unit", nullable = false,
            length = CONFIGURATION_MASSUNIT_MAXLENGTH)
    private MassUnit massUnit;

    public void setCurrency(final CurrencyCode currency) {
        this.currency = assertNotNull(currency, "currency");
    }

    public void setMassUnit(final MassUnit massUnit) {
        this.massUnit = assertNotNull(massUnit, "massUnit");
    }

}
