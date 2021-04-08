package com.j2c.j2c.web.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j2c.j2c.service.application.*;
import com.j2c.j2c.web.config.JacksonConfig;
import com.j2c.j2c.web.controller.JWTAuthenticationTest;
import com.j2c.j2c.web.security.config.DaoAuthenticationUserResolver;
import com.j2c.j2c.web.security.config.MethodSecurityConfig;
import com.j2c.j2c.web.security.filter.dao.DaoAuthenticationFailureHandler;
import com.j2c.j2c.web.security.filter.jwt.JWTAccessDeniedHandler;
import com.j2c.j2c.web.security.filter.jwt.JWTAuthenticationEntryPoint;
import com.j2c.j2c.web.security.filter.jwt.JWTVerifier;
import com.j2c.j2c.web.security.token.TokenProvider;
import org.hamcrest.core.StringContains;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        properties = {
                "spring.profiles.active=test",
                "j2c.web.security.jwt.secret=rbcjXddtz1kJnfAU1dAV0vX5PfM5r0bT/Q5F6xl+eSo7FyIJixsxIQ9Nv9JySxFl2qlvbqeOCAZ0wLGynryxwQ=="
        }
)
@Import({
        JWTAuthenticationTest.SecuredController.class,
        TokenProvider.class,
        DaoAuthenticationUserResolver.class,
        DaoAuthenticationFailureHandler.class,
        JWTVerifier.class,
        JWTAuthenticationEntryPoint.class,
        JWTAccessDeniedHandler.class,
        MethodSecurityConfig.class,
        NoOpPasswordEncoder.class,
        JacksonConfig.class,
})
public abstract class BaseWebMvcTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected CheckoutService checkoutService;

    @MockBean
    protected ConfigurationService configurationService;

    @MockBean
    protected ImageStorageService imageStorageService;

    @MockBean
    protected OrderService orderService;

    @MockBean
    protected ProductService productService;

    @MockBean
    protected ShippingService shippingService;

    @MockBean
    protected UserService userService;

    @Autowired
    protected ObjectMapper objectMapper;

    protected static final String BAD_REQUEST_BODY = "{ \"wrong_field_name\":\"wrong_field_value\" }";

    protected String toJson(final Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (final JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    protected void assert200(final RequestBuilder request) throws Exception {
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    protected void assert204(final RequestBuilder request) throws Exception {
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    protected void assert400(final RequestBuilder request) throws Exception {
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    protected void assert404(final RequestBuilder request) throws Exception {
        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    protected void assert422(final RequestBuilder request) throws Exception {
        mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
    }

    protected void assert401AuthenticationRequired(final RequestBuilder request) throws Exception {
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists(HttpHeaders.WWW_AUTHENTICATE))
                .andExpect(
                        header().string(
                                HttpHeaders.WWW_AUTHENTICATE,
                                new StringContains("Full authentication is required to access this resource")
                        )
                );
    }

    protected void assert403HigherPrivilegesRequired(final RequestBuilder request) throws Exception {
        mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(header().exists(HttpHeaders.WWW_AUTHENTICATE))
                .andExpect(
                        header().string(
                                HttpHeaders.WWW_AUTHENTICATE,
                                new StringContains("requires higher privileges than provided by the access token")
                        )
                );
    }

}
