package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.ShippingMethod;
import com.j2c.j2c.domain.repository.spring.ShippingMethodSDJRepository;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ShippingMethodRepository
        extends BaseRepository<ShippingMethod, Long> {

    private final ShippingMethodSDJRepository repository;

    protected ShippingMethodRepository(final ShippingMethodSDJRepository repository) {
        super(ShippingMethod.class, repository);
        this.repository = repository;
    }

    public Page<ShippingMethod> findAllByZoneId(@NonNull final Long zoneId, final Pageable pageable) {
        return repository.findAllByZoneId(zoneId, pageable);
    }

    @Builder(builderClassName = "ShippingMethodPriceOrTotalMassWithinRangeFinder",
            builderMethodName = "findAllByZoneIdAndPriceOrTotalMassWithinRange",
            buildMethodName = "find")
    private Page<ShippingMethod> findInZoneAndHavingPriceOrTotalMassWithinRange(
            @NonNull final Long zoneId,
            @NonNull final Long price,
            @NonNull final Integer totalMass,
            final Pageable pageable
    ) {
        return repository.findAllInZoneByPriceOrWeight(zoneId, price, Long.valueOf(totalMass), pageable);
    }

}
