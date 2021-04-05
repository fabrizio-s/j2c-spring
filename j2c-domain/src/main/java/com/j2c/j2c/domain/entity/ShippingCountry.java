package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.exception.DomainException;
import com.neovisionaries.i18n.CountryCode;
import lombok.*;

import javax.persistence.*;

import java.util.Objects;

import static com.j2c.j2c.domain.entity.MaxLengths.COUNTRY_CODE_MAXLENGTH;
import static com.j2c.j2c.domain.exception.DomainErrorMessages.COUNTRY_ALREADY_BELONGS_TO_ZONE;

@javax.persistence.Entity
@Table(name = "shippingcountry",
        indexes = @Index(columnList = "zone_id")
)
public class ShippingCountry extends BaseEntity<Long> {

    @Id
    @Getter
    @Column(name = "id")
    private Long id;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, updatable = false, unique = true,
            length = COUNTRY_CODE_MAXLENGTH)
    private CountryCode code;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id",
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private ShippingZone zone;

    @SuppressWarnings("unused")
    ShippingCountry() {}

    public void setZone(final ShippingZone zone) {
        if (zone != null && alreadyBelongsToAZone()) {
            throw new DomainException(String.format(COUNTRY_ALREADY_BELONGS_TO_ZONE, code, this.zone.getId()), this);
        }
        this.zone = zone;
    }

    public boolean belongsToZone(final ShippingZone zone) {
        if (zone == null) {
            return false;
        }
        return zone.isSameAs(this.zone);
    }

    private boolean alreadyBelongsToAZone() {
        return zone != null;
    }

    @Override
    public String toString() {
        return "ShippingCountry(" +
                "code=" + code +
                ')';
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ShippingCountry that = (ShippingCountry) o;
        return Objects.equals(code, that.code);
    }

}
