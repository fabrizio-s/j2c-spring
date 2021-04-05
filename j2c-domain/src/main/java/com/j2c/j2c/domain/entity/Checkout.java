package com.j2c.j2c.domain.entity;

import com.google.common.collect.ImmutableList;
import com.j2c.j2c.domain.enums.MassUnit;
import com.j2c.j2c.domain.exception.DomainException;
import com.neovisionaries.i18n.CurrencyCode;
import lombok.*;

import javax.persistence.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.j2c.j2c.domain.entity.MaxLengths.*;
import static com.j2c.j2c.domain.exception.DomainErrorMessages.*;
import static com.j2c.j2c.domain.util.J2cUtils.*;

@javax.persistence.Entity
@Table(name = "checkout")
public class Checkout extends OnUpdateAuditedEntity<Long> {

    @Id
    @Getter
    @Column(name = "id")
    private Long id;

    @MapsId
    @Getter
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private User customer;

    @Getter
    @Column(name = "email", nullable = false, updatable = false,
            length = EMAIL_MAXLENGTH)
    private String email;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, updatable = false,
            length = CONFIGURATION_CURRENCY_MAXLENGTH)
    private CurrencyCode currency;

    @Getter
    @Column(name = "price", nullable = false)
    private Long price;

    @Getter
    @Embedded
    private PaymentDetails paymentDetails;

    @Getter
    @Column(name = "ip_address", nullable = false, updatable = false,
            length = IPADDRESS_MAXLENGTH)
    private String ipAddress;

    @Getter
    @Column(name = "total_mass", updatable = false)
    private Integer totalMass;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "mass_unit", updatable = false,
            length = CONFIGURATION_MASSUNIT_MAXLENGTH)
    private MassUnit massUnit;

    @Column(name = "address_created")
    private Boolean addressCreated;

    @Getter
    @Embedded
    private Address address;

    @Getter
    @Column(name = "shipping_required", nullable = false, updatable = false)
    private boolean shippingRequired;

    @Getter
    @Embedded
    private ShippingMethodDetails shippingMethodDetails;

    @Getter
    @Column(name = "usessingleaddress")
    private Boolean usesSingleAddress;

    @Getter
    @Column(name = "savepaymentmethodasdefault")
    private Boolean savePaymentMethodAsDefault = false;

    @Getter
    @Column(name = "savecustomeraddresses")
    private Boolean saveCustomerAddresses = false;

    @Column(name = "shippingaddress_created")
    private Boolean shippingAddressCreated;

    @Getter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "firstName", column = @Column(name = "shippingaddress_firstname", length = ADDRESS_FIRSTNAME_MAXLENGTH)),
            @AttributeOverride(name = "lastName", column = @Column(name = "shippingaddress_lastname", length = ADDRESS_LASTNAME_MAXLENGTH)),
            @AttributeOverride(name = "streetAddress1", column = @Column(name = "shippingaddress_streetaddress1", length = ADDRESS_STREETADDRESS_MAXLENGTH)),
            @AttributeOverride(name = "streetAddress2", column = @Column(name = "shippingaddress_streetaddress2", length = ADDRESS_STREETADDRESS_MAXLENGTH)),
            @AttributeOverride(name = "country", column = @Column(name = "shippingaddress_country", length = COUNTRY_CODE_MAXLENGTH)),
            @AttributeOverride(name = "countryArea", column = @Column(name = "shippingaddress_countryarea", length = ADDRESS_COUNTRYAREA_MAXLENGTH)),
            @AttributeOverride(name = "city", column = @Column(name = "shippingaddress_city", length = ADDRESS_CITY_MAXLENGTH)),
            @AttributeOverride(name = "cityArea", column = @Column(name = "shippingaddress_cityarea", length = ADDRESS_CITYAREA_MAXLENGTH)),
            @AttributeOverride(name = "postalCode", column = @Column(name = "shippingaddress_postalcode", length = ADDRESS_POSTALCODE_MAXLENGTH)),
            @AttributeOverride(name = "phone1", column = @Column(name = "shippingaddress_phone1", length = ADDRESS_PHONE_MAXLENGTH)),
            @AttributeOverride(name = "phone2", column = @Column(name = "shippingaddress_phone2", length = ADDRESS_PHONE_MAXLENGTH))
    })
    private Address shippingAddress;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,
            mappedBy = "checkout", orphanRemoval = true)
    private List<CheckoutLine> lines = new ArrayList<>();

    @SuppressWarnings("unused")
    Checkout() {}

    @Builder(access = AccessLevel.PRIVATE,
            builderClassName = "CheckoutConstructor",
            builderMethodName = "newInstance")
    private Checkout(
            final User customer,
            final List<PreCheckoutLine> lines,
            final String email,
            final CurrencyCode currency,
            final String ipAddress,
            final MassUnit massUnit
    ) {
        this.customer = assertNotNull(customer, "customer");
        if (lines == null) {
            throw new IllegalArgumentException("lines must not be null");
        } else if (lines.isEmpty()) {
            throw new IllegalArgumentException("Checkout lines must not be empty");
        } else if (containsNull(lines)) {
            throw new IllegalArgumentException("Checkout lines must not contain null elements");
        }
        verifyAllProductsArePublished(lines);
        this.email = assertNotNull(email, "email");
        this.currency = assertNotNull(currency, "currency");
        this.ipAddress = assertNotNull(ipAddress, "ipAddress");
        price = calculatePrice(lines);
        shippingRequired = calculateShippingRequired(lines);
        if (shippingRequired) {
            if (massUnit == null) {
                throw new DomainException(CHECKOUT_MISSING_MASS_UNIT, this);
            }
            this.massUnit = massUnit;
            totalMass = calculateTotalMass(lines);
            usesSingleAddress = false;
        }
    }

    public boolean hasPayment() {
        return getPaymentId() != null;
    }

    public void setPayment(final Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("payment must not be null");
        }
        paymentDetails = PaymentDetails.builder()
                .id(payment.getId())
                .token(payment.getToken())
                .build();
    }

    public void addShippingAddress(final Address newShippingAddress) {
        setShippingAddress(newShippingAddress);
        shippingAddressCreated = true;
    }

    public void setShippingAddress(final Address newShippingAddress) {
        if (newShippingAddress == null) {
            throw new DomainException(String.format(CHECKOUT_NULL_ADDRESS, id), this);
        } else if (!shippingRequired) {
            throw new DomainException(String.format(CHECKOUT_NO_SHIPPING_REQUIRED, id), this);
        } else if (usesSingleAddress) {
            throw new DomainException(String.format(CHECKOUT_USES_SINGLE_ADDRESS, id), this);
        } else {
            shippingMethodDetails = null;
            shippingAddress = newShippingAddress;
        }
    }

    public void addAddress(final Address newAddress) {
        setAddress(newAddress);
        addressCreated = true;
    }

    public void setAddress(final Address newAddress) {
        if (newAddress == null) {
            throw new DomainException(String.format(CHECKOUT_NULL_ADDRESS, id), this);
        }
        shippingMethodDetails = null;
        address = newAddress;
    }

    public void setShippingMethod(final ShippingMethod shippingMethod) {
        if (shippingMethod == null) {
            throw new IllegalArgumentException("shippingMethod must not be null");
        } else if (!shippingRequired) {
            throw new DomainException(String.format(CHECKOUT_NO_SHIPPING_REQUIRED, id), this);
        } else if (!isApplicable(shippingMethod)) {
            throw new DomainException(String.format(CHECKOUT_INVALID_SHIPPINGMETHOD, shippingMethod.getId(), id), this);
        }
        shippingMethodDetails = ShippingMethodDetails.builder()
                .name(shippingMethod.getName())
                .amount(shippingMethod.getRate())
                .type(shippingMethod.getType())
                .build();
    }

    public void useSingleAddress(final boolean usa) {
        if (usa) {
            if (shippingAddress != null) {
                addressCreated = shippingAddressCreated;
                address = shippingAddress;
            }
            shippingAddressCreated = null;
            shippingAddress = null;
        }
        usesSingleAddress = usa;
    }

    public Order complete(final Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("payment must not be null");
        }
        final String checkoutPaymentId = getPaymentId();
        if (isWrongPayment(payment)) {
            throw new DomainException(String.format(CHECKOUT_WRONG_PAYMENT, payment.getId(), checkoutPaymentId), this);
        }
        final String missingDetails = getMissingDetails();
        if (!missingDetails.isEmpty()) {
            throw new DomainException(String.format(CHECKOUT_MISSING_DETAILS, id, missingDetails), this);
        }
        final Order order = Order.builder()
                .checkout(this)
                .capturedAmount(payment.getCapturedAmount())
                .build();
        saveCustomerAddressesIfWanted();
        savePaymentMethodAsDefaultIfWanted(payment);
        payment.capture();
        return order;
    }

    public Long getTotalPrice() {
        long total = price;
        if (shippingRequired) {
            total += optional(shippingMethodDetails)
                    .map(ShippingMethodDetails::getAmount)
                    .orElse(0L);
        }
        return total;
    }

    public List<CheckoutLine> getLines() {
        return ImmutableList.copyOf(lines);
    }

    public boolean isReadyForPurchase() {
        return getMissingDetails().isEmpty();
    }

    public String getPaymentId() {
        if (paymentDetails == null) {
            return null;
        }
        return paymentDetails.getId();
    }

    public void setSavePaymentMethodAsDefault(final Boolean savePaymentMethodAsDefault) {
        this.savePaymentMethodAsDefault = assertNotNull(savePaymentMethodAsDefault, "savePaymentMethodAsDefault");
    }

    public void setSaveCustomerAddresses(final Boolean saveCustomerAddresses) {
        this.saveCustomerAddresses = assertNotNull(saveCustomerAddresses, "saveCustomerAddresses");
    }

    public Address getActualShippingAddress() {
        if (usesSingleAddress) {
            return address;
        }
        return shippingAddress;
    }

    private void addLine(final PreCheckoutLine line) {
        lines.add(
                CheckoutLine.builder()
                        .checkout(this)
                        .preCheckoutLine(line)
                        .build()
        );
    }

    private boolean isApplicable(final ShippingMethod shippingMethod) {
        final Address shippingAddress = getActualShippingAddress();
        if (shippingAddress == null) {
            throw new DomainException(String.format(CHECKOUT_NULL_ADDRESS, id), this);
        } else if (shippingMethod == null) {
            return false;
        }
        return shippingMethod.canBeAppliedToCheckout(this);
    }

    private boolean hasShippingMethod() {
        return shippingMethodDetails != null;
    }

    private boolean hasAddress() {
        return address != null;
    }

    private boolean hasShippingAddress() {
        return getActualShippingAddress() != null;
    }

    private void saveCustomerAddressesIfWanted() {
        if (saveCustomerAddresses) {
            if (addressCreated && address != null) {
                customer.addAddress(address);
            }
            if (shippingAddressCreated && shippingAddress != null) {
                customer.addAddress(shippingAddress);
            }
        }
    }

    private void savePaymentMethodAsDefaultIfWanted(final Payment payment) {
        if (savePaymentMethodAsDefault) {
            customer.setDefaultPaymentMethodId(payment.getPaymentMethodId());
        }
    }

    private String getMissingDetails() {
        final Set<String> errors = new HashSet<>();
        if (!hasAddress()) {
            errors.add("address");
        }
        if (shippingRequired) {
            if (!hasShippingAddress()) {
                errors.add("shipping address");
            }
            if (!hasShippingMethod()) {
                errors.add("shipping method");
            }
        }
        return String.join(",", errors);
    }

    private boolean isWrongPayment(final Payment payment) {
        if (payment.isFree() && getPaymentId() == null) {
            return false;
        }
        return !payment.belongsToCheckout(this);
    }

    @Builder
    private static Checkout create(
            final User customer,
            final List<PreCheckoutLine> lines,
            final String email,
            final CurrencyCode currency,
            final String ipAddress,
            final MassUnit massUnit
    ) {
        final Checkout checkout = newInstance()
                .customer(customer)
                .lines(lines)
                .email(email)
                .currency(currency)
                .ipAddress(ipAddress)
                .massUnit(massUnit)
                .build();
        lines.forEach(checkout::addLine);
        return checkout;
    }

    private static void verifyAllProductsArePublished(final List<PreCheckoutLine> lines) {
        final String unpublishedProductIds = lines.stream()
                .map(PreCheckoutLine::getVariant)
                .filter(v -> !v.getProduct().isPublished())
                .map(ProductVariant::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        if (!unpublishedProductIds.isEmpty()) {
            throw new DomainException(String.format(CANNOT_CHECKOUT_UNPUBLISHED, unpublishedProductIds), null);
        }
    }

    private static Integer calculateTotalMass(final List<PreCheckoutLine> lines) {
        return lines.stream()
                .map(PreCheckoutLine::getVariant)
                .map(ProductVariant::getMass)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .filter(mass -> mass > 0)
                .sum();
    }

    private static boolean calculateShippingRequired(final List<PreCheckoutLine> lines) {
        return lines.stream()
                .anyMatch(PreCheckoutLine::requiresShipping);
    }

    private static Long calculatePrice(final List<PreCheckoutLine> lines) {
        return lines.stream()
                .map(PreCheckoutLine::price)
                .mapToLong(Long::longValue)
                .filter(unitPrice -> unitPrice > 0)
                .sum();
    }

    public static class PreCheckoutLine {

        @Getter
        private final ProductVariant variant;

        @Getter
        private final int quantity;

        @Builder
        private PreCheckoutLine(
                final ProductVariant variant,
                final int quantity
        ) {
            if (variant == null) {
                throw new IllegalArgumentException("variant must not be null");
            } else if (quantity <= 0) {
                throw new IllegalArgumentException("quantity must be positive");
            }
            this.quantity = quantity;
            this.variant = variant;
        }

        public Long price() {
            if (quantity <= 0) {
                return 0L;
            }
            final Long price = variant.getEffectivePrice();
            return price * quantity;
        }

        public boolean requiresShipping() {
            final Product product = variant.getProduct();
            return !product.isDigital();
        }

    }

}
