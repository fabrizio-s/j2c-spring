package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.exception.DomainException;
import com.j2c.j2c.domain.test.MockEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutLineTest {

    @Test
    void new_NullCheckout_ShouldThrowIllegalArgumentException() {
        final CheckoutLine.CheckoutLineBuilder builder = hpCheckoutLineBuilder()
                .checkout(null);

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                builder::build
        );
        assertEquals("checkout must not be null", exception.getMessage());
    }

    @Test
    void new_NullPreCheckoutLine_ShouldThrowIllegalArgumentException() {
        final CheckoutLine.CheckoutLineBuilder builder = hpCheckoutLineBuilder()
                .preCheckoutLine(null);

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                builder::build
        );
        assertEquals("preCheckoutLine must not be null", exception.getMessage());
    }

    @Test
    void verifyBelongsToCheckout_DoesNotBelongToCheckout_ShouldThrowDomainException() {
        final CheckoutLine checkoutLine = hpCheckoutLineBuilder().build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> checkoutLine.verifyBelongsToCheckout(MockEntity.checkout().build())
        );
        assertTrue(exception.getMessage().contains("does not belong to the checkout"), "Actual: " + exception.getMessage());
    }

    @Test
    void verifyBelongsToCheckout_BelongsToCheckout_ShouldNotThrowAnyExceptions() {
        final CheckoutLine checkoutLine = hpCheckoutLineBuilder().build();

        assertDoesNotThrow(() -> checkoutLine.verifyBelongsToCheckout(checkoutLine.getCheckout()));
    }

    @Test
    void belongsToCheckout_DoesNotBelongToCheckout_ShouldReturnFalse() {
        final CheckoutLine checkoutLine = hpCheckoutLineBuilder().build();

        assertFalse(checkoutLine.belongsToCheckout(MockEntity.checkout().build()));
    }

    @Test
    void belongsToCheckout_BelongsToCheckout_ShouldReturnTrue() {
        final CheckoutLine checkoutLine = hpCheckoutLineBuilder().build();

        assertTrue(checkoutLine.belongsToCheckout(checkoutLine.getCheckout()));
    }

    private static CheckoutLine.CheckoutLineBuilder hpCheckoutLineBuilder() {
        return CheckoutLine.builder()
                .checkout(
                        MockEntity.checkout()
                                .build()
                )
                .preCheckoutLine(
                        Checkout.PreCheckoutLine.builder()
                                .variant(
                                        MockEntity.productVariant()
                                                .price(50L)
                                                .build()
                                )
                                .quantity(1)
                                .build()
                );
    }

}
