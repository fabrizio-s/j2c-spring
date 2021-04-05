package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.Checkout;
import com.j2c.j2c.domain.entity.CheckoutLine;
import com.j2c.j2c.domain.entity.PaymentDetails;
import com.j2c.j2c.service.domain.checkout.CreateCheckoutResult;
import com.j2c.j2c.service.domain.checkout.CompleteCheckoutResult;
import com.j2c.j2c.service.dto.CheckoutDTO;
import com.j2c.j2c.service.dto.CheckoutDTO.CheckoutDTOBuilder;
import com.j2c.j2c.service.dto.CheckoutLineDTO;
import com.j2c.j2c.service.dto.OrderDTO;
import com.j2c.j2c.service.dto.OrderDTO.OrderDTOBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import static com.j2c.j2c.domain.util.J2cUtils.optional;

@Component
@RequiredArgsConstructor
public class CheckoutServiceMapper {

    private final CheckoutDTOMapper checkoutDTOMapper;
    private final CheckoutLineDTOMapper checkoutLineDTOMapper;
    private final OrderDTOMapper orderDTOMapper;
    private final OrderLineDTOMapper orderLineDTOMapper;

    public CheckoutDTO toCheckoutDTO(@NonNull final Checkout checkout) {
        return toCheckoutDTOWithToken(checkout).build();
    }

    public Page<CheckoutDTO> toCheckoutDTO(final Page<Checkout> checkouts) {
        return checkoutDTOMapper.fromEntities(checkouts);
    }

    public CheckoutDTO toCheckoutDTO(@NonNull final CreateCheckoutResult result) {
        final CheckoutDTOBuilder builder = toCheckoutDTOWithToken(result.getCreatedCheckout());
        builder.lines(checkoutLineDTOMapper.fromEntities(result.getCreatedLines()));
        return builder.build();
    }

    public CheckoutLineDTO toLineDTO(@NonNull final CheckoutLine line) {
        return checkoutLineDTOMapper.fromEntity(line).build();
    }

    public Page<CheckoutLineDTO> toLineDTO(final Page<CheckoutLine> lines) {
        return checkoutLineDTOMapper.fromEntities(lines);
    }

    public OrderDTO toOrderDTO(@NonNull final CompleteCheckoutResult result) {
        final OrderDTOBuilder builder = orderDTOMapper.fromEntity(result.getCreatedOrder());
        builder.lines(orderLineDTOMapper.fromEntities(result.getCreatedLines()));
        return builder.build();
    }

    private CheckoutDTOBuilder toCheckoutDTOWithToken(final Checkout checkout) {
        final CheckoutDTOBuilder builder = checkoutDTOMapper.fromEntity(checkout);
        if (builder != null) {
            optional(checkout)
                    .filter(Checkout::isReadyForPurchase)
                    .map(Checkout::getPaymentDetails)
                    .map(PaymentDetails::getToken)
                    .ifPresent(builder::paymentToken);
        }
        return builder;
    }

}
