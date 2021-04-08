package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.entity.Address.AddressBuilder;
import com.j2c.j2c.domain.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.domain.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddressTest extends BaseMapperTest {

    @Test
    void new_NullFirstName_ShouldThrowIllegalArgumentException() {
        final AddressBuilder builder = hpAddressBuilder()
                .firstName(null);

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                builder::build
        );
        assertEquals("firstName must not be null", exception.getMessage());
    }

    @Test
    void new_NullLastName_ShouldThrowIllegalArgumentException() {
        final AddressBuilder builder = hpAddressBuilder()
                .lastName(null);

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                builder::build
        );
        assertEquals("lastName must not be null", exception.getMessage());
    }

    @Test
    void new_NullStreetAddress1_ShouldThrowIllegalArgumentException() {
        final AddressBuilder builder = hpAddressBuilder()
                .streetAddress1(null);

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                builder::build
        );
        assertEquals("streetAddress1 must not be null", exception.getMessage());
    }

    @Test
    void new_NullCountry_ShouldThrowIllegalArgumentException() {
        final AddressBuilder builder = hpAddressBuilder()
                .country(null);

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                builder::build
        );
        assertEquals("country must not be null", exception.getMessage());
    }

    @Test
    void new_NullCountryArea_ShouldThrowIllegalArgumentException() {
        final AddressBuilder builder = hpAddressBuilder()
                .countryArea(null);

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                builder::build
        );
        assertEquals("countryArea must not be null", exception.getMessage());
    }

    @Test
    void new_NullCity_ShouldThrowIllegalArgumentException() {
        final AddressBuilder builder = hpAddressBuilder()
                .city(null);

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                builder::build
        );
        assertEquals("city must not be null", exception.getMessage());
    }

    @Test
    void new_NullPostalCode_ShouldThrowIllegalArgumentException() {
        final AddressBuilder builder = hpAddressBuilder()
                .postalCode(null);

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                builder::build
        );
        assertEquals("postalCode must not be null", exception.getMessage());
    }

    @Test
    void new_NullPhone1_ShouldThrowIllegalArgumentException() {
        final AddressBuilder builder = hpAddressBuilder()
                .phone1(null);

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                builder::build
        );
        assertEquals("phone1 must not be null", exception.getMessage());
    }

    @Test
    void copy_HappyPath_ShouldHaveAllSameFieldValues() {
        final Address address = nextObject(Address.class);

        final AddressBuilder builder = address.copy();

        assertMappings(address, builder);
    }

    private static Address.AddressBuilder hpAddressBuilder() {
        return nextObject(Address.AddressBuilder.class);
    }

}
