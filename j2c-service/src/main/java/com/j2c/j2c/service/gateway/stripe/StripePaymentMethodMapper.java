package com.j2c.j2c.service.gateway.stripe;

import com.j2c.j2c.service.dto.CardDTO;
import com.stripe.model.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
abstract class StripePaymentMethodMapper {

    @Mapping(source = "card.brand", target = "brand")
    @Mapping(source = "card.last4", target = "last4")
    abstract CardDTO toCardDTO(final PaymentMethod method);

}
