package com.j2c.j2c.service.domain.shipping;

import com.j2c.j2c.domain.entity.ShippingCountry;
import com.j2c.j2c.domain.entity.ShippingZone;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateShippingZoneResult {

    @NonNull
    private final ShippingZone createdShippingZone;

    @NonNull
    private final List<ShippingCountry> countries;

}
