package com.j2c.j2c.service.gateway.stripe;

import com.j2c.j2c.domain.enums.PaymentMethodType;
import com.j2c.j2c.service.exception.GatewayException;
import com.j2c.j2c.service.dto.CardDTO;
import com.j2c.j2c.service.dto.PaymentMethodDTO;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentMethodListParams;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class StripeSupportedPaymentMethods {

    private static final StripePaymentMethodMapper paymentMethodMapper = Mappers.getMapper(StripePaymentMethodMapper.class);
    private static final Map<PaymentMethodType, SupportedPaymentMethod> supportedPaymentMethods = new EnumMap<>(
            Map.of(
                    PaymentMethodType.Card, SupportedPaymentMethod.Card
            )
    );

    PaymentMethodListParams.Type getType(final PaymentMethodType type) {
        final SupportedPaymentMethod supportedPaymentMethod = getSupportedPaymentMethod(type);
        return supportedPaymentMethod.getStripePaymentMethodType();
    }

    Function<PaymentMethod, ? extends PaymentMethodDTO> getMapping(final PaymentMethodType type) {
        final SupportedPaymentMethod supportedPaymentMethod = getSupportedPaymentMethod(type);
        return supportedPaymentMethod.getMapping();
    }

    List<String> getAllSupportedTypes() {
        return supportedPaymentMethods.values().stream()
                .map(SupportedPaymentMethod::getStripePaymentMethodType)
                .map(PaymentMethodListParams.Type::getValue)
                .collect(Collectors.toList());
    }

    private static SupportedPaymentMethod getSupportedPaymentMethod(@NonNull final PaymentMethodType type) {
        final SupportedPaymentMethod supportedPaymentMethod = supportedPaymentMethods.get(type);
        if (supportedPaymentMethod == null) {
            throw new GatewayException("Unsupported payment method type: " + type);
        }
        return supportedPaymentMethod;
    }

    @Getter
    private enum SupportedPaymentMethod {
        Card(CardDTO.class, PaymentMethodListParams.Type.CARD, paymentMethodMapper::toCardDTO);

        private final Class<? extends PaymentMethodDTO> dtoType;
        private final PaymentMethodListParams.Type stripePaymentMethodType;
        private final Function<PaymentMethod, ? extends PaymentMethodDTO> mapping;

        SupportedPaymentMethod(
                @NonNull final Class<? extends PaymentMethodDTO> dtoClass,
                @NonNull final PaymentMethodListParams.Type stripePaymentMethodType,
                @NonNull final Function<PaymentMethod, ? extends PaymentMethodDTO> mapping
        ) {
            this.dtoType = dtoClass;
            this.stripePaymentMethodType = stripePaymentMethodType;
            this.mapping = mapping;
        }

    }

}
