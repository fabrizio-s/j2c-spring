package com.j2c.j2c.service.application;

import com.j2c.j2c.service.dto.OrderDTO;
import com.j2c.j2c.service.dto.OrderFulfillmentDTO;
import com.j2c.j2c.service.dto.OrderFulfillmentLineDTO;
import com.j2c.j2c.service.dto.OrderLineDTO;
import com.j2c.j2c.service.input.CompleteOrderFulfillmentForm;
import com.j2c.j2c.service.input.Line;
import com.j2c.j2c.service.input.UpdateOrderFulfillmentTrackingNumberForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.*;
import java.util.List;
import java.util.Set;

public interface OrderService {

    OrderDTO find(@NotNull Long orderId);

    Page<OrderDTO> findAll(@NotNull Pageable pageable);

    Page<OrderLineDTO> findLines(@NotNull Long orderId, @NotNull Pageable pageable);

    OrderLineDTO findLine(@NotNull Long orderId, @NotNull Long lineId);

    Page<OrderFulfillmentDTO> findFulfillments(@NotNull Long orderId, @NotNull Pageable pageable);

    OrderFulfillmentDTO findFulfillment(@NotNull Long orderId, @NotNull Long fulfillmentId);

    Page<OrderFulfillmentLineDTO> findFulfillmentLines(
            @NotNull Long orderId,
            @NotNull Long fulfillmentId,
            @NotNull Pageable pageable
    );

    OrderFulfillmentLineDTO findFulfillmentLine(
            @NotNull Long orderId,
            @NotNull Long fulfillmentId,
            @NotNull Long fulfillmentLineId
    );

    OrderDTO confirm(Long orderId);

    OrderDTO createFulfillment(Long orderId, List<Line> lines);

    OrderDTO addFulfillmentLines(Long orderId, Long fulfillmentId, List<Line> lines);

    OrderDTO updateFulfillmentLineQuantities(Long orderId, Long fulfillmentId, List<Line> lines);

    OrderDTO deleteFulfillmentLines(Long orderId, Long fulfillmentId, Set<Long> lineIds);

    OrderDTO completeFulfillment(Long orderId, Long fulfillmentId, CompleteOrderFulfillmentForm form);

    OrderFulfillmentDTO updateTrackingNumber(Long orderId, Long fulfillmentId, UpdateOrderFulfillmentTrackingNumberForm form);

    OrderDTO deleteFulfillment(Long orderId, Long fulfillmentId);

    OrderDTO fulfill(Long orderId);

    OrderDTO undoFulfill(Long orderId);

    OrderDTO cancel(Long orderId);

    OrderDTO reinstate(Long orderId);

    // TODO: Allow customer to update some details, as long as the Order hasn't been already confirmed

}
