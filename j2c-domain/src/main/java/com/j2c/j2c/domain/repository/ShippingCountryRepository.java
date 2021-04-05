package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.exception.EntityDoesNotExistException;
import com.j2c.j2c.domain.exception.NullQueryParameterException;
import com.j2c.j2c.domain.entity.ShippingCountry;
import com.j2c.j2c.domain.repository.spring.ShippingCountrySDJRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.neovisionaries.i18n.CountryCode;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.j2c.j2c.domain.util.J2cUtils.containsNull;

@Repository
public class ShippingCountryRepository
        extends BaseRepository<ShippingCountry, Long> {

    private final ShippingCountrySDJRepository repository;

    protected ShippingCountryRepository(final ShippingCountrySDJRepository repository) {
        super(ShippingCountry.class, repository);
        this.repository = repository;
    }

    public ShippingCountry findByCode(@NonNull final CountryCode code) {
        return repository.findByCode(code)
                .orElseThrow(() -> new EntityDoesNotExistException(String.format("Shipping Country with code '%s' does not exist", code), type));
    }

    public Page<ShippingCountry> findAllUnused(final Pageable pageable) {
        return repository.findAllUnused(pageable);
    }

    public Page<ShippingCountry> findAllByZoneId(@NonNull final Long zoneId, final Pageable pageable) {
        return repository.findAllByZoneId(zoneId, pageable);
    }

    public List<ShippingCountry> findByCountryCodes(@NonNull final Set<CountryCode> codes) {
        if (codes.isEmpty()) {
            return Collections.emptyList();
        } else if (containsNull(codes)) {
            throw new NullQueryParameterException("List of country codes must not contain null", type);
        }
        return repository.findAllByCodeIn(codes);
    }

}
