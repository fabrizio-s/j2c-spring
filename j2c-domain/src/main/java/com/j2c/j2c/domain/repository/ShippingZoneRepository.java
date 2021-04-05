package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.ShippingZone;
import com.j2c.j2c.domain.repository.spring.ShippingCountrySDJRepository;
import com.j2c.j2c.domain.repository.spring.ShippingZoneSDJRepository;
import org.springframework.stereotype.Repository;

import static com.j2c.j2c.domain.util.J2cUtils.optional;

@Repository
public class ShippingZoneRepository
        extends BaseRepository<ShippingZone, Long> {

    private final ShippingCountrySDJRepository countryRepository;

    protected ShippingZoneRepository(
            final ShippingZoneSDJRepository repository,
            final ShippingCountrySDJRepository countryRepository
    ) {
        super(ShippingZone.class, repository);
        this.countryRepository = countryRepository;
    }

    @Override
    public void remove(final ShippingZone zone) {
        optional(zone)
                .map(ShippingZone::getId)
                .ifPresent(countryRepository::dereferenceShippingZone);
        super.remove(zone);
    }

}
