package com.j2c.j2c.domain.exception;

public final class DomainErrorMessages {
    public static final String CANNOT_SET_UNOWNED_DEFAULT_VARIANT = "Cannot set default product variant to variant that does not belong to product for product with id '%s'";
    public static final String CANNOT_SET_DEFAULT_VARIANT_IF_CURRENT_HAS_NO_NAME = "Cannot change the default product variant for product with id '%s' if the current default variant has no name";
    public static final String DEFAULT_VARIANT_NAME_REQUIRED = "A name is required for the current default variant for product with id '%s'";
    public static final String VARIANT_REQUIRES_MASS = "Product variant mass is required for non-digital product with id '%s'";
    public static final String CANNOT_REMOVE_DEFAULT_VARIANT = "The product variant with id '%s' cannot be removed because it is the default variant of its product";
    public static final String CANNOT_PUBLISH_WITHOUT_DEFAULT_VARIANT = "Product with id '%s' cannot be published without a default variant";
    public static final String CANNOT_PUBLISH_BEFORE_X_MINUTES = "Product with id '%s' cannot be published %s minutes before last unpublish";
    public static final String ALREADY_PUBLISHED = "Product with id '%s' is already published";
    public static final String ALREADY_UNPUBLISHED = "Product with id '%s' is already unpublished";
    public static final String COUNTRY_ALREADY_BELONGS_TO_ZONE = "Country '%s' already belongs to shipping zone with id %s";
    public static final String SHIPPING_METHOD_MIN_MUST_BE_LESS_THAN_MAX = "Shipping Method MIN must not be greater than MAX!";
    public static final String IMAGE_DOES_NOT_BELONG_TO_VARIANT = "The image with id '%s' does not belong to the product variant with id '%s'";
    public static final String LINE_DOES_NOT_BELONG_TO_ORDER = "The order line with id '%s' does not belong to the order with id '%s'";
    public static final String FULFILLMENT_DOES_NOT_BELONG_TO_ORDER = "The order fulfillment with id '%s' does not belong to the order with id '%s'";
    public static final String METHOD_DOES_NOT_BELONG_TO_ZONE = "The shipping method with id '%s' does not belong to the zone with id '%s'";
    public static final String VARIANT_DOES_NOT_BELONG_TO_PRODUCT = "The product variant with id '%s' does not belong to the product with id '%s'";
    public static final String ADDRESS_DOES_NOT_BELONG_TO_USER = "The address with id '%s' does not belong to the user with id '%s'";
    public static final String LINE_DOES_NOT_BELONG_TO_CHECKOUT = "The line with id '%s' does not belong to the checkout with id '%s'";
    public static final String INSUFFICIENT_ORDER_LINE_ASSIGNABLE_QUANTITY = "Cannot reserve for the order line with id '%s' a quantity greater than '%s'";
    public static final String CANNOT_FULFILL_MORE_THAN_ORDER_LINE_QUANTITY = "The fulfilled quantity for the order line with id '%s' cannot be greater than its quantity";
    public static final String ORDER_NOT_PROCESSABLE = "The order status for the order with id '%s' and status '%s' must be one of the following to perform this operation: CONFIRMED, PROCESSING, PARTIALLY_FULFILLED";
    public static final String FULFILLMENT_ORDER_DIFFERS_FROM_ORDER_LINE_ORDER = "Attempting to create a fulfillment order line for an order line that belongs to a different order than that or fulfillment with id '%s'";
    public static final String FULFILLMENT_LINE_QUANTITY_MUST_BE_POSITIVE = "Cannot assign non positive quantity to order fulfillment line with id '%s'";
    public static final String ORDER_LINE_DOES_NOT_REQUIRE_SHIPPING = "Cannot create a fulfillment line for order line with id '%s' that does not require shipping";
    public static final String LINE_DOES_NOT_BELONG_TO_FULFILLMENT = "The fulfillment line with id '%s' does not belong to the fulfillment with id '%s'";
    public static final String FULFILLMENT_ALREADY_COMPLETED = "Cannot perform operation on order fulfillment with id '%s' because it has already been completed";
    public static final String FULFILLMENT_NOT_COMPLETED = "The order fulfillment with id '%s' must be completed to perform this operation";
    public static final String ORDER_NOT_CANCELLED = "The order with id '%s' must be cancelled to perform this operation";
    public static final String ORDER_NOT_FULFILLED = "The order with id '%s' must be fulfilled to perform this operation";
    public static final String ORDER_ALREADY_CANCELLED = "Cannot perform this operation on order with id '%s' because it has already been cancelled";
    public static final String CANNOT_FULFILL_ORDER_WITH_UNFULFILLED_LINES = "Cannot fulfill order with id '%s' because at least one of its lines is not fulfilled";
    public static final String ORDER_STATUS_MUST_BE_CREATED = "The status for the order with id '%s' must be 'CREATED' to perform this action";
    public static final String CANNOT_CHECKOUT_UNPUBLISHED = "The following product variants cannot be purchased because they belong to products that are currently not published: %s";
    public static final String CHECKOUT_MISSING_MASS_UNIT = "Unit of mass is required for checkout that requires shipping";
    public static final String CHECKOUT_NO_SHIPPING_REQUIRED = "The checkout with id '%s' does not require shipping";
    public static final String CHECKOUT_MUST_HAVE_EXISTING_ADDRESS = "The checkout with id '%s' must have an existing address to perform this operation";
    public static final String CHECKOUT_USES_SINGLE_ADDRESS = "The checkout with id '%s' uses a single address";
    public static final String CHECKOUT_NULL_ADDRESS = "A valid address is required to perform this operation on checkout with id '%s'";
    public static final String CHECKOUT_INVALID_SHIPPINGMETHOD = "The shipping method with id '%s' cannot be applied to the checkout with id '%s'";
    public static final String CHECKOUT_WRONG_PAYMENT = "Attempting to complete checkout with payment with id '%s' when '%s' should be used instead";
    public static final String CHECKOUT_MISSING_DETAILS = "The checkout with id '%s' cannot be completed because it is missing the following details: %s";
    public static final String CHECKOUT_ALREADY_EXISTS = "A checkout for customer with id '%s' already exists";
    public static final String VALID_USER_EMAIL_BUT_NULL_PASSWORD = "Cannot set the email of user with id '%s' if their password is null";

    private DomainErrorMessages() {}

}
