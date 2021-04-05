package com.j2c.j2c.service.application.impl;

import com.j2c.j2c.domain.entity.*;
import com.j2c.j2c.domain.enums.MassUnit;
import com.j2c.j2c.domain.enums.ShippingMethodType;
import com.j2c.j2c.service.dto.CheckoutDTO;
import com.j2c.j2c.service.dto.OrderDTO;
import com.j2c.j2c.service.exception.InvalidInputException;
import com.j2c.j2c.service.exception.ResourceAlreadyExistsException;
import com.j2c.j2c.service.exception.ResourceNotFoundException;
import com.j2c.j2c.service.exception.ServiceException;
import com.j2c.j2c.service.input.*;
import com.j2c.j2c.service.input.CreateAddressForm.CreateAddressFormBuilder;
import com.j2c.j2c.service.input.CreateCheckoutAddressForm.CreateCheckoutAddressFormBuilder;
import com.j2c.j2c.service.input.CreateCheckoutForm.CreateCheckoutFormBuilder;
import com.j2c.j2c.service.input.CreateCheckoutShippingAddressForm.CreateCheckoutShippingAddressFormBuilder;
import com.j2c.j2c.service.input.SetCheckoutAddressForm.SetCheckoutAddressFormBuilder;
import com.j2c.j2c.service.input.SetCheckoutShippingAddressForm.SetCheckoutShippingAddressFormBuilder;
import com.j2c.j2c.service.input.SetCheckoutShippingMethodForm.SetCheckoutShippingMethodFormBuilder;
import com.j2c.j2c.service.input.UpdateAddressForm.UpdateAddressFormBuilder;
import com.j2c.j2c.service.input.UpdateCheckoutAddressForm.UpdateCheckoutAddressFormBuilder;
import com.j2c.j2c.service.input.UpdateCheckoutShippingAddressForm.UpdateCheckoutShippingAddressFormBuilder;
import com.j2c.j2c.service.input.UseSingleAddressForm.UseSingleAddressFormBuilder;
import com.j2c.j2c.service.test.*;
import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.CurrencyCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.j2c.j2c.domain.exception.DomainErrorMessages.*;
import static com.j2c.j2c.service.exception.J2cServiceErrorMessages.RESOURCES_NOT_FOUND;
import static com.j2c.j2c.service.exception.J2cServiceErrorMessages.RESOURCE_NOT_FOUND;
import static com.j2c.j2c.service.test.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class CheckoutServiceImplTest extends BaseServiceTest {

    @Autowired
    private CheckoutServiceImpl service;

    @Autowired
    private CheckoutServiceStubber stubber;

    @Autowired
    private MockBeanProvider mockBeanProvider;

    @Test
    public void checkout_NullForm_ShouldThrowInvalidInputException() {
        final Long customerId = 1L;
        final String ipAddress = "127.0.0.1";

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.checkout(customerId, ipAddress, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void checkout_NullCustomerId_ShouldThrowInvalidInputException() {
        final String ipAddress = "127.0.0.1";
        final CreateCheckoutForm form = hpCreateCheckoutForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.checkout(null, ipAddress, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("customer id must not be null"))
        );
    }

    @Test
    public void checkout_NullIpAddress_ShouldThrowInvalidInputException() {
        final Long customerId = 1L;
        final CreateCheckoutForm form = hpCreateCheckoutForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.checkout(customerId, null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("ip address must not be blank"))
        );
    }

    @Test
    public void checkout_BlankIpAddress_ShouldThrowInvalidInputException() {
        final Long customerId = 1L;
        final String ipAddress = "    ";
        final CreateCheckoutForm form = hpCreateCheckoutForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.checkout(customerId, ipAddress, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("ip address must not be blank"))
        );
    }

    @Test
    public void checkout_NullEmail_ShouldThrowInvalidInputException() {
        final Long customerId = 1L;
        final String ipAddress = "127.0.0.1";
        final CreateCheckoutForm form = hpCreateCheckoutForm()
                .email(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.checkout(customerId, ipAddress, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("email must not be blank"))
        );
    }

    @Test
    public void checkout_BlankEmail_ShouldThrowInvalidInputException() {
        final Long customerId = 1L;
        final String ipAddress = "127.0.0.1";
        final CreateCheckoutForm form = hpCreateCheckoutForm()
                .email("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.checkout(customerId, ipAddress, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("email must not be blank"))
        );
    }

    @Test
    public void checkout_InvalidEmail_ShouldThrowInvalidInputException() {
        final Long customerId = 1L;
        final String ipAddress = "127.0.0.1";
        final CreateCheckoutForm form = hpCreateCheckoutForm()
                .email("invalid email !")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.checkout(customerId, ipAddress, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("email must be a well-formed email address"))
        );
    }

    @Test
    public void checkout_NullLines_ShouldThrowInvalidInputException() {
        final Long customerId = 1L;
        final String ipAddress = "127.0.0.1";
        final CreateCheckoutForm form = hpCreateCheckoutForm()
                .lines(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.checkout(customerId, ipAddress, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("lines must not be empty"))
        );
    }

    @Test
    public void checkout_EmptyLines_ShouldThrowInvalidInputException() {
        final Long customerId = 1L;
        final String ipAddress = "127.0.0.1";
        final CreateCheckoutForm form = hpCreateCheckoutForm()
                .lines(Collections.emptyList())
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.checkout(customerId, ipAddress, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("lines must not be empty"))
        );
    }

    @Test
    public void checkout_AnyLineIsNull_ShouldThrowInvalidInputException() {
        final Long customerId = 1L;
        final String ipAddress = "127.0.0.1";
        final CreateCheckoutForm form = hpCreateCheckoutForm().build();

        form.getLines().add(null);

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.checkout(customerId, ipAddress, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("lines must not contain null elements"))
        );
    }

    @Test
    public void checkout_AnyLineIdIsNull_ShouldThrowInvalidInputException() {
        final Long customerId = 1L;
        final String ipAddress = "127.0.0.1";
        final CreateCheckoutForm form = hpCreateCheckoutForm().build();

        form.getLines().add(Line.builder().id(null).quantity(1).build());

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.checkout(customerId, ipAddress, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("id must not be null"))
        );
    }

    @Test
    public void checkout_AnyLineQuantityIsNull_ShouldThrowInvalidInputException() {
        final Long customerId = 1L;
        final String ipAddress = "127.0.0.1";
        final CreateCheckoutForm form = hpCreateCheckoutForm().build();

        form.getLines().add(Line.builder().id(1L).quantity(null).build());

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.checkout(customerId, ipAddress, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("quantity must not be null"))
        );
    }

    @Test
    public void checkout_AnyLineQuantityIsNonPositive_ShouldThrowInvalidInputException() {
        final Long customerId = 1L;
        final String ipAddress = "127.0.0.1";
        final CreateCheckoutForm form = hpCreateCheckoutForm().build();

        form.getLines().add(Line.builder().id(1L).quantity(-1).build());

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.checkout(customerId, ipAddress, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("quantity must be greater than 0"))
        );
    }

    @Test
    public void checkout_CustomerDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long customerId = 1L;
        final String ipAddress = "127.0.0.1";
        final CreateCheckoutForm form = hpCreateCheckoutForm().build();

        stubber.checkout()
                .variants(physicalProducts(form.getLines()))
                .configuration(defaultConfiguration())
                .payment(Payment.free())
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.checkout(customerId, ipAddress, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "user", customerId)));
    }

    @Test
    public void checkout_CheckoutForCustomerAlreadyExists_ShouldThrowResourceAlreadyExistsException() {
        final Long customerId = 1L;
        final String ipAddress = "127.0.0.1";
        final CreateCheckoutForm form = hpCreateCheckoutForm().build();

        final User customer = userWithId(customerId);
        stubber.checkout()
                .customer(customer)
                .checkout(MockEntity.checkout().id(customerId).customer(customer).build())
                .variants(physicalProducts(form.getLines()))
                .configuration(defaultConfiguration())
                .payment(Payment.free())
                .stub();

        final ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.checkout(customerId, ipAddress, form)
        );
        assertTrue(exception.getMessage().matches(String.format(CHECKOUT_ALREADY_EXISTS, customerId)));
    }

    @Test
    public void checkout_AnyProductVariantDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long customerId = 1L;
        final String ipAddress = "127.0.0.1";
        final CreateCheckoutForm form = hpCreateCheckoutForm().build();

        stubber.checkout()
                .customer(userWithId(customerId))
                .variants(removeFirst(physicalProducts(form.getLines())))
                .configuration(defaultConfiguration())
                .payment(Payment.free())
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.checkout(customerId, ipAddress, form)
        );
        assertTrue(exception.getMessage().matches(String.format(RESOURCES_NOT_FOUND, "product variant", "[0-9]+")));
    }

    @Test
    public void checkout_DefaultConfigurationDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long customerId = 1L;
        final String ipAddress = "127.0.0.1";
        final CreateCheckoutForm form = hpCreateCheckoutForm().build();

        stubber.checkout()
                .customer(userWithId(customerId))
                .variants(physicalProducts(form.getLines()))
                .payment(Payment.free())
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.checkout(customerId, ipAddress, form)
        );
        assertEquals("No default configuration exists", exception.getMessage());
    }

    @Test
    public void checkout_AnyProductIsNotPublished_ShouldThrowServiceException() {
        final Long customerId = 1L;
        final String ipAddress = "127.0.0.1";
        final CreateCheckoutForm form = hpCreateCheckoutForm().build();

        stubber.checkout()
                .customer(userWithId(customerId))
                .variants(transformFirst(physicalProducts(form.getLines()), CheckoutServiceImplTest::setPublished))
                .configuration(defaultConfiguration())
                .payment(Payment.free())
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.checkout(customerId, ipAddress, form)
        );
        assertTrue(exception.getMessage().matches(String.format(CANNOT_CHECKOUT_UNPUBLISHED, "[0-9]+")));
    }

    @Test
    public void checkout_ShippingRequiredAndNullMassUnit_ShouldReturnCreatedCheckoutAndLines() {
        final Long customerId = 1L;
        final String ipAddress = "127.0.0.1";
        final CreateCheckoutForm form = hpCreateCheckoutForm().build();

        stubber.checkout()
                .customer(userWithId(customerId))
                .variants(physicalProducts(form.getLines()))
                .configuration(
                        MockEntity.configuration()
                                .currency(CurrencyCode.EUR)
                                .nullMassUnit(true)
                                .build()
                )
                .payment(Payment.free())
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.checkout(customerId, ipAddress, form)
        );
        assertEquals(CHECKOUT_MISSING_MASS_UNIT, exception.getMessage());
    }

    @Test
    public void checkout_HappyPath_ShouldReturnCreatedCheckoutAndLines() {
        final Long customerId = 1L;
        final String ipAddress = "127.0.0.1";
        final CreateCheckoutForm form = hpCreateCheckoutForm().build();

        stubber.checkout()
                .customer(userWithId(customerId))
                .variants(physicalProducts(form.getLines()))
                .configuration(defaultConfiguration())
                .payment(Payment.free())
                .stub();

        final CheckoutDTO checkoutDTO = service.checkout(customerId, ipAddress, form);

        assertNotNull(checkoutDTO);
        assertEquals(customerId, checkoutDTO.getCustomerId());
        assertEquals(form.getEmail(), checkoutDTO.getEmail());
        assertEquals(ipAddress, checkoutDTO.getIpAddress());
        assertEquals(form.getLines().size(), checkoutDTO.getLines().size());

        verify(mockBeanProvider.getCheckoutRepository(), times(1))
                .save(any(Checkout.class));
    }

    @Test
    public void checkout_ShippingIsNotRequired_ShouldRequestGatewayPayment() {
        final Long customerId = 1L;
        final String ipAddress = "127.0.0.1";
        final CreateCheckoutForm form = hpCreateCheckoutForm().build();

        stubber.checkout()
                .customer(userWithId(customerId))
                .variants(digitalProducts(form.getLines()))
                .configuration(defaultConfiguration())
                .payment(payment())
                .stub();

        service.checkout(customerId, ipAddress, form);

        verify(mockBeanProvider.getPaymentGateway(), times(1))
                .request(any(Long.class), any(CurrencyCode.class), anyString());
    }

    @Test
    public void createShippingAddress_NullCheckoutId_ShouldThrowInvalidInputException() {
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("checkout id must not be null"))
        );
    }

    @Test
    public void createShippingAddress_NullForm_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void createShippingAddress_NullAddressForm_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("address must not be null"))
        );
    }

    @Test
    public void createShippingAddress_NullFirstName_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .firstName(null)
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("first name must not be blank"))
        );
    }

    @Test
    public void createShippingAddress_BlankFirstName_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .firstName("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("first name must not be blank"))
        );
    }

    @Test
    public void createShippingAddress_FirstNameDoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .firstName("Inv4lid N4m3 !")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("first name must match \".+\""))
        );
    }

    @Test
    public void createShippingAddress_NullLastName_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .lastName(null)
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("last name must not be blank"))
        );
    }

    @Test
    public void createShippingAddress_BlankLastName_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .lastName("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("last name must not be blank"))
        );
    }

    @Test
    public void createShippingAddress_LastNameDoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .lastName("Inv4lid N4m3 !")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("last name must match \".+\""))
        );
    }

    @Test
    public void createShippingAddress_NullStreetAddress1_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .streetAddress1(null)
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("street address 1 must not be blank"))
        );
    }

    @Test
    public void createShippingAddress_BlankStreetAddress1_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .streetAddress1("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("street address 1 must not be blank"))
        );
    }

    @Test
    public void createShippingAddress_BlankStreetAddress2_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .streetAddress2("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("street address 2 must not be blank"))
        );
    }

    @Test
    public void createShippingAddress_NullCountry_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .country(null)
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("country must not be null"))
        );
    }

    @Test
    public void createShippingAddress_NullCountryArea_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .countryArea(null)
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("country area must not be blank"))
        );
    }

    @Test
    public void createShippingAddress_BlankCountryArea_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .countryArea("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("country area must not be blank"))
        );
    }

    @Test
    public void createShippingAddress_NullCity_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .city(null)
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("city must not be blank"))
        );
    }

    @Test
    public void createShippingAddress_BlankCity_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .city("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("city must not be blank"))
        );
    }

    @Test
    public void createShippingAddress_BlankCityArea_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .cityArea("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("city area must not be blank"))
        );
    }

    @Test
    public void createShippingAddress_NullPostalCode_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .postalCode(null)
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("postal code must not be blank"))
        );
    }

    @Test
    public void createShippingAddress_BlankPostalCode_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .postalCode("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("postal code must not be blank"))
        );
    }

    @Test
    public void createShippingAddress_PostalCodeDoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .postalCode("Invalid postal code !%£")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("postal code must match \".+\""))
        );
    }

    @Test
    public void createShippingAddress_NullPhone1_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .phone1(null)
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("phone 1 must not be blank"))
        );
    }

    @Test
    public void createShippingAddress_BlankPhone1_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .phone1("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("phone 1 must not be blank"))
        );
    }

    @Test
    public void createShippingAddress_Phone1DoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .phone1("Invalid phone !")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("phone 1 must match \".+\""))
        );
    }

    @Test
    public void createShippingAddress_BlankPhone2_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .phone2("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("phone 2 must not be blank"))
        );
    }

    @Test
    public void createShippingAddress_Phone2DoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .address(
                        hpCreateAddressForm()
                                .phone2("Invalid phone !")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("phone 2 must match \".+\""))
        );
    }

    @Test
    public void createShippingAddress_CheckoutDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .build();

        stubber.createShippingAddress()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "checkout", checkoutId)));
    }

    @Test
    public void createShippingAddress_CheckoutDoesNotRequireShipping_ShouldThrowServiceException() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .build();

        stubber.createShippingAddress()
                .checkout(
                        MockEntity.checkout()
                                .id(checkoutId)
                                .shippingRequired(false)
                                .build()
                )
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.createShippingAddress(checkoutId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(CHECKOUT_NO_SHIPPING_REQUIRED, checkoutId)));
    }

    @Test
    public void createShippingAddress_HappyPath_ShouldReturnUpdatedCheckout() {
        final Long checkoutId = 1L;
        final CreateCheckoutShippingAddressForm form = hpCreateCheckoutShippingAddressForm()
                .build();

        stubber.createShippingAddress()
                .checkout(checkoutWithId(checkoutId).build())
                .stub();

        final CheckoutDTO checkoutDTO = service.createShippingAddress(checkoutId, form);

        assertNotNull(checkoutDTO);
        assertEquals(form.getSaveCustomerAddresses(), checkoutDTO.getSaveCustomerAddresses());
        assertNotNull(checkoutDTO.getShippingAddress());
        assertNull(checkoutDTO.getShippingMethodDetails());
    }

    @Test
    public void updateShippingAddress_BlankFirstName_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .firstName("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("first name must not be blank"))
        );
    }

    @Test
    public void updateShippingAddress_FirstNameDoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .firstName("Inv4lid N4m3 !")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("first name must match \".+\""))
        );
    }

    @Test
    public void updateShippingAddress_BlankLastName_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .lastName("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("last name must not be blank"))
        );
    }

    @Test
    public void updateShippingAddress_LastNameDoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .lastName("Inv4lid N4m3 !")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("last name must match \".+\""))
        );
    }

    @Test
    public void updateShippingAddress_BlankStreetAddress1_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .streetAddress1("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("street address 1 must not be blank"))
        );
    }

    @Test
    public void updateShippingAddress_BlankStreetAddress2_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .streetAddress2("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("street address 2 must not be blank"))
        );
    }

    @Test
    public void updateShippingAddress_BlankCountryArea_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .countryArea("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("country area must not be blank"))
        );
    }

    @Test
    public void updateShippingAddress_BlankCity_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .city("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("city must not be blank"))
        );
    }

    @Test
    public void updateShippingAddress_BlankCityArea_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .cityArea("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("city area must not be blank"))
        );
    }

    @Test
    public void updateShippingAddress_BlankPostalCode_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .postalCode("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("postal code must not be blank"))
        );
    }

    @Test
    public void updateShippingAddress_PostalCodeDoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .postalCode("Invalid postal code !%£")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("postal code must match \".+\""))
        );
    }

    @Test
    public void updateShippingAddress_BlankPhone1_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .phone1("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("phone 1 must not be blank"))
        );
    }

    @Test
    public void updateShippingAddress_Phone1DoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .phone1("Invalid phone !")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("phone 1 must match \".+\""))
        );
    }

    @Test
    public void updateShippingAddress_BlankPhone2_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .phone2("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("phone 2 must not be blank"))
        );
    }

    @Test
    public void updateShippingAddress_Phone2DoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .phone2("Invalid phone !")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("phone 2 must match \".+\""))
        );
    }

    @Test
    public void updateShippingAddress_CheckoutDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm().build();

        stubber.updateShippingAddress()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateShippingAddress(checkoutId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "checkout", checkoutId)));
    }

    @Test
    public void updateShippingAddress_CheckoutDoesNotRequireShipping_ShouldThrowServiceException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm().build();

        stubber.updateShippingAddress()
                .checkout(
                        MockEntity.checkout()
                                .id(checkoutId)
                                .shippingRequired(false)
                                .build()
                )
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.updateShippingAddress(checkoutId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(CHECKOUT_NO_SHIPPING_REQUIRED, checkoutId)));
    }

    @Test
    public void updateShippingAddress_CheckoutHasNoShippingAddress_ShouldThrowServiceException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm().build();

        stubber.updateShippingAddress()
                .checkout(
                        MockEntity.checkout()
                                .id(checkoutId)
                                .shippingRequired(true)
                                .nullShippingAddress(true)
                                .build()
                )
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.updateShippingAddress(checkoutId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(CHECKOUT_NULL_ADDRESS, checkoutId)));
    }

    @Test
    public void updateShippingAddress_HappyPath_ShouldReturnUpdatedCheckout() {
        final Long checkoutId = 1L;
        final UpdateCheckoutShippingAddressForm form = hpUpdateCheckoutShippingAddressForm().build();

        stubber.updateShippingAddress()
                .checkout(checkoutWithId(checkoutId).build())
                .stub();

        final CheckoutDTO checkoutDTO = service.updateShippingAddress(checkoutId, form);

        assertNotNull(checkoutDTO);
        assertEquals(form.getSaveCustomerAddresses(), checkoutDTO.getSaveCustomerAddresses());
        assertNotNull(checkoutDTO.getShippingAddress());
        assertNull(checkoutDTO.getShippingMethodDetails());
    }

    @Test
    public void setShippingAddress_NullCheckoutId_ShouldThrowInvalidInputException() {
        final SetCheckoutShippingAddressForm form = hpSetCheckoutShippingAddressForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.setShippingAddress(null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("checkout id must not be null"))
        );
    }

    @Test
    public void setShippingAddress_NullForm_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.setShippingAddress(checkoutId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void setShippingAddress_NullAddressId_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final SetCheckoutShippingAddressForm form = hpSetCheckoutShippingAddressForm()
                .addressId(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.setShippingAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("address id must not be null"))
        );
    }

    @Test
    public void setShippingAddress_CheckoutDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long checkoutId = 1L;
        final SetCheckoutShippingAddressForm form = hpSetCheckoutShippingAddressForm().build();

        stubber.setShippingAddress()
                .userAddress(userAddressWithIdForUser(form.getAddressId(), MockEntity.user().build()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.setShippingAddress(checkoutId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "checkout", checkoutId)));
    }

    @Test
    public void setShippingAddress_UserAddressDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long checkoutId = 1L;
        final SetCheckoutShippingAddressForm form = hpSetCheckoutShippingAddressForm().build();

        stubber.setShippingAddress()
                .checkout(checkoutWithId(checkoutId).build())
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.setShippingAddress(checkoutId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "user address", form.getAddressId())));
    }

    @Test
    public void setShippingAddress_AddressDoesNotBelongToCustomer_ShouldThrowServiceException() {
        final Long checkoutId = 1L;
        final SetCheckoutShippingAddressForm form = hpSetCheckoutShippingAddressForm().build();

        final Checkout checkout = checkoutWithId(checkoutId).build();
        stubber.setShippingAddress()
                .checkout(checkout)
                .userAddress(userAddressWithIdForUser(form.getAddressId(), MockEntity.user().build()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.setShippingAddress(checkoutId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(ADDRESS_DOES_NOT_BELONG_TO_USER, form.getAddressId(), checkout.getCustomer().getId())));
    }

    @Test
    public void setShippingAddress_CheckoutDoesNotRequireShipping_ShouldThrowServiceException() {
        final Long checkoutId = 1L;
        final SetCheckoutShippingAddressForm form = hpSetCheckoutShippingAddressForm().build();

        final Checkout checkout = MockEntity.checkout()
                .id(checkoutId)
                .shippingRequired(false)
                .build();
        stubber.setShippingAddress()
                .checkout(checkout)
                .userAddress(userAddressWithIdForUser(form.getAddressId(), checkout.getCustomer()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.setShippingAddress(checkoutId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(CHECKOUT_NO_SHIPPING_REQUIRED, checkoutId)));
    }

    @Test
    public void setShippingAddress_HappyPath_ShouldReturnUpdatedCheckout() {
        final Long checkoutId = 1L;
        final SetCheckoutShippingAddressForm form = hpSetCheckoutShippingAddressForm().build();

        final Checkout checkout = checkoutWithId(checkoutId).build();
        stubber.setShippingAddress()
                .checkout(checkout)
                .userAddress(userAddressWithIdForUser(form.getAddressId(), checkout.getCustomer()))
                .stub();

        final CheckoutDTO checkoutDTO = service.setShippingAddress(checkoutId, form);

        assertNotNull(checkoutDTO);
        assertNotNull(checkoutDTO.getShippingAddress());
        assertNull(checkoutDTO.getShippingMethodDetails());
    }

    @Test
    public void setShippingMethod_NullCheckoutId_ShouldThrowInvalidInputException() {
        final SetCheckoutShippingMethodForm form = hpSetCheckoutShippingMethodForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.setShippingMethod(null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("checkout id must not be null"))
        );
    }

    @Test
    public void setShippingMethod_NullForm_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.setShippingMethod(checkoutId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void setShippingMethod_NullShippingMethodId_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final SetCheckoutShippingMethodForm form = hpSetCheckoutShippingMethodForm()
                .shippingMethodId(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.setShippingMethod(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("shipping method id must not be null"))
        );
    }

    @Test
    public void setShippingMethod_CheckoutDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long checkoutId = 1L;
        final SetCheckoutShippingMethodForm form = hpSetCheckoutShippingMethodForm().build();

        stubber.setShippingMethod()
                .shippingMethod(shippingMethodWithIdForCountry(form.getShippingMethodId(), CountryCode.US))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.setShippingMethod(checkoutId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "checkout", checkoutId)));
    }

    @Test
    public void setShippingMethod_ShippingMethodDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long checkoutId = 1L;
        final SetCheckoutShippingMethodForm form = hpSetCheckoutShippingMethodForm().build();

        stubber.setShippingMethod()
                .checkout(checkoutWithId(checkoutId).build())
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.setShippingMethod(checkoutId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "shipping method", form.getShippingMethodId())));
    }

    @Test
    public void setShippingMethod_CheckoutDoesNotRequireShipping_ShouldThrowServiceException() {
        final Long checkoutId = 1L;
        final SetCheckoutShippingMethodForm form = hpSetCheckoutShippingMethodForm().build();

        final Checkout checkout = checkoutWithId(checkoutId)
                .shippingRequired(false)
                .build();
        stubber.setShippingMethod()
                .checkout(checkout)
                .shippingMethod(shippingMethodWithIdForCountry(form.getShippingMethodId(), checkout.getShippingAddress().getCountry()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.setShippingMethod(checkoutId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(CHECKOUT_NO_SHIPPING_REQUIRED, checkoutId)));
    }

    @Test
    public void setShippingMethod_CheckoutShippingCountryDoesNotBelongToShippingMethodsZone_ShouldThrowServiceException() {
        final Long checkoutId = 1L;
        final SetCheckoutShippingMethodForm form = hpSetCheckoutShippingMethodForm().build();

        final Checkout checkout = checkoutWithId(checkoutId)
                .shippingAddress(addressInCountry(CountryCode.US))
                .build();
        stubber.setShippingMethod()
                .checkout(checkout)
                .shippingMethod(shippingMethodWithIdForCountry(form.getShippingMethodId(), CountryCode.CF))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.setShippingMethod(checkoutId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(CHECKOUT_INVALID_SHIPPINGMETHOD, form.getShippingMethodId(), checkoutId)));
    }

    @Test
    public void setShippingMethod_CheckoutShippingMethodParameterOutOfBoundsForShippingMethodMinAndMax_ShouldThrowServiceException() {
        final Long checkoutId = 1L;
        final SetCheckoutShippingMethodForm form = hpSetCheckoutShippingMethodForm().build();

        final Checkout checkout = checkoutWithId(checkoutId)
                .totalMass(Integer.MAX_VALUE) // ShippingMethodParameter = totalMass because ShippingMethodType is Weight
                .build();
        stubber.setShippingMethod()
                .checkout(checkout)
                .shippingMethod(shippingMethodWithIdForCountry(form.getShippingMethodId(), checkout.getShippingAddress().getCountry()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.setShippingMethod(checkoutId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(CHECKOUT_INVALID_SHIPPINGMETHOD, form.getShippingMethodId(), checkoutId)));
    }

    @Test
    public void setShippingMethod_HappyPath_ShouldReturnUpdatedCheckout() {
        final Long checkoutId = 1L;
        final SetCheckoutShippingMethodForm form = hpSetCheckoutShippingMethodForm().build();

        final Checkout checkout = checkoutWithId(checkoutId).build();
        stubber.setShippingMethod()
                .payment(payment())
                .checkout(checkout)
                .shippingMethod(shippingMethodWithIdForCountry(form.getShippingMethodId(), checkout.getShippingAddress().getCountry()))
                .stub();

        final CheckoutDTO checkoutDTO = service.setShippingMethod(checkoutId, form);

        assertNotNull(checkoutDTO);
        assertNotNull(checkoutDTO.getShippingMethodDetails());

        verify(mockBeanProvider.getPaymentGateway(), times(1))
                .request(any(Long.class), any(CurrencyCode.class), anyString());
    }

    @Test
    public void setShippingMethod_CheckoutAlreadyHasPayment_ShouldUpdatePayment() {
        final Long checkoutId = 1L;
        final SetCheckoutShippingMethodForm form = hpSetCheckoutShippingMethodForm().build();

        final Payment payment = spy(payment());
        final Checkout checkout = checkoutWithId(checkoutId)
                .paymentDetails(paymentDetailsFromPayment(payment))
                .build();
        stubber.setShippingMethod()
                .checkout(checkout)
                .shippingMethod(shippingMethodWithIdForCountry(form.getShippingMethodId(), checkout.getShippingAddress().getCountry()))
                .payment(payment)
                .stub();

        service.setShippingMethod(checkoutId, form);

        verify(payment, times(1))
                .update(checkout);
    }

    @Test
    public void createAddress_NullCheckoutId_ShouldThrowInvalidInputException() {
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("checkout id must not be null"))
        );
    }

    @Test
    public void createAddress_NullForm_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void createAddress_NullAddressForm_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("address must not be null"))
        );
    }

    @Test
    public void createAddress_NullFirstName_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .firstName(null)
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("first name must not be blank"))
        );
    }

    @Test
    public void createAddress_BlankFirstName_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .firstName("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("first name must not be blank"))
        );
    }

    @Test
    public void createAddress_FirstNameDoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .firstName("Inv4lid N4m3 !")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("first name must match \".+\""))
        );
    }

    @Test
    public void createAddress_NullLastName_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .lastName(null)
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("last name must not be blank"))
        );
    }

    @Test
    public void createAddress_BlankLastName_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .lastName("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("last name must not be blank"))
        );
    }

    @Test
    public void createAddress_LastNameDoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .lastName("Inv4lid N4m3 !")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("last name must match \".+\""))
        );
    }

    @Test
    public void createAddress_NullStreetAddress1_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .streetAddress1(null)
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("street address 1 must not be blank"))
        );
    }

    @Test
    public void createAddress_BlankStreetAddress1_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .streetAddress1("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("street address 1 must not be blank"))
        );
    }

    @Test
    public void createAddress_BlankStreetAddress2_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .streetAddress2("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("street address 2 must not be blank"))
        );
    }

    @Test
    public void createAddress_NullCountry_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .country(null)
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("country must not be null"))
        );
    }

    @Test
    public void createAddress_NullCountryArea_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .countryArea(null)
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("country area must not be blank"))
        );
    }

    @Test
    public void createAddress_BlankCountryArea_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .countryArea("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("country area must not be blank"))
        );
    }

    @Test
    public void createAddress_NullCity_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .city(null)
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("city must not be blank"))
        );
    }

    @Test
    public void createAddress_BlankCity_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .city("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("city must not be blank"))
        );
    }

    @Test
    public void createAddress_BlankCityArea_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .cityArea("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("city area must not be blank"))
        );
    }

    @Test
    public void createAddress_NullPostalCode_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .postalCode(null)
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("postal code must not be blank"))
        );
    }

    @Test
    public void createAddress_BlankPostalCode_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .postalCode("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("postal code must not be blank"))
        );
    }

    @Test
    public void createAddress_PostalCodeDoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .postalCode("Invalid postal code !%£")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("postal code must match \".+\""))
        );
    }

    @Test
    public void createAddress_NullPhone1_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .phone1(null)
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("phone 1 must not be blank"))
        );
    }

    @Test
    public void createAddress_BlankPhone1_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .phone1("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("phone 1 must not be blank"))
        );
    }

    @Test
    public void createAddress_Phone1DoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .phone1("Invalid phone !")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("phone 1 must match \".+\""))
        );
    }

    @Test
    public void createAddress_BlankPhone2_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .phone2("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("phone 2 must not be blank"))
        );
    }

    @Test
    public void createAddress_Phone2DoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .address(
                        hpCreateAddressForm()
                                .phone2("Invalid phone !")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("phone 2 must match \".+\""))
        );
    }

    @Test
    public void createAddress_CheckoutDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .build();

        stubber.createAddress()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.createAddress(checkoutId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "checkout", checkoutId)));
    }

    @Test
    public void createAddress_HappyPath_ShouldReturnUpdatedCheckout() {
        final Long checkoutId = 1L;
        final CreateCheckoutAddressForm form = hpCreateCheckoutAddressForm()
                .build();

        stubber.createAddress()
                .checkout(checkoutWithId(checkoutId).build())
                .stub();

        final CheckoutDTO checkoutDTO = service.createAddress(checkoutId, form);

        assertNotNull(checkoutDTO);
        assertNotNull(checkoutDTO.getAddress());
        assertNull(checkoutDTO.getShippingMethodDetails());
    }

    @Test
    public void updateAddress_NullCheckoutId_ShouldThrowInvalidInputException() {
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("checkout id must not be null"))
        );
    }

    @Test
    public void updateAddress_NullForm_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(checkoutId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void updateAddress_BlankFirstName_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .firstName("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("first name must not be blank"))
        );
    }

    @Test
    public void updateAddress_FirstNameDoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .firstName("Inv4lid N4m3 !")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("first name must match \".+\""))
        );
    }

    @Test
    public void updateAddress_BlankLastName_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .lastName("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("last name must not be blank"))
        );
    }

    @Test
    public void updateAddress_LastNameDoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .lastName("Inv4lid N4m3 !")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("last name must match \".+\""))
        );
    }

    @Test
    public void updateAddress_BlankStreetAddress1_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .streetAddress1("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("street address 1 must not be blank"))
        );
    }

    @Test
    public void updateAddress_BlankStreetAddress2_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .streetAddress2("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("street address 2 must not be blank"))
        );
    }

    @Test
    public void updateAddress_BlankCountryArea_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .countryArea("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("country area must not be blank"))
        );
    }

    @Test
    public void updateAddress_BlankCity_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .city("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("city must not be blank"))
        );
    }

    @Test
    public void updateAddress_BlankCityArea_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .cityArea("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("city area must not be blank"))
        );
    }

    @Test
    public void updateAddress_BlankPostalCode_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .postalCode("    ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("postal code must not be blank"))
        );
    }

    @Test
    public void updateAddress_PostalCodeDoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .postalCode("Invalid postal code !%£")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("postal code must match \".+\""))
        );
    }

    @Test
    public void updateAddress_BlankPhone1_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .phone1("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("phone 1 must not be blank"))
        );
    }

    @Test
    public void updateAddress_Phone1DoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .phone1("Invalid phone !")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("phone 1 must match \".+\""))
        );
    }

    @Test
    public void updateAddress_BlankPhone2_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .phone2("   ")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("phone 2 must not be blank"))
        );
    }

    @Test
    public void updateAddress_Phone2DoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm()
                .address(
                        hpUpdateAddressForm()
                                .phone2("Invalid phone !")
                                .build()
                )
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("phone 2 must match \".+\""))
        );
    }

    @Test
    public void updateAddress_CheckoutDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm().build();

        stubber.updateAddress()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateAddress(checkoutId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "checkout", checkoutId)));
    }

    @Test
    public void updateAddress_CheckoutHasNoAddress_ShouldThrowServiceException() {
        final Long checkoutId = 1L;
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm().build();

        stubber.updateAddress()
                .checkout(
                        MockEntity.checkout()
                                .id(checkoutId)
                                .shippingRequired(true)
                                .nullAddress(true)
                                .build()
                )
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.updateAddress(checkoutId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(CHECKOUT_NULL_ADDRESS, checkoutId)));
    }

    @Test
    public void updateAddress_HappyPath_ShouldReturnUpdatedCheckout() {
        final Long checkoutId = 1L;
        final UpdateCheckoutAddressForm form = hpUpdateCheckoutAddressForm().build();

        stubber.updateAddress()
                .checkout(checkoutWithId(checkoutId).build())
                .stub();

        final CheckoutDTO checkoutDTO = service.updateAddress(checkoutId, form);

        assertNotNull(checkoutDTO);
        assertNotNull(checkoutDTO.getAddress());
        assertNull(checkoutDTO.getShippingMethodDetails());
    }

    @Test
    public void setAddress_NullCheckoutId_ShouldThrowInvalidInputException() {
        final SetCheckoutAddressForm form = hpSetCheckoutAddressForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.setAddress(null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("checkout id must not be null"))
        );
    }

    @Test
    public void setAddress_NullForm_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.setAddress(checkoutId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void setAddress_NullAddressId_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final SetCheckoutAddressForm form = hpSetCheckoutAddressForm()
                .addressId(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.setAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("address id must not be null"))
        );
    }

    @Test
    public void setAddress_CheckoutDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long checkoutId = 1L;
        final SetCheckoutAddressForm form = hpSetCheckoutAddressForm().build();

        stubber.setAddress()
                .userAddress(userAddressWithIdForUser(form.getAddressId(), MockEntity.user().build()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.setAddress(checkoutId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "checkout", checkoutId)));
    }

    @Test
    public void setAddress_UserAddressDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long checkoutId = 1L;
        final SetCheckoutAddressForm form = hpSetCheckoutAddressForm().build();

        stubber.setAddress()
                .checkout(checkoutWithId(checkoutId).build())
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.setAddress(checkoutId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "user address", form.getAddressId())));
    }

    @Test
    public void setAddress_AddressDoesNotBelongToCustomer_ShouldThrowServiceException() {
        final Long checkoutId = 1L;
        final SetCheckoutAddressForm form = hpSetCheckoutAddressForm().build();

        final Checkout checkout = checkoutWithId(checkoutId).build();
        stubber.setAddress()
                .checkout(checkout)
                .userAddress(userAddressWithIdForUser(form.getAddressId(), MockEntity.user().build()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.setAddress(checkoutId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(ADDRESS_DOES_NOT_BELONG_TO_USER, form.getAddressId(), checkout.getCustomer().getId())));
    }

    @Test
    public void setAddress_HappyPath_ShouldReturnUpdatedCheckout() {
        final Long checkoutId = 1L;
        final SetCheckoutAddressForm form = hpSetCheckoutAddressForm().build();

        final Checkout checkout = checkoutWithId(checkoutId).build();
        stubber.setAddress()
                .checkout(checkout)
                .userAddress(userAddressWithIdForUser(form.getAddressId(), checkout.getCustomer()))
                .stub();

        final CheckoutDTO checkoutDTO = service.setAddress(checkoutId, form);

        assertNotNull(checkoutDTO);
        assertNotNull(checkoutDTO.getAddress());
        assertNull(checkoutDTO.getShippingMethodDetails());
    }

    @Test
    public void useSingleAddress_NullCheckoutId_ShouldThrowInvalidInputException() {
        final UseSingleAddressForm form = hpUseSingleAddressForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.useSingleAddress(null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("checkout id must not be null"))
        );
    }

    @Test
    public void useSingleAddress_NullForm_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.useSingleAddress(checkoutId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void useSingleAddress_NullUseSingleAddress_ShouldThrowInvalidInputException() {
        final Long checkoutId = 1L;
        final UseSingleAddressForm form = hpUseSingleAddressForm()
                .useSingleAddress(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.useSingleAddress(checkoutId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("use single address must not be null"))
        );
    }

    @Test
    public void useSingleAddress_CheckoutDoesNotExist_ShouldReturnUpdatedCheckout() {
        final Long checkoutId = 1L;
        final UseSingleAddressForm form = hpUseSingleAddressForm().build();

        stubber.useSingleAddress()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.useSingleAddress(checkoutId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "checkout", checkoutId)));
    }

    @Test
    public void useSingleAddress_HappyPath_ShouldReturnUpdatedCheckout() {
        final Long checkoutId = 1L;
        final UseSingleAddressForm form = hpUseSingleAddressForm().build();

        stubber.useSingleAddress()
                .checkout(checkoutWithId(checkoutId).build())
                .stub();

        final CheckoutDTO checkoutDTO = service.useSingleAddress(checkoutId, form);

        assertNotNull(checkoutDTO);
        assertEquals(form.getUseSingleAddress(), checkoutDTO.getUsesSingleAddress());
        assertEquals(form.getSavePaymentMethodAsDefault(), checkoutDTO.getSavePaymentMethodAsDefault());
    }

    @Test
    public void complete_NullCheckoutId_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.complete(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("checkout id must not be null"))
        );
    }

    @Test
    public void complete_CheckoutDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long checkoutId = 1L;

        stubber.complete()
                .payment(payment())
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.complete(checkoutId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "checkout", checkoutId)));
    }

    @Test
    public void complete_PaymentDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long checkoutId = 1L;

        final Checkout checkout = checkoutWithId(checkoutId).build();
        stubber.complete()
                .checkout(checkout)
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.complete(checkoutId)
        );
        assertTrue(exception.getMessage().matches(String.format(RESOURCE_NOT_FOUND, "payment", ".+")));
    }

    @Test
    public void complete_HasNoAddress_ShouldThrowServiceException() {
        final Long checkoutId = 1L;

        final Payment payment = payment();
        final Checkout checkout = checkoutWithId(checkoutId)
                .paymentDetails(paymentDetailsFromPayment(payment))
                .nullAddress(true)
                .build();
        stubber.complete()
                .checkout(checkout)
                .payment(payment)
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.complete(checkoutId)
        );
        assertTrue(exception.getMessage().matches(String.format(CHECKOUT_MISSING_DETAILS, checkoutId, "address")));
    }

    @Test
    public void complete_ShippingIsRequiredAndHasNoShippingAddress_ShouldThrowServiceException() {
        final Long checkoutId = 1L;

        final Payment payment = payment();
        final Checkout checkout = checkoutWithId(checkoutId)
                .paymentDetails(paymentDetailsFromPayment(payment))
                .nullShippingAddress(true)
                .build();
        stubber.complete()
                .checkout(checkout)
                .payment(payment)
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.complete(checkoutId)
        );
        assertTrue(exception.getMessage().matches(String.format(CHECKOUT_MISSING_DETAILS, checkoutId, "shipping address")));
    }

    @Test
    public void complete_ShippingIsRequiredAndHasNoShippingMethod_ShouldThrowServiceException() {
        final Long checkoutId = 1L;

        final Payment payment = payment();
        final Checkout checkout = checkoutWithId(checkoutId)
                .paymentDetails(paymentDetailsFromPayment(payment))
                .nullShippingMethodDetails(true)
                .build();
        stubber.complete()
                .checkout(checkout)
                .payment(payment)
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.complete(checkoutId)
        );
        assertTrue(exception.getMessage().matches(String.format(CHECKOUT_MISSING_DETAILS, checkoutId, "shipping method")));
    }

    @Test
    public void complete_HappyPath_ShouldReturnCreatedOrder() {
        final Long checkoutId = 1L;

        final Payment payment = spy(payment());
        final Checkout checkout = checkoutWithId(checkoutId)
                .paymentDetails(paymentDetailsFromPayment(payment))
                .build();
        stubber.complete()
                .checkout(checkout)
                .payment(payment)
                .stub();

        final OrderDTO orderDTO = service.complete(checkoutId);

        assertNotNull(orderDTO);
        assertNotNull(orderDTO.getLines());

        verify(mockBeanProvider.getOrderRepository(), times(1))
                .save(any(Order.class));
        verify(payment, times(1))
                .capture();
        verify(mockBeanProvider.getCheckoutRepository(), times(1))
                .delete(any(Checkout.class));
    }

    @Test
    public void cancel_NullCheckoutId_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.cancel(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("checkout id must not be null"))
        );
    }

    @Test
    public void cancel_CheckoutDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long checkoutId = 1L;

        stubber.cancel()
                .payment(payment())
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.cancel(checkoutId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "checkout", checkoutId)));
    }

    @Test
    public void cancel_HappyPath_ShouldDeleteCheckout() {
        final Long checkoutId = 1L;

        final Payment payment = spy(payment());
        final Checkout checkout = checkoutWithId(checkoutId)
                .paymentDetails(paymentDetailsFromPayment(payment))
                .build();
        stubber.cancel()
                .checkout(checkout)
                .payment(payment)
                .stub();

        service.cancel(checkoutId);

        verify(payment, times(1))
                .cancel();
        verify(mockBeanProvider.getCheckoutRepository(), times(1))
                .delete(any(Checkout.class));
    }

    @Test
    public void cancel_PaymentDoesNotExist_ShouldIgnore() {
        final Long checkoutId = 1L;

        stubber.cancel()
                .checkout(checkoutWithId(checkoutId).build())
                .stub();

        service.cancel(checkoutId);
    }

    private static SetCheckoutAddressFormBuilder hpSetCheckoutAddressForm() {
        return SetCheckoutAddressForm.builder()
                .addressId(1L)
                .savePaymentMethodAsDefault(true);
    }

    private static CreateCheckoutFormBuilder hpCreateCheckoutForm() {
        return CreateCheckoutForm.builder()
                .email("tester@example.com")
                .lines(
                        new ArrayList<>(
                                Arrays.asList(
                                        Line.builder().id(1L).quantity(3).build(),
                                        Line.builder().id(2L).quantity(2).build(),
                                        Line.builder().id(3L).quantity(4).build()
                                )
                        )
                );
    }

    private static CreateCheckoutShippingAddressFormBuilder hpCreateCheckoutShippingAddressForm() {
        return CreateCheckoutShippingAddressForm.builder()
                .saveCustomerAddresses(true)
                .address(hpCreateAddressForm().build());
    }

    private static CreateCheckoutAddressFormBuilder hpCreateCheckoutAddressForm() {
        return CreateCheckoutAddressForm.builder()
                .savePaymentMethodAsDefault(true)
                .saveCustomerAddresses(true)
                .address(hpCreateAddressForm().build());
    }

    private static CreateAddressFormBuilder hpCreateAddressForm() {
        return CreateAddressForm.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .streetAddress1("Street Address 1")
                .streetAddress2("Street Address 2")
                .country(CountryCode.US)
                .countryArea("Country Area")
                .city("City")
                .cityArea("City Area")
                .postalCode("ABC123")
                .phone1("123456789")
                .phone2("987654321");
    }

    private static UpdateCheckoutShippingAddressFormBuilder hpUpdateCheckoutShippingAddressForm() {
        return UpdateCheckoutShippingAddressForm.builder()
                .saveCustomerAddresses(true)
                .address(hpUpdateAddressForm().build());
    }

    private static UpdateCheckoutAddressFormBuilder hpUpdateCheckoutAddressForm() {
        return UpdateCheckoutAddressForm.builder()
                .saveCustomerAddresses(true)
                .savePaymentMethodAsDefault(true)
                .address(hpUpdateAddressForm().build());
    }

    private static UpdateAddressFormBuilder hpUpdateAddressForm() {
        return UpdateAddressForm.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .streetAddress1("Street Address 1")
                .streetAddress2("Street Address 2")
                .country(CountryCode.US)
                .countryArea("Country Area")
                .city("City")
                .cityArea("City Area")
                .postalCode("ABC123")
                .phone1("123456789")
                .phone2("987654321");
    }

    private static UseSingleAddressFormBuilder hpUseSingleAddressForm() {
        return UseSingleAddressForm.builder()
                .useSingleAddress(true)
                .savePaymentMethodAsDefault(true);
    }

    private static SetCheckoutShippingAddressFormBuilder hpSetCheckoutShippingAddressForm() {
        return SetCheckoutShippingAddressForm.builder()
                .addressId(1L);
    }

    private static SetCheckoutShippingMethodFormBuilder hpSetCheckoutShippingMethodForm() {
        return SetCheckoutShippingMethodForm.builder()
                .shippingMethodId(1L);
    }

    private static User userWithId(final Long id) {
        return MockEntity.user()
                .id(id)
                .build();
    }

    private static MockEntity.MockCheckoutBuilder checkoutWithId(final Long id) {
        return MockEntity.checkout()
                .id(id)
                .usesSingleAddress(false)
                .shippingRequired(true)
                .massUnit(MassUnit.g)
                .totalMass(100)
                .nullPaymentDetails(true);
    }

    private static Address addressInCountry(final CountryCode country) {
        return Address.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .streetAddress1("Street Address 1")
                .streetAddress2("Street Address 2")
                .country(country)
                .countryArea("Country Area")
                .city("City")
                .cityArea("City Area")
                .postalCode("ABC123")
                .phone1("123456789")
                .phone2("987654321")
                .build();
    }

    private static Payment payment() {
        return MockEntity.payment().build();
    }

    private static PaymentDetails paymentDetailsFromPayment(final Payment payment) {
        return MockEntity.paymentDetails()
                .id(payment.getId())
                .token(payment.getToken())
                .build();
    }

    private static ShippingMethod shippingMethodWithIdForCountry(final Long id, final CountryCode country) {
        final ShippingZone zone = MockEntity.shippingZone().build();
        MockEntity.shippingCountry().zone(zone).code(country).build();
        return MockEntity.shippingMethod()
                .id(id)
                .zone(zone)
                .type(ShippingMethodType.Weight)
                .min(0L)
                .max(75000L)
                .rate(1000L)
                .build();
    }

    private static UserAddress userAddressWithIdForUser(final Long id, final User user) {
        return MockEntity.userAddress()
                .id(id)
                .user(user)
                .build();
    }

    private static Configuration defaultConfiguration() {
        return MockEntity.configuration()
                .massUnit(MassUnit.g)
                .currency(CurrencyCode.EUR)
                .build();
    }

    private static List<ProductVariant> physicalProducts(final List<Line> lines) {
        return nullable(lines)
                .map(line -> physicalProductWithId(line.getId()))
                .collect(Collectors.toList());
    }

    private static List<ProductVariant> digitalProducts(final List<Line> lines) {
        return nullable(lines)
                .map(line -> digitalProductWithId(line.getId()))
                .collect(Collectors.toList());
    }

    private static ProductVariant physicalProductWithId(final Long id) {
        return MockEntity.productVariant()
                .id(id)
                .product(MockEntity.product()
                        .defaultPrice(350L)
                        .published(true)
                        .digital(false)
                        .build())
                .build();
    }

    private static ProductVariant digitalProductWithId(final Long id) {
        return MockEntity.productVariant()
                .id(id)
                .product(MockEntity.product()
                        .defaultPrice(350L)
                        .published(true)
                        .digital(true)
                        .build())
                .build();
    }

    private static void setPublished(final ProductVariant variant) {
        final Product product = variant.getProduct();
        setField(product, "published", false);
    }

}
