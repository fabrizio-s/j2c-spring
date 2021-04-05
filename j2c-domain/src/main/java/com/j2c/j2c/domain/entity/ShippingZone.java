package com.j2c.j2c.domain.entity;

import com.neovisionaries.i18n.CountryCode;
import lombok.*;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.j2c.j2c.domain.entity.MaxLengths.SHIPPINGZONE_NAME_MAXLENGTH;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

@javax.persistence.Entity
@Table(name = "shippingzone")
public class ShippingZone extends BaseEntity<Long> {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @Column(name = "name", nullable = false,
            length = SHIPPINGZONE_NAME_MAXLENGTH)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "zone",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.REFRESH, CascadeType.DETACH})
    private List<ShippingCountry> countries = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "zone", orphanRemoval = true)
    private List<ShippingMethod> methods;

    @SuppressWarnings("unused")
    ShippingZone() {}

    @Builder
    private ShippingZone(final String name) {
        this.name = assertNotNull(name, "name");
    }

    public List<ShippingCountry> addCountries(final List<ShippingCountry> countriesToAdd) {
        if (countriesToAdd == null) {
            return Collections.emptyList();
        }
        return countriesToAdd.stream()
                .filter(Objects::nonNull)
                .map(this::addCountry)
                .collect(Collectors.toList());
    }

    public List<ShippingCountry> removeCountries(final List<ShippingCountry> countriesToRemove) {
        if (countriesToRemove == null) {
            return Collections.emptyList();
        }
        return countriesToRemove.stream()
                .filter(Objects::nonNull)
                .map(this::removeCountry)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public boolean hasCountry(final CountryCode code) {
        if (code == null) {
            return false;
        }
        return countries.stream()
                .map(ShippingCountry::getCode)
                .anyMatch(c -> c.equals(code));
    }

    public void setName(final String name) {
        this.name = assertNotNull(name, "name");
    }

    private ShippingCountry addCountry(final ShippingCountry countryToAdd) {
        countryToAdd.setZone(this);
        countries.add(countryToAdd);
        return countryToAdd;
    }

    private ShippingCountry removeCountry(final ShippingCountry countryToRemove) {
        if (!countryToRemove.belongsToZone(this)) {
            return null;
        }
        countries.remove(countryToRemove);
        countryToRemove.setZone(null);
        return countryToRemove;
    }

}
