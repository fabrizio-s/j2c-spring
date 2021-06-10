package com.j2c.j2c.service.dto;

import com.j2c.j2c.domain.enums.OrderStatus;
import com.neovisionaries.i18n.CurrencyCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "93")
    private final Long id;

    @Schema(example = "8")
    private final Long customerId;

    @Schema(example = "yonah89@example.com")
    private final String email;

    @Schema(example = "PROCESSING")
    private final OrderStatus status;

    @Schema(example = "USD")
    private final CurrencyCode currency;

    @Schema(example = "1799")
    private final Long capturedAmount;

    @Schema(example = "ABC123")
    private final String paymentId;

    @Schema(example = "127.0.0.1")
    private final String ipAddress;

    private final AddressDTO address;

    private final ShippingMethodDetailsDTO shippingMethodDetails;

    private final AddressDTO shippingAddress;

    private final LocalDateTime createdAt;

    @Schema(hidden = true)
    private final OrderFulfillmentDTO fulfillment;

    @Schema(hidden = true)
    private final List<OrderLineDTO> lines;

    @Schema(hidden = true)
    private final List<OrderLineDTO> updatedLines;

    public static class OrderDTOBuilder implements DTOBuilder<OrderDTO> {}

}
