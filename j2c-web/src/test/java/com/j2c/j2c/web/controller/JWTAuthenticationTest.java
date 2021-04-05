package com.j2c.j2c.web.controller;

import com.j2c.j2c.web.test.BaseWebMvcTest;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.j2c.j2c.web.controller.JWTAuthenticationTest.SecuredController.TEST_URI;
import static com.j2c.j2c.web.security.util.AuthenticationUtils.getUserId;
import static com.j2c.j2c.web.security.util.AuthenticationUtils.isAnonymous;
import static com.j2c.j2c.web.util.WebConstants.Bearer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JWTAuthenticationTest extends BaseWebMvcTest {

    public static final String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aG9yaXRpZXMiOlsiVEVTVCJdfQ.qSVQ1nAqmO5hZg-LF0nvHbkZ4jwk4roXP8w3eOhZbI-6RZJVcePyoq3ZFYiJPshGdpYMfIOL8gJwzkgWZUaPiQ";
    private static final String insufficientScopeToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aG9yaXRpZXMiOltdfQ.ZHoWqErOcAViKjuQeYRIsUix3dtWHI_wSgqJaORJpRN9n7D-z2IkmuZkiyc7Vwu4zAhfTrZfQe-yEad43J2vPQ";
    private static final String expiredToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNjEzNjk2ODQ5LCJhdXRob3JpdGllcyI6WyJURVNUIl19.0F2Hw6Wpy_bwfiFceO3B4K2ikVDe5JjTR93s2jn0lqX-1hcWapVfRftcuhz3T3HI71noisaJdxROa7Dk4jyqFQ";
    private static final String invalidSignatureToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aG9yaXRpZXMiOlsiVEVTVCJdfQ.o-7x2Gzg3OFqPoerKfvJkTuyiac3Y2EizHeRxC_ujsGVFDUHzLXZDlLsnY1TsGNr3tUx1jOAjuNk3LVeMHkDQQ";

    @BeforeAll
    void beforeAll() throws Exception {
        final RequestBuilder request = get(TEST_URI);

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

    @Test
    public void JWTAuthentication_ValidToken_ShouldRespond2xxWithRequestedResource() throws Exception {
        final RequestBuilder request = get(TEST_URI)
                .header(HttpHeaders.AUTHORIZATION, Bearer + token);

        mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("Secured controller reached! Current user is 1"));
    }

    @Test
    public void JWTAuthentication_NoToken_ShouldRespond401AuthenticationRequired() throws Exception {
        final RequestBuilder request = get(TEST_URI);

        assert401AuthenticationRequired(request);
    }

    @Test
    public void JWTAuthentication_TokenWithInsufficientScope_ShouldRespond403HigherPrivilegesRequired() throws Exception {
        final RequestBuilder request = get(TEST_URI)
                .header(HttpHeaders.AUTHORIZATION, Bearer + insufficientScopeToken);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    public void JWTAuthentication_TokenWithoutPrefix_ShouldRespond401InvalidAuthorizationHeader() throws Exception {
        final RequestBuilder request = get(TEST_URI)
                .header(HttpHeaders.AUTHORIZATION, token);

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists(HttpHeaders.WWW_AUTHENTICATE))
                .andExpect(
                        header().string(
                                HttpHeaders.WWW_AUTHENTICATE,
                                new StringContains("Expected token prefix value of 'Bearer'")
                        )
                );
    }

    @Test
    public void JWTAuthentication_RequestWithExpiredToken_ShouldRespond401ExpiredToken() throws Exception {
        final RequestBuilder request = get(TEST_URI)
                .header(HttpHeaders.AUTHORIZATION, Bearer + expiredToken);

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists(HttpHeaders.WWW_AUTHENTICATE))
                .andExpect(
                        header().string(
                                HttpHeaders.WWW_AUTHENTICATE,
                                new StringContains("Token expired")
                        )
                );
    }

    @Test
    public void JWTAuthentication_RequestWithMalformedToken_ShouldRespond401MalformedToken() throws Exception {
        final RequestBuilder request = get(TEST_URI)
                .header(HttpHeaders.AUTHORIZATION, Bearer + "this.is_a_malformed_token");

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists(HttpHeaders.WWW_AUTHENTICATE))
                .andExpect(
                        header().string(
                                HttpHeaders.WWW_AUTHENTICATE,
                                new StringContains("Malformed token")
                        )
                );
    }

    @Test
    public void JWTAuthentication_InvalidSignatureToken_ShouldRespond401InvalidToken() throws Exception {
        final RequestBuilder request = get(TEST_URI)
                .header(HttpHeaders.AUTHORIZATION, Bearer + invalidSignatureToken);

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists(HttpHeaders.WWW_AUTHENTICATE))
                .andExpect(
                        header().string(
                                HttpHeaders.WWW_AUTHENTICATE,
                                new StringContains("Invalid token signature")
                        )
                );
    }

    @RestController
    public static class SecuredController {

        public static final String TEST_URI = "/api/test";

        @GetMapping(TEST_URI)
        @PreAuthorize("hasAuthority('TEST')")
        public String secured() {
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = "anonymous";
            if (!isAnonymous(authentication)) {
                principal = getUserId(authentication);
            }
            return String.format("Secured controller reached! Current user is %s", principal);
        }

    }

}
