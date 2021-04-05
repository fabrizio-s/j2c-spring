package com.j2c.j2c.service.domain.shipping;

import com.j2c.j2c.domain.entity.ShippingCountry;
import com.j2c.j2c.domain.entity.ShippingZone;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class UpdateShippingZoneResult {

    @NonNull
    private final ShippingZone updatedZone;

    @NonNull
    private final List<ShippingCountry> addedCountries;

    @NonNull
    private final List<ShippingCountry> removedCountries;

}
