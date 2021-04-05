package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.enums.MassUnit;
import com.j2c.j2c.domain.enums.ShippingMethodType;
import com.j2c.j2c.domain.exception.DomainException;
import com.j2c.j2c.domain.test.MockEntity;
import com.neovisionaries.i18n.CurrencyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.j2c.j2c.domain.exception.DomainErrorMessages.*;
import static com.j2c.j2c.domain.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckoutTest {

    private MockEntity.MockProductBuilder productBuilder;
    private MockEntity.MockProductVariantBuilder variantBuilder;
    private Checkout.CheckoutBuilder checkoutBuilder;

    @BeforeEach
    void setUp() {
        productBuilder = MockEntity.product()
                .published(true)
                .digital(false);
        variantBuilder = MockEntity.productVariant()
                .product(productBuilder.build());
        checkoutBuilder = Checkout.builder()
                .customer(MockEntity.user().build())
                .email("test@example.com")
                .currency(CurrencyCode.EUR)
                .massUnit(MassUnit.g)
                .ipAddress("127.0.0.1");
    }

    @Test
    void new_PositivePrice_ShouldSum() {
        final long price1 = 397L;
        final int quantity1 = 3;
        final long price2 = 521L;
        final int quantity2 = 2;

        final List<Checkout.PreCheckoutLine> lines = List.of(
                Checkout.PreCheckoutLine.builder()
                        .variant(variantBuilder
                                .price(price1)
                                .build())
                        .quantity(quantity1)
                        .build(),
                Checkout.PreCheckoutLine.builder()
                        .variant(variantBuilder
                                .price(price2)
                                .build())
                        .quantity(quantity2)
                        .build()
        );

        final Checkout checkout = checkoutBuilder
                .lines(lines)
                .build();

        assertEquals(price1 * quantity1 + price2 * quantity2, checkout.getPrice());
    }

    @Test
    void new_AnyNonPositivePrice_ShouldIgnore() {
        final long price1 = 397L;
        final int quantity1 = 3;
        final long price2 = -521L;
        final int quantity2 = 2;

        final List<Checkout.PreCheckoutLine> lines = List.of(
                Checkout.PreCheckoutLine.builder()
                        .variant(variantBuilder
                                .price(price1)
                                .build())
                        .quantity(quantity1)
                        .build(),
                Checkout.PreCheckoutLine.builder()
                        .variant(variantBuilder
                                .price(price2)
                                .build())
                        .quantity(quantity2)
                        .build()
        );

        final Checkout checkout = checkoutBuilder
                .lines(lines)
                .build();

        assertEquals(price1 * quantity1, checkout.getPrice());
    }

    @Test
    void new_PositiveMass_ShouldSum() {
        final int mass1 = 745;
        final int mass2 = 2123;

        final List<Checkout.PreCheckoutLine> lines = List.of(
                Checkout.PreCheckoutLine.builder()
                        .variant(variantBuilder
                                .mass(mass1)
                                .build())
                        .quantity(1)
                        .build(),
                Checkout.PreCheckoutLine.builder()
                        .variant(variantBuilder
                                .mass(mass2)
                                .build())
                        .quantity(1)
                        .build()
        );

        final Checkout checkout = checkoutBuilder
                .lines(lines)
                .build();

        assertEquals(mass1 + mass2, checkout.getTotalMass());
    }

    @Test
    void new_AnyNonPositivePositiveMass_ShouldIgnore() {
        final int mass1 = 745;
        final int mass2 = -2123;

        final List<Checkout.PreCheckoutLine> lines = List.of(
                Checkout.PreCheckoutLine.builder()
                        .variant(variantBuilder
                                .mass(mass1)
                                .build())
                        .quantity(1)
                        .build(),
                Checkout.PreCheckoutLine.builder()
                        .variant(variantBuilder
                                .mass(mass2)
                                .build())
                        .quantity(1)
                        .build()
        );

        final Checkout checkout = checkoutBuilder
                .lines(lines)
                .build();

        assertEquals(mass1, checkout.getTotalMass());
    }

    @Test
    void new_AnyProductIsPhysical_ShouldRequireShipping() {
        final boolean digital1 = true;
        final boolean digital2 = true;
        final boolean digital3 = false;

        final List<Checkout.PreCheckoutLine> lines = List.of(
                Checkout.PreCheckoutLine.builder()
                        .variant(variantBuilder
                                .product(productBuilder
                                        .digital(digital1)
                                        .build())
                                .build())
                        .quantity(1)
                        .build(),
                Checkout.PreCheckoutLine.builder()
                        .variant(variantBuilder
                                .product(productBuilder
                                        .digital(digital2)
                                        .build())
                                .build())
                        .quantity(1)
                        .build(),
                Checkout.PreCheckoutLine.builder()
                        .variant(variantBuilder
                                .product(productBuilder
                                        .digital(digital3)
                                        .build())
                                .build())
                        .quantity(1)
                        .build()
        );

        final Checkout checkout = checkoutBuilder
                .lines(lines)
                .build();

        assertTrue(checkout.isShippingRequired());
    }

    @Test
    void new_AllProductsArePhysical_ShouldRequireShipping() {
        final boolean digital1 = false;
        final boolean digital2 = false;
        final boolean digital3 = false;

        final List<Checkout.PreCheckoutLine> lines = List.of(
                Checkout.PreCheckoutLine.builder()
                        .variant(variantBuilder
                                .product(productBuilder
                                        .digital(digital1)
                                        .build())
                                .build())
                        .quantity(1)
                        .build(),
                Checkout.PreCheckoutLine.builder()
                        .variant(variantBuilder
                                .product(productBuilder
                                        .digital(digital2)
                                        .build())
                                .build())
                        .quantity(1)
                        .build(),
                Checkout.PreCheckoutLine.builder()
                        .variant(variantBuilder
                                .product(productBuilder
                                        .digital(digital3)
                                        .build())
                                .build())
                        .quantity(1)
                        .build()
        );

        final Checkout checkout = checkoutBuilder
                .lines(lines)
                .build();

        assertTrue(checkout.isShippingRequired());
    }

    @Test
    void new_AllProductsAreDigital_ShouldNotRequireShipping() {
        final boolean digital1 = true;
        final boolean digital2 = true;
        final boolean digital3 = true;

        final List<Checkout.PreCheckoutLine> lines = List.of(
                Checkout.PreCheckoutLine.builder()
                        .variant(variantBuilder
                                .product(productBuilder
                                        .digital(digital1)
                                        .build())
                                .build())
                        .quantity(1)
                        .build(),
                Checkout.PreCheckoutLine.builder()
                        .variant(variantBuilder
                                .product(productBuilder
                                        .digital(digital2)
                                        .build())
                                .build())
                        .quantity(1)
                        .build(),
                Checkout.PreCheckoutLine.builder()
                        .variant(variantBuilder
                                .product(productBuilder
                                        .digital(digital3)
                                        .build())
                                .build())
                        .quantity(1)
                        .build()
        );

        final Checkout checkout = checkoutBuilder
                .lines(lines)
                .build();

        assertFalse(checkout.isShippingRequired());
    }

    @Test
    void hasPayment_HasPaymentDetails_ShouldReturnTrue() {
        final Checkout checkout = MockEntity.checkout()
                .paymentDetails(
                        MockEntity.paymentDetails()
                                .build()
                )
                .build();

        assertTrue(checkout.hasPayment());
    }

    @Test
    void hasPayment_DoesNotHavePaymentDetails_ShouldReturnFalse() {
        final Checkout checkout = MockEntity.checkout()
                .nullPaymentDetails(true)
                .build();

        assertFalse(checkout.hasPayment());
    }

    @Test
    void setPayment_NullPayment_ShouldThrowIllegalArgumentException() {
        final Checkout checkout = MockEntity.checkout()
                .build();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> checkout.setPayment(null)
        );
        assertEquals("payment must not be null", exception.getMessage());
    }

    @Test
    void addShippingAddress_NullShippingAddress_ShouldThrowDomainException() {
        final Checkout checkout = MockEntity.checkout()
                .shippingRequired(true)
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> checkout.addShippingAddress(null)
        );
        assertTrue(
                exception.getMessage().matches(String.format(CHECKOUT_NULL_ADDRESS, ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void addShippingAddress_ShippingIsNotRequired_ShouldThrowDomainException() {
        final Checkout checkout = MockEntity.checkout()
                .shippingRequired(false)
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> checkout.addShippingAddress(nextObject(Address.class))
        );
        assertTrue(
                exception.getMessage().matches(String.format(CHECKOUT_NO_SHIPPING_REQUIRED, ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void addShippingAddress_UsesSingleAddress_ShouldThrowDomainException() {
        final Checkout checkout = MockEntity.checkout()
                .shippingRequired(true)
                .usesSingleAddress(true)
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> checkout.addShippingAddress(nextObject(Address.class))
        );
        assertTrue(
                exception.getMessage().matches(String.format(CHECKOUT_USES_SINGLE_ADDRESS, ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void addShippingAddress_HappyPath_ShouldThrowDomainException() {
        final Checkout checkout = MockEntity.checkout()
                .shippingRequired(true)
                .usesSingleAddress(false)
                .build();

        final Address address = nextObject(Address.class);

        checkout.addShippingAddress(address);

        assertEquals(checkout.getShippingAddress(), address);
        assertNull(checkout.getShippingMethodDetails());
    }

    @Test
    void setShippingAddress_NullShippingAddress_ShouldThrowDomainException() {
        final Checkout checkout = MockEntity.checkout()
                .shippingRequired(true)
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> checkout.setShippingAddress(null)
        );
        assertTrue(
                exception.getMessage().matches(String.format(CHECKOUT_NULL_ADDRESS, ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void setShippingAddress_ShippingIsNotRequired_ShouldThrowDomainException() {
        final Checkout checkout = MockEntity.checkout()
                .shippingRequired(false)
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> checkout.setShippingAddress(nextObject(Address.class))
        );
        assertTrue(
                exception.getMessage().matches(String.format(CHECKOUT_NO_SHIPPING_REQUIRED, ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void setShippingAddress_UsesSingleAddress_ShouldThrowDomainException() {
        final Checkout checkout = MockEntity.checkout()
                .shippingRequired(true)
                .usesSingleAddress(true)
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> checkout.setShippingAddress(nextObject(Address.class))
        );
        assertTrue(
                exception.getMessage().matches(String.format(CHECKOUT_USES_SINGLE_ADDRESS, ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void setShippingAddress_HappyPath_ShouldThrowDomainException() {
        final Checkout checkout = MockEntity.checkout()
                .shippingRequired(true)
                .usesSingleAddress(false)
                .build();

        final Address address = nextObject(Address.class);

        checkout.setShippingAddress(address);

        assertEquals(checkout.getShippingAddress(), address);
        assertNull(checkout.getShippingMethodDetails());
    }

    @Test
    void addAddress_NullAddress_ShouldThrowDomainException() {
        final Checkout checkout = MockEntity.checkout().build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> checkout.addAddress(null)
        );
        assertTrue(
                exception.getMessage().matches(String.format(CHECKOUT_NULL_ADDRESS, ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void addAddress_HappyPath_ShouldThrowDomainException() {
        final Checkout checkout = MockEntity.checkout().build();

        final Address address = nextObject(Address.class);

        checkout.addAddress(address);

        assertEquals(checkout.getAddress(), address);
        assertNull(checkout.getShippingMethodDetails());
    }

    @Test
    void setAddress_NullAddress_ShouldThrowDomainException() {
        final Checkout checkout = MockEntity.checkout().build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> checkout.setAddress(null)
        );
        assertTrue(
                exception.getMessage().matches(String.format(CHECKOUT_NULL_ADDRESS, ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void setAddress_HappyPath_ShouldThrowDomainException() {
        final Checkout checkout = MockEntity.checkout().build();

        final Address address = nextObject(Address.class);

        checkout.setAddress(address);

        assertEquals(checkout.getAddress(), address);
        assertNull(checkout.getShippingMethodDetails());
    }

    @Test
    void setShippingMethod_NullShippingMethod_ShouldThrowIllegalArgumentException() {
        final Checkout checkout = MockEntity.checkout()
                .shippingRequired(true)
                .build();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> checkout.setShippingMethod(null)
        );
        assertEquals("shippingMethod must not be null", exception.getMessage());
    }

    @Test
    void setShippingMethod_ShippingIsNotRequired_ShouldThrowDomainException() {
        final Checkout checkout = MockEntity.checkout()
                .shippingRequired(false)
                .build();

        final ShippingMethod shippingMethod = mockShippingMethodForCheckout(checkout).build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> checkout.setShippingMethod(shippingMethod)
        );
        assertTrue(
                exception.getMessage().matches(String.format(CHECKOUT_NO_SHIPPING_REQUIRED, ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void setShippingMethod_ShippingMethodCannotBeAppliedToCheckout_ShouldThrowDomainException() {
        final Checkout checkout = MockEntity.checkout()
                .price(50L)
                .shippingRequired(true)
                .build();

        final ShippingMethod shippingMethod = mockShippingMethodForCheckout(checkout)
                .min(Long.MAX_VALUE)
                .max(Long.MAX_VALUE)
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> checkout.setShippingMethod(shippingMethod)
        );
        assertTrue(
                exception.getMessage().matches(String.format(CHECKOUT_INVALID_SHIPPINGMETHOD, ".+", ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void setShippingMethod_HappyPath_ShouldSetShippingMethodDetailsFromShippingMethod() {
        final Checkout checkout = MockEntity.checkout()
                .shippingRequired(true)
                .build();

        final ShippingMethod shippingMethod = mockShippingMethodForCheckout(checkout).build();

        checkout.setShippingMethod(shippingMethod);

        assertNotNull(checkout.getShippingMethodDetails());
        assertEquals(shippingMethod.getName(), checkout.getShippingMethodDetails().getName());
        assertEquals(shippingMethod.getType(), checkout.getShippingMethodDetails().getType());
        assertEquals(shippingMethod.getRate(), checkout.getShippingMethodDetails().getAmount());
    }

    @Test
    void useSingleAddress_TrueAndHasShippingAddress_ShouldSetAddressToShippingAddress() {
        final Checkout checkout = MockEntity.checkout().build();

        final Address shippingAddress = checkout.getShippingAddress();

        assertNotNull(shippingAddress);

        checkout.useSingleAddress(true);

        assertEquals(shippingAddress, checkout.getAddress());
        assertNull(checkout.getShippingAddress());
    }

    @Test
    void complete_NullPayment_ShouldThrowIllegalArgumentException() {
        final Checkout checkout = MockEntity.checkout().build();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> checkout.complete(null)
        );
        assertEquals("payment must not be null", exception.getMessage());
    }

    @Test
    void complete_WrongPayment_ShouldThrowDomainException() {
        final Checkout checkout = MockEntity.checkout()
                .paymentDetails(
                        MockEntity.paymentDetails()
                                .id("ABC123")
                                .build()
                )
                .build();

        final Payment payment = MockEntity.payment()
                .id("DEF456")
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> checkout.complete(payment)
        );
        assertTrue(
                exception.getMessage().matches(String.format(CHECKOUT_WRONG_PAYMENT, ".+", ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void complete_MissingAddress_ShouldThrowDomainException() {
        final Checkout checkout = MockEntity.checkout()
                .usesSingleAddress(false)
                .nullAddress(true)
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> checkout.complete(
                        MockEntity.payment()
                                .id(checkout.getPaymentId())
                                .build()
                )
        );
        assertTrue(
                exception.getMessage().matches(String.format(CHECKOUT_MISSING_DETAILS, ".+", ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void complete_ShippingRequiredAndMissingShippingAddress_ShouldThrowDomainException() {
        final Checkout checkout = MockEntity.checkout()
                .usesSingleAddress(false)
                .shippingRequired(true)
                .nullShippingAddress(true)
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> checkout.complete(
                        MockEntity.payment()
                                .id(checkout.getPaymentId())
                                .build()
                )
        );
        assertTrue(
                exception.getMessage().matches(String.format(CHECKOUT_MISSING_DETAILS, ".+", ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void complete_ShippingRequiredAndMissingShippingMethod_ShouldThrowDomainException() {
        final Checkout checkout = MockEntity.checkout()
                .usesSingleAddress(false)
                .shippingRequired(true)
                .nullShippingMethodDetails(true)
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> checkout.complete(
                        MockEntity.payment()
                                .id(checkout.getPaymentId())
                                .build()
                )
        );
        assertTrue(
                exception.getMessage().matches(String.format(CHECKOUT_MISSING_DETAILS, ".+", ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void complete_HappyPath_ShouldCapturePayment() {
        final Checkout checkout = MockEntity.checkout()
                .usesSingleAddress(false)
                .shippingRequired(true)
                .build();

        final Payment payment = spy(
                MockEntity.payment()
                        .id(checkout.getPaymentId())
                        .build()
        );

        final Order order = checkout.complete(payment);

        assertNotNull(order);
        verify(payment, times(1))
                .capture();
    }

    @Test
    void complete_SaveCustomerAddresses_ShouldAddCustomerAddresses() {
        final User customer = spy(
                MockEntity.user()
                        .build()
        );

        final Checkout checkout = MockEntity.checkout()
                .usesSingleAddress(false)
                .saveCustomerAddresses(true)
                .addressCreated(true)
                .shippingAddressCreated(true)
                .customer(customer)
                .build();

        final Address address = checkout.getAddress();

        final Address shippingAddress = checkout.getShippingAddress();

        checkout.complete(
                MockEntity.payment()
                        .id(checkout.getPaymentId())
                        .build()
        );

        verify(customer, times(1))
                .addAddress(address);
        verify(customer, times(1))
                .addAddress(shippingAddress);
    }

    @Test
    void complete_SavePaymentMethodAsDefault_ShouldSetCustomerDefaultPaymentMethod() {
        final User customer = spy(
                MockEntity.user()
                        .build()
        );

        final Checkout checkout = MockEntity.checkout()
                .savePaymentMethodAsDefault(true)
                .customer(customer)
                .build();

        final Payment payment = MockEntity.payment()
                .id(checkout.getPaymentId())
                .build();

        checkout.complete(payment);

        verify(customer, times(1))
                .setDefaultPaymentMethodId(payment.getPaymentMethodId());
    }

    @Test
    void getTotalPrice_HappyPath_ShouldReturnSumOfPriceAndShippingMethodAmount() {
        final Checkout checkout = MockEntity.checkout()
                .shippingRequired(true)
                .price(12345L)
                .shippingMethodDetails(
                        ShippingMethodDetails.builder()
                                .type(ShippingMethodType.Price)
                                .name("test")
                                .amount(654L)
                                .build()
                )
                .build();

        assertEquals(checkout.getPrice() + checkout.getShippingMethodDetails().getAmount(), checkout.getTotalPrice());
    }

    @Test
    void getLines_HappyPath_ShouldReturnImmutableCopy() {
        final Checkout checkout = MockEntity.checkout().build();

        assertThrows(
                UnsupportedOperationException.class,
                () -> checkout.getLines().add(
                        MockEntity.checkoutLine()
                                .build()
                )
        );
    }

    @Test
    void isReadyForPurchase_MissingAddress_ShouldReturnFalse() {
        final Checkout checkout = MockEntity.checkout()
                .usesSingleAddress(false)
                .nullAddress(true)
                .build();

        assertFalse(checkout.isReadyForPurchase());
    }

    @Test
    void isReadyForPurchase_ShippingRequiredAndMissingShippingAddress_ShouldReturnFalse() {
        final Checkout checkout = MockEntity.checkout()
                .usesSingleAddress(false)
                .shippingRequired(true)
                .nullShippingAddress(true)
                .build();

        assertFalse(checkout.isReadyForPurchase());
    }

    @Test
    void isReadyForPurchase_ShippingRequiredAndMissingShippingMethod_ShouldReturnFalse() {
        final Checkout checkout = MockEntity.checkout()
                .usesSingleAddress(false)
                .shippingRequired(true)
                .nullShippingMethodDetails(true)
                .build();

        assertFalse(checkout.isReadyForPurchase());
    }

    @Test
    void isReadyForPurchase_HappyPath_ShouldReturnTrue() {
        final Checkout checkout = MockEntity.checkout().build();

        assertTrue(checkout.isReadyForPurchase());
    }

    @Test
    void getPaymentId_NullPaymentDetails_ShouldReturnNull() {
        final Checkout checkout = MockEntity.checkout()
                .nullPaymentDetails(true)
                .build();

        assertNull(checkout.getPaymentId());
    }

    @Test
    void setSavePaymentMethodAsDefault_Null_ShouldThrowIllegalArgumentException() {
        final Checkout checkout = MockEntity.checkout().build();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> checkout.setSavePaymentMethodAsDefault(null)
        );
        assertEquals("savePaymentMethodAsDefault must not be null", exception.getMessage());
    }

    @Test
    void setSaveCustomerAddresses_Null_ShouldThrowIllegalArgumentException() {
        final Checkout checkout = MockEntity.checkout().build();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> checkout.setSaveCustomerAddresses(null)
        );
        assertEquals("saveCustomerAddresses must not be null", exception.getMessage());
    }

    @Test
    void getActualShippingAddress_UsesSingleAddressIsTrue_ShouldReturnAddress() {
        final Checkout checkout = MockEntity.checkout()
                .usesSingleAddress(true)
                .build();

        assertEquals(checkout.getAddress(), checkout.getActualShippingAddress());
    }

    @Test
    void getActualShippingAddress_UsesSingleAddressIsFalse_ShouldReturnShippingAddress() {
        final Checkout checkout = MockEntity.checkout()
                .usesSingleAddress(false)
                .build();

        assertEquals(checkout.getShippingAddress(), checkout.getActualShippingAddress());
    }

    private static MockEntity.MockShippingMethodBuilder mockShippingMethodForCheckout(final Checkout checkout) {
        final Address address = checkout.getActualShippingAddress();
        final ShippingZone zone = MockEntity.shippingZone().build();
        MockEntity.shippingCountry()
                .zone(zone)
                .code(address.getCountry())
                .build();
        return MockEntity.shippingMethod()
                .zone(zone)
                .type(ShippingMethodType.Price)
                .min(Long.MIN_VALUE)
                .max(Long.MAX_VALUE)
                .rate(50L);
    }

}
