package com.j2c.j2c.service.test;

import com.google.common.collect.ImmutableList;
import com.j2c.j2c.domain.entity.Entity;
import com.j2c.j2c.domain.repository.spring.*;
import com.j2c.j2c.service.mail.MailSender;
import com.j2c.j2c.service.image.ImageStore;
import com.j2c.j2c.service.gateway.PaymentGateway;
import lombok.Getter;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.List;

import static com.j2c.j2c.service.test.ReflectionUtils.getFieldsOfType;

@TestComponent
@Getter
public class MockBeanProvider {

    @MockBean
    private CheckoutLineSDJRepository checkoutLineRepository;

    @MockBean
    private CheckoutSDJRepository checkoutRepository;

    @MockBean
    private ConfigurationSDJRepository configurationRepository;

    @MockBean
    private UserVerificationTokenSDJRepository userVerificationTokenRepository;

    @MockBean
    private OrderFulfillmentSDJRepository orderFulfillmentRepository;

    @MockBean
    private OrderFulfillmentLineSDJRepository orderFulfillmentLineRepository;

    @MockBean
    private OrderLineSDJRepository orderLineRepository;

    @MockBean
    private OrderSDJRepository orderRepository;

    @MockBean
    private ProductCategorySDJRepository productCategoryRepository;

    @MockBean
    private ProductToTagAssociationSDJRepository productToTagAssociationRepository;

    @MockBean
    private ProductSDJRepository productRepository;

    @MockBean
    private ProductTagSDJRepository productTagRepository;

    @MockBean
    private ProductVariantImageSDJRepository productVariantImageRepository;

    @MockBean
    private ProductVariantSDJRepository productVariantRepository;

    @MockBean
    private RoleSDJRepository roleRepository;

    @MockBean
    private ShippingCountrySDJRepository shippingCountryRepository;

    @MockBean
    private ShippingMethodSDJRepository shippingMethodRepository;

    @MockBean
    private ShippingZoneSDJRepository shippingZoneRepository;

    @MockBean
    private UploadedImageSDJRepository uploadedImageRepository;

    @MockBean
    private UserAddressSDJRepository userAddressRepository;

    @MockBean
    private UserSDJRepository userRepository;

    @MockBean
    private ImageStore imageStore;

    @MockBean
    private PaymentGateway paymentGateway;

    @MockBean
    private MailSender mailSender;

    @Getter
    private static List<JpaRepository<? extends Entity<?>, ?>> repositories;

    @PostConstruct
    public void gatherRepositories() {
        repositories = getFieldsOfType(this.getClass(), JpaRepository.class).stream()
                .map(this::getRepositoryFromField)
                .collect(ImmutableList.toImmutableList());
    }

    @SuppressWarnings("unchecked")
    private <T extends Entity<?>> JpaRepository<T, ?> getRepositoryFromField(final Field field) {
        try {
            return (JpaRepository<T, ?>) field.get(this);
        } catch (final IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

}
