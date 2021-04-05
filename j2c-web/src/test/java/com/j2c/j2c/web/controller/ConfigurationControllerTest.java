package com.j2c.j2c.web.controller;

import com.j2c.j2c.web.test.BaseWebMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.j2c.j2c.domain.enums.Authorities.CONFIG;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

class ConfigurationControllerTest extends BaseWebMvcTest {

    @Test
    void get() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/configuration");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = CONFIG)
    void configure_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/configuration")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = CONFIG)
    void configure_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/configuration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void configure_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = patch("/api/configuration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void configure_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = patch("/api/configuration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = CONFIG)
    void configure_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/configuration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

}
