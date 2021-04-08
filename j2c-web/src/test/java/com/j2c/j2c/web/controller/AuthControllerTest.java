package com.j2c.j2c.web.controller;

import com.j2c.j2c.domain.enums.RoleType;
import com.j2c.j2c.service.dto.RoleDTO;
import com.j2c.j2c.service.dto.UserDTO;
import com.j2c.j2c.service.exception.ResourceNotFoundException;
import com.j2c.j2c.web.input.AuthRequest;
import com.j2c.j2c.web.test.BaseWebMvcTest;
import com.j2c.j2c.web.util.WebConstants;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Collections;
import java.util.Map;

import static com.j2c.j2c.web.util.WebConstants.Bearer;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends BaseWebMvcTest {

    private static final UserDTO existingUser = UserDTO.builder()
            .id(1L)
            .email("admin@test.com")
            .password("1q2w3e4r5t6y7u8i9o0p")
            .role(
                    RoleDTO.builder()
                            .type(RoleType.Customer)
                            .authorities(Collections.emptySet())
                            .build()
            )
            .build();

    @Test
    void authenticate_ValidRequestForExistingUser_ShouldRespond204WithTokenHeader() throws Exception {
        final RequestBuilder request = post(WebConstants.AuthURI)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        toJson(
                                AuthRequest.builder()
                                        .email(existingUser.getEmail())
                                        .password(existingUser.getPassword())
                                        .build()
                        )
                );

        when(userService.findWithAuthenticationDetails(existingUser.getEmail()))
                .thenReturn(existingUser);

        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, new StringStartsWith(Bearer)));
    }

    @Test
    public void authenticate_WrongPassword_ShouldRespond401BadCredentials() throws Exception {
        final RequestBuilder request = post(WebConstants.AuthURI)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        toJson(
                                AuthRequest.builder()
                                        .email(existingUser.getEmail())
                                        .password("wrong_password123")
                                        .build()
                        )
                );

        when(userService.findWithAuthenticationDetails(existingUser.getEmail()))
                .thenReturn(existingUser);

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("Bad credentials"));
    }

    @Test
    public void authenticate_UserDoesNotExist_ShouldRespond401BadCredentials() throws Exception {
        final RequestBuilder request = post(WebConstants.AuthURI)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        toJson(
                                AuthRequest.builder()
                                        .email("a_user_with_this_email_does_not_exist@test.com")
                                        .password(existingUser.getPassword())
                                        .build()
                        )
                );

        when(userService.findWithAuthenticationDetails(not(eq(existingUser.getEmail()))))
                .thenThrow(new ResourceNotFoundException());

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("Bad credentials"));
    }

    @Test
    public void authenticate_AdditionalField_ShouldRespond400InvalidRequest() throws Exception {
        final RequestBuilder request = post(WebConstants.AuthURI)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        toJson(
                                Map.of(
                                        "wrong_field_name", existingUser.getEmail(),
                                        "email", existingUser.getEmail(),
                                        "password", existingUser.getPassword()
                                )
                        )
                );

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Request must contain only 'email' and 'password' fields"));
    }

    @Test
    public void authenticate_NullEmail_ShouldRespond400InvalidRequest() throws Exception {
        final RequestBuilder request = post(WebConstants.AuthURI)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        toJson(
                                AuthRequest.builder()
                                        .password(existingUser.getPassword())
                                        .build()
                        )
                );

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Invalid request: email must not be blank"));
    }

    @Test
    public void authenticate_BlankEmail_ShouldRespond400InvalidRequest() throws Exception {
        final RequestBuilder request = post(WebConstants.AuthURI)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        toJson(
                                AuthRequest.builder()
                                        .email("")
                                        .password(existingUser.getPassword())
                                        .build()
                        )
                );

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Invalid request: email must not be blank"));
    }

    @Test
    public void authenticate_InvalidEmail_ShouldRespond400InvalidRequest() throws Exception {
        final RequestBuilder request = post(WebConstants.AuthURI)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        toJson(
                                AuthRequest.builder()
                                        .email("this is an invalid email !")
                                        .password(existingUser.getPassword())
                                        .build()
                        )
                );

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Invalid request: email must be a well-formed email address"));
    }

    @Test
    public void authenticate_NullPassword_ShouldRespond400InvalidRequest() throws Exception {
        final RequestBuilder request = post(WebConstants.AuthURI)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        toJson(
                                AuthRequest.builder()
                                        .email(existingUser.getEmail())
                                        .build()
                        )
                );

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Invalid request: password must not be blank"));
    }

    @Test
    public void authenticate_BlankPassword_ShouldRespond400InvalidRequest() throws Exception {
        final RequestBuilder request = post(WebConstants.AuthURI)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(
                        toJson(
                                AuthRequest.builder()
                                        .email(existingUser.getEmail())
                                        .password("")
                                        .build()
                        )
                );

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Invalid request: password must not be blank"));
    }

    @Test
    void refresh_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/refresh")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void refresh_Authenticated_ShouldRespond204() throws Exception {
        final RequestBuilder request = post("/api/refresh")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, new StringStartsWith(Bearer)));
    }

    @Test
    void anonymous_HappyPath_ShouldRespond200WithTokenHeader() throws Exception {
        final RequestBuilder request = post("/api/anonymous");

        when(userService.create(any()))
                .thenReturn(
                        UserDTO.builder()
                                .id(1L)
                                .build()
                );

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, new StringStartsWith(Bearer)));
    }

}
