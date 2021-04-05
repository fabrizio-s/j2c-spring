package com.j2c.j2c.service.domain.order;

import com.j2c.j2c.domain.entity.Order;
import com.j2c.j2c.domain.entity.OrderLine;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeleteFulfillmentResult {

    @NonNull
    private final Order order;

    @NonNull
    private final List<OrderLine> updatedLines;

}
