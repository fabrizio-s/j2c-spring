package com.j2c.j2c.it.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j2c.j2c.service.application.*;
import com.j2c.j2c.web.input.AuthRequest;
import com.j2c.j2c.web.security.token.TokenProvider;
import com.j2c.j2c.web.util.WebConstants;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        properties = {
                "spring.profiles.active=test",
                "spring.mail.host=", // blank value will use NoOpMailSender implementation
                "j2c.web.create-default-admin=false",
                "j2c.web.security.jwt.secret=rbcjXddtz1kJnfAU1dAV0vX5PfM5r0bT/Q5F6xl+eSo7FyIJixsxIQ9Nv9JySxFl2qlvbqeOCAZ0wLGynryxwQ==",
                "j2c.service.storage.image.filesystem.location=${J2C_TEST_LOCAL_IMAGE_STORAGE_PATH}",
                "j2c.service.gateway.stripe.key=${J2C_STRIPE_TEST_KEY}",
                "spring.datasource.url=${J2C_TEST_DB_URL}",
                "spring.datasource.username=${J2C_TEST_DB_USERNAME}",
                "spring.datasource.password=${J2C_TEST_DB_PASSWORD}",
                "spring.flyway.url=${J2C_TEST_DB_URL}",
                "spring.flyway.user=${J2C_TEST_DB_USERNAME}",
                "spring.flyway.password=${J2C_TEST_DB_PASSWORD}",
                "spring.flyway.locations=filesystem:../j2c-domain/src/main/db/postgresql/migrations/",
                "spring.flyway.cleanOnValidationError=true",
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Import({
        MockPaymentGateway.class, // PaymentGateway mock implementation
        TestDataCreator.class
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIT {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected TestDataCreator testDataCreator;

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

    @Autowired
    protected TokenProvider tokenProvider;

    protected String baseUrl;

    protected String adminToken;

    @TestConfiguration
    static class ITConfiguration {

        @Bean
        public FlywayMigrationStrategy flywayMigrationStrategy() {
            return flyway -> {};
        }

    }

    @BeforeAll
    void beforeAll() {
        baseUrl = "http://localhost:" + port;

        adminToken = authenticate("admin@j2c.com", "admin");

        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    protected ResponseEntity<JsonNode> httpRequest(
            @NonNull final String url,
            @NonNull final HttpMethod httpMethod,
            final Object body,
            final String token
    ) {
        final ResponseEntity<JsonNode> response = restTemplate.exchange(
                url,
                httpMethod,
                new HttpEntity<>(body, getTokenHeaders(token)),
                JsonNode.class
        );
        prettyPrintJson(response);
        return response;
    }

    protected void prettyPrintJson(@NonNull final Object obj) {
        try {
            final String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            System.out.println(json); // pretty print the json response
        } catch (final JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    protected <T> void assertBodyIsOfType(final JsonNode body, @NonNull final Class<T> type) {
        assertNotNull(body);
        assertDoesNotThrow(() -> {
            final T t = objectMapper.convertValue(body, type);
            assertEquals(type, t.getClass());
        });
    }

    protected <T> void assertPageOfTypeIsNotEmpty(JsonNode node, @NonNull final Class<T> type) {
        assertNotNull(node);
        node = node.get("content");
        assertNotNull(node);
        assertListOfTypeIsNotEmpty(node, type);
    }

    protected <T> void assertListOfTypeIsNotEmpty(final JsonNode node, @NonNull final Class<T> type) {
        try {
            final List<T> list = ((List<?>) objectMapper.treeToValue(node, List.class)).stream()
                    .map(obj -> objectMapper.convertValue(obj, type))
                    .collect(Collectors.toList());
            list.forEach(a -> assertEquals(a.getClass(), type));
            assertFalse(list.isEmpty());
        } catch (final JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    protected String authenticate(final String email, final String password) {
        final AuthRequest authRequest = AuthRequest.builder()
                .email(email)
                .password(password)
                .build();
        final ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/api/authentication", authRequest, String.class);
        final List<String> headers = response.getHeaders().get(HttpHeaders.AUTHORIZATION);
        final String tokenWithPrefix = Objects.requireNonNull(headers).get(0);
        return tokenWithPrefix.replaceFirst(WebConstants.Bearer, "");
    }

    private HttpHeaders getTokenHeaders(final String token) {
        final HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.setBearerAuth(token);
        }
        return headers;
    }

}
