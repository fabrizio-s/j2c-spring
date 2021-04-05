package com.j2c.j2c.service.domain.checkout;

import com.j2c.j2c.domain.entity.Order;
import com.j2c.j2c.domain.entity.OrderLine;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompleteCheckoutResult {

    @NonNull
    private final Order createdOrder;

    @NonNull
    private final List<OrderLine> createdLines;

}
