package com.j2c.j2c.service.domain.checkout;

import com.j2c.j2c.domain.entity.*;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateCheckoutResult {

    @NonNull
    private final Checkout createdCheckout;

    @NonNull
    private final List<CheckoutLine> createdLines;

}
