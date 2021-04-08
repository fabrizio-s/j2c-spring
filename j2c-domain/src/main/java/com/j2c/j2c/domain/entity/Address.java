package com.j2c.j2c.domain.entity;

import com.neovisionaries.i18n.CountryCode;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.lang.reflect.Field;
import java.util.Arrays;

import static com.j2c.j2c.domain.entity.MaxLengths.*;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

@Embeddable
public class Address {

    @Getter
    @Column(name = "address_firstname",
            length = ADDRESS_FIRSTNAME_MAXLENGTH)
    private String firstName;

    @Getter
    @Column(name = "address_lastname",
            length = ADDRESS_LASTNAME_MAXLENGTH)
    private String lastName;

    @Getter
    @Column(name = "address_streetaddress1",
            length = ADDRESS_STREETADDRESS_MAXLENGTH)
    private String streetAddress1;

    @Getter
    @Column(name = "address_streetaddress2", length = ADDRESS_STREETADDRESS_MAXLENGTH)
    private String streetAddress2;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "address_country",
            length = COUNTRY_CODE_MAXLENGTH)
    private CountryCode country;

    @Getter
    @Column(name = "address_countryarea",
            length = ADDRESS_COUNTRYAREA_MAXLENGTH)
    private String countryArea;

    @Getter
    @Column(name = "address_city",
            length = ADDRESS_CITY_MAXLENGTH)
    private String city;

    @Getter
    @Column(name = "address_cityarea", length = ADDRESS_CITYAREA_MAXLENGTH)
    private String cityArea;

    @Getter
    @Column(name = "address_postalcode",
            length = ADDRESS_POSTALCODE_MAXLENGTH)
    private String postalCode;

    @Getter
    @Column(name = "address_phone1",
            length = ADDRESS_PHONE_MAXLENGTH)
    private String phone1;

    @Getter
    @Column(name = "address_phone2", length = ADDRESS_PHONE_MAXLENGTH)
    private String phone2;

    @SuppressWarnings("unused")
    Address() {}

    @Builder
    private Address(
            final String firstName,
            final String lastName,
            final String streetAddress1,
            final String streetAddress2,
            final CountryCode country,
            final String countryArea,
            final String city,
            final String cityArea,
            final String postalCode,
            final String phone1,
            final String phone2
    ) {
        this.firstName = assertNotNull(firstName, "firstName");
        this.lastName = assertNotNull(lastName, "lastName");
        this.streetAddress1 = assertNotNull(streetAddress1, "streetAddress1");
        this.streetAddress2 = streetAddress2;
        this.country = assertNotNull(country, "country");
        this.countryArea = assertNotNull(countryArea, "countryArea");
        this.city = assertNotNull(city, "city");
        this.cityArea = cityArea;
        this.postalCode = assertNotNull(postalCode, "postalCode");
        this.phone1 = assertNotNull(phone1, "phone1");
        this.phone2 = phone2;
    }

    public AddressBuilder copy() {
        final AddressBuilder builder = new AddressBuilder();
        Arrays.stream(this.getClass().getDeclaredFields())
                .forEach(f -> copyBuilderField(builder, f));
        return builder;
    }

    private void copyBuilderField(final AddressBuilder builder, final Field field) {
        final Field builderField = getField(field.getName());
        final Object value = getFieldValue(field, this);
        setFieldValue(builderField, builder, value);
    }

    private static Object getFieldValue(final Field field, final Object obj) {
        try {
            return field.get(obj);
        } catch (final IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static void setFieldValue(final Field field, final Object obj, final Object value) {
        try {
            field.set(obj, value);
        } catch (final IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Field getField(final String name) {
        try {
            return AddressBuilder.class.getDeclaredField(name);
        } catch (final NoSuchFieldException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public String toString() {
        return "Address(" +
                "streetAddress1='" + streetAddress1 + '\'' +
                ", country=" + country +
                ", city='" + city + '\'' +
                ')';
    }

}
