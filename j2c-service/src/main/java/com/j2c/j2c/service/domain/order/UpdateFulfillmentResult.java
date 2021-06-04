package com.j2c.j2c.service.domain.order;

import com.j2c.j2c.domain.entity.Order;
import com.j2c.j2c.domain.entity.OrderFulfillment;
import com.j2c.j2c.domain.entity.OrderFulfillmentLine;
import com.j2c.j2c.domain.entity.OrderLine;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class UpdateFulfillmentResult {

    @NonNull
    private final Order order;

    @NonNull
    private final List<OrderLine> updatedOrderLines;

    @NonNull
    private final OrderFulfillment fulfillment;

    @NonNull
    private final List<OrderFulfillmentLine> addedFulfillmentLines;

    @NonNull
    private final List<OrderFulfillmentLine> updatedFulfillmentLines;

}
