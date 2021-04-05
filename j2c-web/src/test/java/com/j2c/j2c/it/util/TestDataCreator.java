package com.j2c.j2c.it.util;

import com.j2c.j2c.domain.enums.RoleType;
import com.j2c.j2c.domain.enums.ShippingMethodType;
import com.j2c.j2c.service.application.*;
import com.j2c.j2c.service.dto.*;
import com.j2c.j2c.service.input.*;
import com.neovisionaries.i18n.CountryCode;
import lombok.NonNull;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@TestComponent
public class TestDataCreator {

    @Autowired
    protected Flyway flyway;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ShippingService shippingService;

    @Autowired
    protected ProductService productService;

    @Autowired
    protected CheckoutService checkoutService;

    @Autowired
    protected OrderService orderService;

    @Autowired
    protected ImageStorageService imageStorageService;

    private static final Set<String> alreadyUsedEmails = new HashSet<>();

    @EventListener
    public void onApplicationReadyEvent(final ApplicationReadyEvent event) {
        emptyDB();

        createInitialTestData();
    }

    public UserDTO createUserWithUniqueEmail() {
        return userService.create(
                CreateUserForm.builder()
                        .email(newRandomEmail())
                        .password("password")
                        .role(RoleType.Customer)
                        .enabled(true)
                        .verified(true)
                        .build()
        );
    }

    public CheckoutDTO createCheckoutForCustomer(@NonNull final UserDTO customer) {
        return checkoutService.checkout(
                customer.getId(),
                "127.0.0.1",
                CreateCheckoutForm.builder()
                        .email("customer@j2c.com")
                        .lines(
                                List.of( // variant with id 1 is the one created in 'createInitialTestData'!
                                        Line.builder().id(1L).quantity(1).build()
                                )
                        )
                        .build()
        );
    }

    public UserAddressDTO createAddressForUser(@NonNull final UserDTO user) {
        return userService.createAddress(
                user.getId(),
                CreateAddressForm.builder()
                        .firstName("First Name")
                        .lastName("Last Name")
                        .streetAddress1("Street Address 1")
                        .country(CountryCode.IT)
                        .countryArea("Country Area")
                        .city("City")
                        .postalCode("ABC123")
                        .phone1("123456789")
                        .build()
        );
    }

    public ShippingMethodDTO createValidShippingMethodForCheckout(final CountryCode country, final CheckoutDTO checkout) {
        final ShippingZoneDTO shippingZone = shippingService.createZone(
                CreateShippingZoneForm.builder()
                        .name("Test Shipping Zone")
                        .countries(Set.of(country))
                        .build()
        );
        return shippingService.createMethod(
                shippingZone.getId(),
                CreateShippingMethodForm.builder()
                        .name("Test Shipping Method")
                        .type(ShippingMethodType.Price)
                        .min(0L)
                        .max(checkout.getPrice() + 500L)
                        .rate(50L)
                        .build()
        );
    }

    public CheckoutDTO createCheckoutReadyForPurchase(final UserDTO customer) {
        CheckoutDTO checkout = createCheckoutForCustomer(customer);
        final Page<ShippingCountryDTO> unusedCountries = shippingService.findAllUnusedCountries(PageRequest.of(0, 3));
        checkoutService.createShippingAddress(
                checkout.getId(),
                CreateCheckoutShippingAddressForm.builder()
                        .address(
                                CreateAddressForm.builder()
                                        .firstName("First Name")
                                        .lastName("Last Name")
                                        .streetAddress1("Street Address 1")
                                        .country(unusedCountries.getContent().get(0).getCode())
                                        .countryArea("Country Area")
                                        .city("City")
                                        .postalCode("ABC123")
                                        .phone1("123456789")
                                        .build()
                        )
                        .build()
        );
        checkout = checkoutService.useSingleAddress(checkout.getId(), UseSingleAddressForm.builder().useSingleAddress(true).build());
        final ShippingMethodDTO shippingMethod = createValidShippingMethodForCheckout(checkout.getAddress().getCountry(), checkout);
        return checkoutService.setShippingMethod(checkout.getId(), SetCheckoutShippingMethodForm.builder().shippingMethodId(shippingMethod.getId()).build());
    }

    public OrderDTO createUnconfirmedOrderForCustomer(final UserDTO customer) {
        final CheckoutDTO checkout = createCheckoutReadyForPurchase(customer);
        return checkoutService.complete(checkout.getId());
    }

    public OrderFulfillmentDTO createSingleFinalizingFulfillmentForOrder(@NonNull final OrderDTO order) {
        // 'single finalizing fulfillment' = fulfillment that is the sole fulfillment for the order,
        //  and as such fulfills all of the order's lines
        final List<OrderLineDTO> orderLines = Objects.requireNonNull(
                order.getLines(),
                "OrderDTO must have a reference to all its lines! (result of checkoutService.complete)"
        );
        return orderService.createFulfillment(
                order.getId(),
                orderLines.stream()
                        .map(
                                orderLine -> Line.builder()
                                        .id(orderLine.getId())
                                        .quantity(orderLine.getQuantity())
                                        .build()
                        )
                        .collect(Collectors.toList())
        ).getFulfillment();
    }

    public ProductDTO createProduct() {
        return productService.create(
                CreateProductForm.builder()
                        .name("Test Product " + randomString(6))
                        .digital(false)
                        .mass(100)
                        .price(100L)
                        .build()
        );
    }

    public ProductVariantDTO createVariantForProduct(final ProductDTO product) {
        return productService.createVariant(
                product.getId(),
                CreateProductVariantForm.builder()
                        .defaultVariantName("Default Test Variant Name " + randomString(6))
                        .name("Test Variant " + randomString(6))
                        .mass(100)
                        .build()
        );
    }

    public ProductCategoryDTO createCategory() {
        return productService.createCategory(
                CreateProductCategoryForm.builder()
                        .name("Test Category " + randomString(6))
                        .build()
        );
    }

    public ProductTagDTO createTag() {
        return productService.createTag(
                CreateProductTagForm.builder()
                        .name("Test Tag " + randomString(6))
                        .build()
        );
    }

    public ShippingZoneDTO createShippingZone() {
        return shippingService.createZone(
                CreateShippingZoneForm.builder()
                        .name("Test Shipping Zone " + randomString(6))
                        .build()
        );
    }

    public ShippingMethodDTO createShippingMethodForZone(final ShippingZoneDTO zone) {
        return shippingService.createMethod(
                zone.getId(),
                CreateShippingMethodForm.builder()
                        .name("Test Shipping Method " + randomString(6))
                        .type(ShippingMethodType.Price)
                        .min(0L)
                        .max(1000L)
                        .rate(50L)
                        .build()
        );
    }

    public String newRandomEmail() {
        while (true) {
            final String email = randomString(10) + "@j2c.com";
            if (!alreadyUsedEmails.contains(email)) {
                alreadyUsedEmails.add(email);
                return email;
            }
        }
    }

    private void emptyDB() {
        flyway.clean();
        flyway.migrate();
    }

    private void createInitialTestData() {
        final ProductCategoryDTO category = productService.createCategory(
                CreateProductCategoryForm.builder()
                        .name("Electronics")
                        .description("Category description.")
                        .build()
        );
        final ProductCategoryDTO subCategory = productService.createSubCategory(
                category.getId(),
                CreateProductCategoryForm.builder()
                        .name("Computers")
                        .description("Sub Category description.")
                        .build()
        );
        final ProductTagDTO tag = productService.createTag(
                CreateProductTagForm.builder()
                        .name("PC")
                        .build()
        );
        final ProductDTO product = createPublishedProductWithVariantImage(
                CreateProductForm.builder()
                        .name("Laptop")
                        .description("Product description.")
                        .digital(false)
                        .mass(100)
                        .price(300L)
                        .categoryId(subCategory.getId())
                        .tagIds(Set.of(tag.getId()))
                        .build()
        );
        final UserDTO user = userService.create(
                CreateUserForm.builder()
                        .email("admin@j2c.com")
                        .password("admin")
                        .enabled(true)
                        .verified(true)
                        .role(RoleType.Admin)
                        .build()
        );
        final UserAddressDTO userAddress = userService.createAddress(
                user.getId(),
                CreateAddressForm.builder()
                        .firstName("Jennifer")
                        .lastName("Jaytoocee")
                        .streetAddress1("Jay str.")
                        .country(CountryCode.HU)
                        .countryArea("Too")
                        .city("Cee")
                        .postalCode("ABC123")
                        .phone1("123456789")
                        .build()
        );
        final ShippingZoneDTO shippingZone = shippingService.createZone(
                CreateShippingZoneForm.builder()
                        .name("Hungary")
                        .countries(Set.of(userAddress.getAddress().getCountry()))
                        .build()
        );
        final ShippingMethodDTO shippingMethod = shippingService.createMethod(
                shippingZone.getId(),
                CreateShippingMethodForm.builder()
                        .name("CLS Standard")
                        .type(ShippingMethodType.Price)
                        .min(50L)
                        .max(1000L)
                        .rate(350L)
                        .build()
        );
        createOrderAndFulfillment(
                user.getId(),
                CreateCheckoutForm.builder()
                        .email(user.getEmail())
                        .lines(
                                List.of(
                                        Line.builder().id(product.getDefaultVariantId()).quantity(1).build()
                                )
                        )
                        .build(),
                userAddress,
                shippingMethod
        );
        checkoutService.checkout(
                user.getId(),
                "127.0.0.1",
                CreateCheckoutForm.builder()
                        .email(user.getEmail())
                        .lines(
                                List.of(
                                        Line.builder().id(product.getDefaultVariantId()).quantity(1).build()
                                )
                        )
                        .build()
        );
    }

    private ProductDTO createPublishedProductWithVariantImage(final CreateProductForm form) {
        final ProductDTO product = productService.create(form);
        productService.updateVariant(
                product.getId(),
                product.getDefaultVariant().getId(),
                UpdateProductVariantForm.builder()
                        .imagesToAddIds(
                                Set.of(imageStorageService.store(getSampleImage()).getId())
                        )
                        .build()
        );
        return productService.publish(product.getId());
    }

    private void createOrderAndFulfillment(
            final Long customerId,
            final CreateCheckoutForm form,
            final UserAddressDTO address,
            final ShippingMethodDTO method
    ) {
        final CheckoutDTO checkout = checkoutService.checkout(customerId, "127.0.0.1", form);
        checkoutService.setShippingAddress(
                checkout.getId(),
                SetCheckoutShippingAddressForm.builder()
                        .addressId(address.getId())
                        .build()
        );
        checkoutService.setShippingMethod(checkout.getId(), SetCheckoutShippingMethodForm.builder().shippingMethodId(method.getId()).build());
        checkoutService.useSingleAddress(
                checkout.getId(),
                UseSingleAddressForm.builder()
                        .useSingleAddress(true)
                        .build()
        );
        final OrderDTO order = checkoutService.complete(checkout.getId());
        orderService.confirm(order.getId());
        orderService.createFulfillment(
                order.getId(),
                List.of(
                        Line.builder().id(order.getLines().get(0).getId()).quantity(1).build()
                )
        );
    }

    private InputStream getSampleImage() {
        try {
            return new ClassPathResource("image.png").getInputStream();
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static String randomString(final int length) {
        final int leftLimit = 97; // letter 'a'
        final int rightLimit = 122; // letter 'z'
        final Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
