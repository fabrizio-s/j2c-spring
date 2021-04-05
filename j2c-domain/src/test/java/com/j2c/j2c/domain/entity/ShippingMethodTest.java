package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.enums.ShippingMethodType;
import com.j2c.j2c.domain.test.MockEntity;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.domain.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;

class ShippingMethodTest {

    @Test
    void new_NullName_ShouldThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ShippingMethod.builder()
                        .name(null)
                        .type(ShippingMethodType.Price)
                        .min(50L)
                        .max(150L)
                        .rate(300L)
                        .zone(
                                MockEntity.shippingZone()
                                        .build()
                        )
                        .build()
        );
    }

    @Test
    void new_NullType_ShouldThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ShippingMethod.builder()
                        .name("awdawd")
                        .type(null)
                        .min(50L)
                        .max(150L)
                        .rate(300L)
                        .zone(
                                MockEntity.shippingZone()
                                        .build()
                        )
                        .build()
        );
    }

    @Test
    void new_NullMin_ShouldThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ShippingMethod.builder()
                        .name("awdawd")
                        .type(ShippingMethodType.Price)
                        .min(null)
                        .max(150L)
                        .rate(300L)
                        .zone(
                                MockEntity.shippingZone()
                                        .build()
                        )
                        .build()
        );
    }

    @Test
    void new_NullMax_ShouldThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ShippingMethod.builder()
                        .name("awdawd")
                        .type(ShippingMethodType.Price)
                        .min(50L)
                        .max(null)
                        .rate(300L)
                        .zone(
                                MockEntity.shippingZone()
                                        .build()
                        )
                        .build()
        );
    }

    @Test
    void new_NullRate_ShouldThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ShippingMethod.builder()
                        .name("awdawd")
                        .type(ShippingMethodType.Price)
                        .min(50L)
                        .max(150L)
                        .rate(null)
                        .zone(
                                MockEntity.shippingZone()
                                        .build()
                        )
                        .build()
        );
    }

    @Test
    void new_NullZone_ShouldThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ShippingMethod.builder()
                        .name("awdawd")
                        .type(ShippingMethodType.Price)
                        .min(50L)
                        .max(150L)
                        .rate(300L)
                        .zone(null)
                        .build()
        );
    }

    @Test
    void canBeAppliedToCheckout_CheckoutHasNoShippingAddress_ShouldReturnFalse() {
        final ShippingMethod shippingMethod = MockEntity.shippingMethod().build();

        final Checkout checkout = MockEntity.checkout()
                .nullShippingAddress(true)
                .nullAddress(true)
                .build();

        assertFalse(shippingMethod.canBeAppliedToCheckout(checkout));
    }

    @Test
    void canBeAppliedToCheckout_CheckoutDoesNotRequireShipping_ShouldReturnFalse() {
        final ShippingMethod shippingMethod = MockEntity.shippingMethod().build();

        final Checkout checkout = MockEntity.checkout()
                .shippingRequired(false)
                .build();

        assertFalse(shippingMethod.canBeAppliedToCheckout(checkout));
    }

    @Test
    void canBeAppliedToCheckout_CheckoutShippingAddressCountryDoesNotBelongToZone_ShouldReturnFalse() {
        final ShippingMethod shippingMethod = MockEntity.shippingMethod()
                .type(ShippingMethodType.Price)
                .min(Long.MIN_VALUE)
                .max(Long.MAX_VALUE)
                .build();

        final Checkout checkout = MockEntity.checkout()
                .shippingRequired(true)
                .usesSingleAddress(false)
                .shippingAddress(nextObject(Address.class))
                .build();

        assertFalse(shippingMethod.canBeAppliedToCheckout(checkout));
    }

    @Test
    void canBeAppliedToCheckout_CheckoutParameterIsOutOfBound_ShouldReturnFalse() {
        final ShippingMethod shippingMethod = MockEntity.shippingMethod()
                .type(ShippingMethodType.Price)
                .min(Long.MAX_VALUE)
                .max(Long.MAX_VALUE)
                .build();

        final Checkout checkout = MockEntity.checkout()
                .price(50L)
                .shippingRequired(true)
                .usesSingleAddress(false)
                .shippingAddress(
                        nextObject(Address.class).copy()
                                .country(
                                        MockEntity.shippingCountry()
                                                .zone(shippingMethod.getZone())
                                                .build()
                                                .getCode()
                                )
                                .build()
                )
                .build();

        assertFalse(shippingMethod.canBeAppliedToCheckout(checkout));
    }

    @Test
    void canBeAppliedToCheckout_HappyPath_ShouldReturnTrue() {
        final ShippingMethod shippingMethod = MockEntity.shippingMethod()
                .type(ShippingMethodType.Price)
                .min(Long.MIN_VALUE)
                .max(Long.MAX_VALUE)
                .build();

        final Checkout checkout = MockEntity.checkout()
                .price(50L)
                .shippingRequired(true)
                .usesSingleAddress(false)
                .shippingAddress(
                        nextObject(Address.class).copy()
                                .country(
                                        MockEntity.shippingCountry()
                                                .zone(shippingMethod.getZone())
                                                .build()
                                                .getCode()
                                )
                                .build()
                )
                .build();

        assertTrue(shippingMethod.canBeAppliedToCheckout(checkout));
    }

    @Test
    void setName_Null_ShouldThrowIllegalArgumentException() {
        final ShippingMethod shippingMethod = MockEntity.shippingMethod().build();

        assertThrows(
                IllegalArgumentException.class,
                () -> shippingMethod.setName(null)
        );
    }

    @Test
    void setMin_Null_ShouldThrowIllegalArgumentException() {
        final ShippingMethod shippingMethod = MockEntity.shippingMethod().build();

        assertThrows(
                IllegalArgumentException.class,
                () -> shippingMethod.setMin(null)
        );
    }

    @Test
    void setMax_Null_ShouldThrowIllegalArgumentException() {
        final ShippingMethod shippingMethod = MockEntity.shippingMethod().build();

        assertThrows(
                IllegalArgumentException.class,
                () -> shippingMethod.setMax(null)
        );
    }

    @Test
    void setRate_Null_ShouldThrowIllegalArgumentException() {
        final ShippingMethod shippingMethod = MockEntity.shippingMethod().build();

        assertThrows(
                IllegalArgumentException.class,
                () -> shippingMethod.setRate(null)
        );
    }

}
