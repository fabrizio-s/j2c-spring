package com.j2c.j2c.domain.entity;

import com.google.common.collect.ImmutableList;
import com.j2c.j2c.domain.exception.DomainException;
import com.j2c.j2c.domain.enums.OrderStatus;
import com.neovisionaries.i18n.CurrencyCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.j2c.j2c.domain.entity.MaxLengths.*;
import static com.j2c.j2c.domain.exception.DomainErrorMessages.*;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

@javax.persistence.Entity
@Table(name = "`order`",
        indexes = @Index(columnList = "customer_id")
)
public class Order extends OnUpdateAuditedEntity<Long> {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private User customer;

    @Getter
    @Column(name = "email", nullable = false, updatable = false,
            length = EMAIL_MAXLENGTH)
    private String email;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false,
            length = ORDER_STATUS_MAXLENGTH)
    private OrderStatus status;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status",
            length = ORDER_STATUS_MAXLENGTH)
    private OrderStatus previousStatus;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, updatable = false,
            length = CONFIGURATION_CURRENCY_MAXLENGTH)
    private CurrencyCode currency;

    @Getter
    @Column(name = "captured_amount",
            nullable = false, updatable = false)
    private Long capturedAmount;

    @Getter
    @Column(name = "payment_id", nullable = false, updatable = false,
            length = PAYMENT_ID_MAXLENGTH)
    private String paymentId;

    @Getter
    @Column(name = "ip_address", nullable = false, updatable = false,
            length = IPADDRESS_MAXLENGTH)
    private String ipAddress;

    @Getter
    @Embedded
    private Address address;

    @Getter
    @Embedded
    private ShippingMethodDetails shippingMethodDetails;

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
            mappedBy = "order", orphanRemoval = true)
    private List<OrderLine> lines = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,
            mappedBy = "order", orphanRemoval = true)
    private List<OrderFulfillment> fulfillments = new ArrayList<>();

    @SuppressWarnings("unused")
    Order() {}

    private Order(
            final Checkout checkout,
            final Long capturedAmount
    ) {
        if (checkout == null) {
            throw new IllegalArgumentException("checkout must not be null");
        }
        this.capturedAmount = assertNotNull(capturedAmount, "capturedAmount");
        this.currency = checkout.getCurrency();
        customer = checkout.getCustomer();
        email = checkout.getEmail();
        status = OrderStatus.CREATED;
        paymentId = checkout.getPaymentId();
        ipAddress = checkout.getIpAddress();
        address = checkout.getAddress();
        shippingMethodDetails = checkout.getShippingMethodDetails();
        shippingAddress = checkout.getShippingAddress();
    }

    public OrderFulfillment newFulfillment() {
        if (!isProcessable()) {
            throw new DomainException(String.format(ORDER_NOT_PROCESSABLE, id, status), this);
        } else if (OrderStatus.CONFIRMED.equals(status)) {
            setStatusAndPreviousStatus(OrderStatus.PROCESSING);
        }
        final OrderFulfillment fulfillment = OrderFulfillment.builder()
                .order(this)
                .build();
        fulfillments.add(fulfillment);
        return fulfillment;
    }

    public List<OrderLine> removeFulfillment(final OrderFulfillment fulfillment) {
        if (!isProcessable()) {
            throw new DomainException(String.format(ORDER_NOT_PROCESSABLE, id, status), this);
        } else if (!fulfillment.belongsToOrder(this)) {
            return Collections.emptyList();
        }
        fulfillments.remove(fulfillment);
        return fulfillment.remove();
    }

    public boolean isProcessable() {
        return OrderStatus.CONFIRMED.equals(status)
                || OrderStatus.PROCESSING.equals(status)
                || OrderStatus.PARTIALLY_FULFILLED.equals(status);
    }

    public void confirm() {
        if (!OrderStatus.CREATED.equals(status)) {
            throw new DomainException(String.format(ORDER_STATUS_MUST_BE_CREATED, id), this);
        }
        setStatusAndPreviousStatus(OrderStatus.CONFIRMED);
    }

    public void fulfill() {
        if (!isProcessable()) {
            throw new DomainException(String.format(ORDER_NOT_PROCESSABLE, id, status), this);
        } else if (anyLineThatRequiresShippingIsNotFulfilled()) {
            throw new DomainException(String.format(CANNOT_FULFILL_ORDER_WITH_UNFULFILLED_LINES, id), this);
        }
        setStatusAndPreviousStatus(OrderStatus.FULFILLED);
    }

    public void undoFulfill() {
        if (!OrderStatus.FULFILLED.equals(status)) {
            throw new DomainException(String.format(ORDER_NOT_FULFILLED, id), this);
        }
        status = previousStatus;
    }

    public void cancel() {
        if (OrderStatus.CANCELLED.equals(status)) {
            throw new DomainException(String.format(ORDER_ALREADY_CANCELLED, id), this);
        }
        setStatusAndPreviousStatus(OrderStatus.CANCELLED);
    }

    public void reinstate() {
        if (!OrderStatus.CANCELLED.equals(status)) {
            throw new DomainException(String.format(ORDER_NOT_CANCELLED, id), this);
        }
        status = previousStatus;
    }

    public List<OrderLine> getLines() {
        return ImmutableList.copyOf(lines);
    }

    void partiallyFulfill() {
        if (OrderStatus.CANCELLED.equals(this.status)) {
            throw new DomainException(String.format(ORDER_ALREADY_CANCELLED, id), this);
        }
        setStatusAndPreviousStatus(OrderStatus.PARTIALLY_FULFILLED);
    }

    private void addLine(final CheckoutLine checkoutLine) {
        lines.add(
                OrderLine.builder()
                        .order(this)
                        .checkoutLine(checkoutLine)
                        .build()
        );
    }

    private void setStatusAndPreviousStatus(final OrderStatus newStatus) {
        if (!status.equals(newStatus) && !status.isFinalizing()) {
            previousStatus = status;
        }
        status = newStatus;
    }

    private boolean anyLineThatRequiresShippingIsNotFulfilled() {
        return lines.stream()
                .anyMatch(ol -> ol.isShippingRequired() && !ol.isFulfilled());
    }

    @Builder(access = AccessLevel.PACKAGE)
    private static Order create(
            final Checkout checkout,
            final Long capturedAmount
    ) {
        final Order order = new Order(checkout, capturedAmount);
        checkout.getLines().forEach(order::addLine);
        if (noLineRequiresShipping(order.lines)) {
            order.previousStatus = OrderStatus.CREATED;
            order.status = OrderStatus.FULFILLED;
        }
        return order;
    }

    private static boolean noLineRequiresShipping(final List<OrderLine> lines) {
        return lines.stream()
                .noneMatch(OrderLine::isShippingRequired);
    }

}
