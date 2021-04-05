package com.j2c.j2c.domain.test;

import com.j2c.j2c.domain.entity.*;
import com.j2c.j2c.domain.enums.*;
import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.CurrencyCode;
import lombok.Builder;
import lombok.NonNull;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

import static com.j2c.j2c.domain.test.MockIdGenerator.*;
import static com.j2c.j2c.domain.test.TestUtils.randomLong;
import static com.j2c.j2c.domain.test.TestUtils.spyField;
import static java.util.Objects.requireNonNull;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@SuppressWarnings("unchecked")
public final class MockEntity {

    private static final EasyRandom easyRandom = new EasyRandom(
            new EasyRandomParameters()
                    .seed(randomLong())
                    .collectionSizeRange(0, 0)
                    .stringLengthRange(2, 12)
                    .charset(StandardCharsets.UTF_8)
                    .randomizationDepth(2)
                    .scanClasspathForConcreteTypes(true)
                    .objectPoolSize(50)
                    .bypassSetters(true)
    );

    private MockEntity() {}

    @Builder(builderClassName = "MockPaymentBuilder",
            builderMethodName = "payment")
    private static Payment mPayment(
            final String id,
            final String token,
            final Long capturedAmount,
            final String paymentMethodId,
            final CurrencyCode currency
    ) {
        final Payment payment = newEntity(MockPayment.class, id);
        setFieldIfNotNull(payment, "token", token);
        setFieldIfNotNull(payment, "capturedAmount", capturedAmount);
        setFieldIfNotNull(payment, "paymentMethodId", paymentMethodId);
        setFieldIfNotNull(payment, "currency", currency);

        return payment;
    }

    @Builder(builderClassName = "MockPaymentDetailsBuilder",
            builderMethodName = "paymentDetails")
    private static PaymentDetails mPaymentDetails(
            final String id,
            final String token
    ) {
        final PaymentDetails payment = easyRandom.nextObject(PaymentDetails.class);
        setFieldIfNotNull(payment, "id", id);
        setFieldIfNotNull(payment, "token", token);

        return payment;
    }

    @Builder(builderClassName = "MockConfigurationBuilder",
            builderMethodName = "configuration")
    private static Configuration mConfiguration(
            final Long id,
            final CurrencyCode currency,
            final MassUnit massUnit,
            final boolean nullMassUnit
    ) {
        final Configuration configuration = newEntity(Configuration.class, id);
        if (nullMassUnit) {
            setNull(configuration, "massUnit");
        }
        setFieldIfNotNull(configuration, "profile", Profile.DEFAULT);
        setFieldIfNotNull(configuration, "currency", currency);
        setFieldIfNotNull(configuration, "massUnit", massUnit);

        return configuration;
    }

    @Builder(builderClassName = "MockRoleBuilder",
            builderMethodName = "role")
    private static Role mRole(
            final Long id,
            final RoleType roleType
            ) {
        final Role role = newEntity(Role.class, id);
        setFieldIfNotNull(role, "roleType", roleType);

        return role;
    }

    @Builder(builderClassName = "MockUserBuilder",
            builderMethodName = "user")
    private static User mUser(
            final Long id,
            final String email,
            final String externalId,
            final boolean nullPassword,
            final boolean nullExternalId
    ) {
        final User user = newEntity(User.class, id);
        if (nullPassword) {
            setNull(user, "password");
        }
        if (nullExternalId) {
            setNull(user, "externalId");
        }
        setFieldIfNotNull(user, "email", email);
        setFieldIfNotNull(user, "externalId", externalId);

        return user;
    }

    @Builder(builderClassName = "MockUserAddressBuilder",
            builderMethodName = "userAddress")
    private static UserAddress mUserAddress(
            final Long id,
            final User user,
            final Address address
    ) {
        final UserAddress userAddress = newEntity(UserAddress.class, id);
        setFieldIfNotNull(userAddress, "user", user);
        setFieldIfNotNull(userAddress, "address", address);

        addToParent(userAddress, "user", user, "addresses");
        return userAddress;
    }

    @Builder(builderClassName = "MockCheckoutBuilder",
            builderMethodName = "checkout")
    private static Checkout mCheckout(
            final Long id,
            final User customer,
            final String email,
            final CurrencyCode currency,
            final Long price,
            final PaymentDetails paymentDetails,
            final String ipAddress,
            final Integer totalMass,
            final MassUnit massUnit,
            final boolean shippingRequired,
            final ShippingMethodDetails shippingMethodDetails,
            final Address shippingAddress,
            final boolean usesSingleAddress,
            final boolean saveCustomerAddresses,
            final boolean savePaymentMethodAsDefault,
            final boolean addressCreated,
            final boolean shippingAddressCreated,
            final boolean nullShippingAddress,
            final boolean nullAddress,
            final boolean nullPaymentDetails,
            final boolean nullShippingMethodDetails
    ) {
        final Checkout checkout = newEntity(Checkout.class, id);
        if (nullShippingAddress) {
            setNull(checkout, "shippingAddress");
        }
        if (nullAddress) {
            setNull(checkout, "address");
        }
        if (nullPaymentDetails) {
            setNull(checkout, "paymentDetails");
        }
        if (nullShippingMethodDetails) {
            setNull(checkout, "shippingMethodDetails");
        }
        setFieldIfNotNull(checkout, "email", email);
        setFieldIfNotNull(checkout, "currency", currency);
        setFieldIfNotNull(checkout, "price", price);
        setFieldIfNotNull(checkout, "paymentDetails", paymentDetails);
        setFieldIfNotNull(checkout, "ipAddress", ipAddress);
        setFieldIfNotNull(checkout, "totalMass", totalMass);
        setFieldIfNotNull(checkout, "massUnit", massUnit);
        setFieldIfNotNull(checkout, "shippingRequired", shippingRequired);
        setFieldIfNotNull(checkout, "shippingMethodDetails", shippingMethodDetails);
        setFieldIfNotNull(checkout, "usesSingleAddress", usesSingleAddress);
        setFieldIfNotNull(checkout, "saveCustomerAddresses", saveCustomerAddresses);
        setFieldIfNotNull(checkout, "savePaymentMethodAsDefault", savePaymentMethodAsDefault);
        setFieldIfNotNull(checkout, "addressCreated", addressCreated);
        setFieldIfNotNull(checkout, "shippingAddressCreated", shippingAddressCreated);
        setFieldIfNotNull(checkout, "shippingAddress", shippingAddress);

        setUnidirectionalRelation(checkout, "customer", customer);
        return checkout;
    }

    @Builder(builderClassName = "MockCheckoutLineBuilder",
            builderMethodName = "checkoutLine")
    private static CheckoutLine mCheckoutLine(
            final Long id,
            final Checkout checkout,
            final int quantity,
            final boolean shippingRequired
    ) {
        final CheckoutLine checkoutLine = newEntity(CheckoutLine.class, id);
        setFieldIfNotNull(checkoutLine, "quantity", quantity);
        setFieldIfNotNull(checkoutLine, "shippingRequired", shippingRequired);

        setUnidirectionalRelation(checkoutLine, "checkout", checkout);
        return checkoutLine;
    }

    @Builder(builderClassName = "MockShippingZoneBuilder",
            builderMethodName = "shippingZone")
    private static ShippingZone mShippingZone(
            final Long id,
            final String name
    ) {
        final ShippingZone zone = newEntity(ShippingZone.class, id);
        setFieldIfNotNull(zone, "name", name);
        return zone;
    }

    @Builder(builderClassName = "MockShippingMethodBuilder",
            builderMethodName = "shippingMethod")
    private static ShippingMethod mShippingMethod(
            final Long id,
            final String name,
            final Long min,
            final Long max,
            final Long rate,
            final ShippingMethodType type,
            final ShippingZone zone
    ) {
        final ShippingMethod method = newEntity(ShippingMethod.class, id);
        setFieldIfNotNull(method, "name", name);
        setFieldIfNotNull(method, "min", min);
        setFieldIfNotNull(method, "max", max);
        setFieldIfNotNull(method, "rate", rate);
        setFieldIfNotNull(method, "type", type);
        setUnidirectionalRelation(method, "zone", zone);
        return method;
    }

    @Builder(builderClassName = "MockShippingCountryBuilder",
            builderMethodName = "shippingCountry")
    private static ShippingCountry mShippingCountry(
            final Long id,
            final CountryCode code,
            final ShippingZone zone,
            final boolean nullZone
    ) {
        final ShippingCountry country = newEntity(ShippingCountry.class, id);
        if (nullZone) {
            setNull(country, "zone");
        }
        setFieldIfNotNull(country, "code", code);
        addToParent(country, "zone", zone, "countries");

        return country;
    }

    @Builder(builderClassName = "MockProductBuilder",
            builderMethodName = "product")
    private static Product mProduct(
            final Long id,
            final String name,
            final String imageFilename,
            final String description,
            final boolean digital,
            final boolean published,
            final Long defaultPrice,
            final LocalDateTime lastUnpublished,
            final ProductVariant defaultVariant,
            final ProductCategory category,
            final boolean nullDefaultVariant
    ) {
        final Product product = newEntity(Product.class, id);
        if (nullDefaultVariant) {
            setNull(product, "defaultVariant");
        }
        setFieldIfNotNull(product, "name", name);
        setFieldIfNotNull(product, "imageFilename", imageFilename);
        setFieldIfNotNull(product, "description", description);
        setFieldIfNotNull(product, "digital", digital);
        setFieldIfNotNull(product, "published", published);
        setFieldIfNotNull(product, "defaultPrice", defaultPrice);
        setFieldIfNotNull(product, "lastUnpublished", lastUnpublished);

        setBidirectionalRelation(product, "defaultVariant", defaultVariant, "product");
        setUnidirectionalRelation(product, "category", category);
        mockSaveChildren(product, "variants");

        return product;
    }

    @Builder(builderClassName = "MockProductVariantBuilder",
            builderMethodName = "productVariant")
    private static ProductVariant mProductVariant(
            final Long id,
            final String name,
            final Product product,
            final Integer mass,
            final Long price,
            final boolean nullName,
            final boolean nullPrice
    ) {
        final ProductVariant variant = newEntity(ProductVariant.class, id);
        if (nullName) {
            setNull(variant, "name");
        }
        if (nullPrice) {
            setNull(variant, "price");
        }
        setFieldIfNotNull(variant, "name", name);
        setFieldIfNotNull(variant, "mass", mass);
        setFieldIfNotNull(variant, "price", price);
        addToParent(variant, "product", product, "variants");

        return variant;
    }

    @Builder(builderClassName = "MockProductVariantImageBuilder",
            builderMethodName = "productVariantImage")
    private static ProductVariantImage mProductVariantImage(
            final Long id,
            final ProductVariant variant,
            final String filename
    ) {
        final ProductVariantImage variantImage = newEntity(ProductVariantImage.class, id);
        setFieldIfNotNull(variantImage, "filename", filename);
        setUnidirectionalRelation(variantImage, "variant", variant);

        return variantImage;
    }

    @Builder(builderClassName = "MockUploadedImageBuilder",
            builderMethodName = "uploadedImage")
    private static UploadedImage mUploadedImage(
            final UUID id,
            final String filename
    ) {
        final UploadedImage uploadedImage = newEntity(UploadedImage.class, id);
        setFieldIfNotNull(uploadedImage, "filename", filename);
        return uploadedImage;
    }

    @Builder(builderClassName = "MockProductCategoryBuilder",
            builderMethodName = "productCategory")
    private static ProductCategory mProductCategory(
            final Long id,
            final String name,
            final String description,
            final String imageFilename,
            final boolean nullRoot
    ) {
        final ProductCategory category = newEntity(ProductCategory.class, id);
        if (nullRoot) {
            setNull(category, "root");
        }
        setFieldIfNotNull(category, "name", name);
        setFieldIfNotNull(category, "description", description);
        setFieldIfNotNull(category, "imageFilename", imageFilename);
        return category;
    }

    @Builder(builderClassName = "MockProductTagBuilder",
            builderMethodName = "productTag")
    private static ProductTag mProductTag(
            final Long id,
            final String name
    ) {
        final ProductTag tag = newEntity(ProductTag.class, id);
        setFieldIfNotNull(tag, "name", name);
        return tag;
    }

    @Builder(builderClassName = "MockProductToTagAssociationBuilder",
            builderMethodName = "productToTagAssociation")
    private static ProductToTagAssociation mProductToTagAssociation(
            final Product product,
            final ProductTag tag
    ) {
        return ProductToTagAssociation.builder()
                .product(product)
                .tag(tag)
                .build();
    }

    @Builder(builderClassName = "MockOrderBuilder",
            builderMethodName = "order")
    private static Order mOrder(
            final Long id,
            final OrderStatus status,
            final OrderStatus previousStatus
    ) {
        final Order order = newEntity(Order.class, id);
        setFieldIfNotNull(order, "status", status);
        setFieldIfNotNull(order, "previousStatus", previousStatus);

        mockSaveChildren(order, "lines");
        mockSaveChildren(order, "fulfillments");
        return order;
    }

    @Builder(builderClassName = "MockOrderLineBuilder",
            builderMethodName = "orderLine")
    private static OrderLine mOrderLine(
            final Long id,
            final Order order,
            final boolean shippingRequired,
            final int quantity,
            final int fulfilledQuantity,
            final int reservedQuantity
    ) {
        final OrderLine line = newEntity(OrderLine.class, id);
        setFieldIfNotNull(line, "shippingRequired", shippingRequired);
        setFieldIfNotNull(line, "quantity", quantity);
        setFieldIfNotNull(line, "fulfilledQuantity", fulfilledQuantity);
        setFieldIfNotNull(line, "reservedQuantity", reservedQuantity);

        addToParent(line, "order", order, "lines");
        return line;
    }

    @Builder(builderClassName = "MockOrderFulfillmentBuilder",
            builderMethodName = "orderFulfillment")
    private static OrderFulfillment mOrderFulfillment(
            final Long id,
            final Order order,
            final boolean completed
    ) {
        final OrderFulfillment fulfillment = newEntity(OrderFulfillment.class, id);
        setFieldIfNotNull(fulfillment, "completed", completed);

        addToParent(fulfillment, "order", order, "fulfillments");
        mockSaveChildren(fulfillment, "lines");
        return fulfillment;
    }

    @Builder(builderClassName = "MockOrderFulfillmentLineBuilder",
            builderMethodName = "orderFulfillmentLine")
    private static OrderFulfillmentLine mOrderFulfillmentLine(
            final Long id,
            final OrderFulfillment fulfillment,
            final OrderLine orderLine,
            final int quantity
    ) {
        final OrderFulfillmentLine fulfillmentLine = newEntity(OrderFulfillmentLine.class, id);
        setFieldIfNotNull(fulfillmentLine, "quantity", quantity);
        setUnidirectionalRelation(fulfillmentLine, "orderLine", orderLine);
        addToParent(fulfillmentLine, "fulfillment", fulfillment, "lines");
        return fulfillmentLine;
    }

    private static void setFieldIfNotNull(final Object obj, final String fieldName, final Object value) {
        if (value != null) {
            setField(obj, fieldName, value);
        }
    }

    private static void setNull(final Object obj, final String fieldName) {
        setField(obj, fieldName, null);
    }

    private static <T extends Entity<?>> T newEntity(final Class<T> clazz, final Object id) {
        final T entity = easyRandom.nextObject(clazz);
        setId(entity, id);
        return entity;
    }

    private static <T extends Entity<?>> void setUnidirectionalRelation(final Entity<?> owner, final String fieldName, T referenced) {
        if (referenced != null) {
            setField(owner, fieldName, referenced);
        } else {
            referenced = (T) getField(owner, fieldName);
            if (referenced != null) {
                setNewId(referenced);
            }
        }
    }

    private static <T extends Entity<?>> void setBidirectionalRelation(final Entity<?> entity1, final String entity1FieldName, T entity2, final String entity2FieldName) {
        if (entity2 != null) {
            setField(entity1, entity1FieldName, entity2);
            setField(entity2, entity2FieldName, entity1);
        } else {
            entity2 = (T) getField(entity1, entity1FieldName);
            if (entity2 != null) {
                setNewId(entity2);
                setField(entity2, entity2FieldName, entity1);
            }
        }
    }

    private static <T extends Entity<?>, P extends Entity<?>> void addToParent(
            @NonNull final T child,
            final String parentFieldName,
            P parent,
            final String childrenFieldName
    ) {
        final Consumer<P> addToParent = p -> {
            final Collection<T> collection = (Collection<T>) getField(p, childrenFieldName);
            requireNonNull(collection).add(child);
        };
        if (parent != null) {
            setField(child, parentFieldName, parent);
            addToParent.accept(parent);
        } else {
            parent = (P) getField(child, parentFieldName);
            if (parent != null) {
                setNewId(parent);
                addToParent.accept(parent);
            }
        }
    }

    private static <T extends Entity<?>> void mockSaveChildren(final T entity, final String collectionFieldName) {
         /* using reflection on spies seems to cause the following warnings:
         WARNING: An illegal reflective access operation has occurred
         WARNING: Illegal reflective access by org.mockito.internal.util.reflection.ReflectionMemberAccessor (file:/home/xylo/.m2/repository/org/mockito/mockito-core/3.6.28/mockito-core-3.6.28.jar) to field java.util.ArrayList.elementData
         WARNING: Please consider reporting this to the maintainers of org.mockito.internal.util.reflection.ReflectionMemberAccessor
         WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
         WARNING: All illegal access operations will be denied in a future release. */
        mockAdd((Collection<? extends Entity<?>>) spyField(entity, collectionFieldName));
    }

    // compilation problems when statically importing @Builder methods: https://github.com/rzwitserloot/lombok/issues/979

}
