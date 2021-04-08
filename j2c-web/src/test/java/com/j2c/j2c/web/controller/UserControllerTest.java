package com.j2c.j2c.web.controller;

import com.j2c.j2c.service.exception.InvalidInputException;
import com.j2c.j2c.service.exception.ResourceNotFoundException;
import com.j2c.j2c.service.exception.ServiceException;
import com.j2c.j2c.web.test.BaseWebMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static com.j2c.j2c.domain.enums.Authorities.READ_ACCESS;
import static com.j2c.j2c.domain.enums.Authorities.WRITE_USERS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class UserControllerTest extends BaseWebMvcTest {

    @Test
    void getAll_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void getAll_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = READ_ACCESS)
    void getAll_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users");

        assert200(request);
    }

    @Test
    void emailExists_UserWithEmailDoesNotExist_ShouldRespond404() throws Exception {
        final RequestBuilder request = head("/api/users")
                .param("email", "admin@j2c.com");

        when(userService.exists(any()))
                .thenReturn(false);

        assert404(request);
    }

    @Test
    void emailExists_HappyPath_ShouldRespond204() throws Exception {
        final RequestBuilder request = head("/api/users")
                .param("email", "admin@j2c.com");

        when(userService.exists(any()))
                .thenReturn(true);

        assert204(request);
    }

    @Test
    void get_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void get_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void get_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = READ_ACCESS)
    void get_HasReadAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1");

        assert200(request);
    }

    @Test
    void getAddresses_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/addresses");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void getAddresses_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/addresses");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void getAddresses_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/addresses");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = READ_ACCESS)
    void getAddresses_HasReadAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/addresses");

        assert200(request);
    }

    @Test
    void getAddress_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/addresses/1");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void getAddress_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/addresses/1");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void getAddress_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/addresses/1");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = READ_ACCESS)
    void getAddress_HasReadAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/addresses/1");

        assert200(request);
    }

    @Test
    void getOrders_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/orders");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void getOrders_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/orders");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void getOrders_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/orders");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = READ_ACCESS)
    void getOrders_HasReadAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/orders");

        assert200(request);
    }

    @Test
    void getOrderLines_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/orders/1/lines");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void getOrderLines_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/orders/1/lines");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void getOrderLines_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/orders/1/lines");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = READ_ACCESS)
    void getOrderLines_HasReadAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/orders/1/lines");

        assert200(request);
    }

    @Test
    void getPaymentMethods_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/payment-methods");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void getPaymentMethods_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/payment-methods");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void getPaymentMethods_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/payment-methods");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = READ_ACCESS)
    void getPaymentMethods_HasReadAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/users/1/payment-methods");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_USERS)
    void create_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_USERS)
    void create_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void create_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void create_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_USERS)
    void create_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.create(any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_USERS)
    void create_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.create(any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_USERS)
    void create_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.create(any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_USERS)
    void create_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_USERS)
    void update_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_USERS)
    void update_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void update_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = patch("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void update_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = patch("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_USERS)
    void update_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.update(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_USERS)
    void update_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = patch("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.update(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_USERS)
    void update_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = patch("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.update(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_USERS)
    void update_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    void delete_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void delete_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void delete_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ResourceNotFoundException())
                .when(userService).delete(any());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1")
    void delete_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ServiceException())
                .when(userService).delete(any());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1")
    void delete_HappyPath_ShouldRespond204() throws Exception {
        final RequestBuilder request = delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert204(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = WRITE_USERS)
    void delete_HasWriteAccess_ShouldRespond204() throws Exception {
        final RequestBuilder request = delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert204(request);
    }

    @Test
    void signUp_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    void signUp_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void signUp_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.signUp(any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    void signUp_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.signUp(any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    void signUp_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.signUp(any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    void signUp_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    void verify_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/verify")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    void verify_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/verify")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void verify_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/verify")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.verify(any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    void verify_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/verify")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.verify(any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    void verify_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/verify")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.verify(any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    void verify_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/verify")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1")
    void changeEmail_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/users/1/email")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void changeEmail_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/users/1/email")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void changeEmail_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = patch("/api/users/1/email")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void changeEmail_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = patch("/api/users/1/email")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void changeEmail_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/users/1/email")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.changeEmail(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void changeEmail_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = patch("/api/users/1/email")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.changeEmail(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1")
    void changeEmail_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = patch("/api/users/1/email")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.changeEmail(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1")
    void changeEmail_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/users/1/email")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = WRITE_USERS)
    void changeEmail_HasWriteAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/users/1/email")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1")
    void changePassword_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/users/1/password")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void changePassword_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/users/1/password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void changePassword_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = patch("/api/users/1/password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void changePassword_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = patch("/api/users/1/password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void changePassword_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/users/1/password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.changePassword(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void changePassword_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = patch("/api/users/1/password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.changePassword(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1")
    void changePassword_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = patch("/api/users/1/password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.changePassword(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1")
    void changePassword_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/users/1/password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = WRITE_USERS)
    void changePassword_HasWriteAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/users/1/password")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createAddress_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/users/1/addresses")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createAddress_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/users/1/addresses")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void createAddress_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/users/1/addresses")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void createAddress_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/users/1/addresses")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createAddress_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/users/1/addresses")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.createAddress(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createAddress_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/users/1/addresses")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.createAddress(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createAddress_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/users/1/addresses")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.createAddress(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createAddress_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/users/1/addresses")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = WRITE_USERS)
    void createAddress_HasWriteAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/users/1/addresses")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateAddress_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/users/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateAddress_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/users/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void updateAddress_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = patch("/api/users/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void updateAddress_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = patch("/api/users/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateAddress_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/users/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.updateAddress(any(), any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateAddress_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = patch("/api/users/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.updateAddress(any(), any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateAddress_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = patch("/api/users/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(userService.updateAddress(any(), any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateAddress_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/users/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = WRITE_USERS)
    void updateAddress_HasWriteAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/users/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    void deleteAddress_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = delete("/api/users/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void deleteAddress_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = delete("/api/users/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void deleteAddress_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = delete("/api/users/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ResourceNotFoundException())
                .when(userService).deleteAddress(any(), any());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1")
    void deleteAddress_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = delete("/api/users/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ServiceException())
                .when(userService).deleteAddress(any(), any());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1")
    void deleteAddress_HappyPath_ShouldRespond204() throws Exception {
        final RequestBuilder request = delete("/api/users/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert204(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = WRITE_USERS)
    void deleteAddress_HasWriteAccess_ShouldRespond204() throws Exception {
        final RequestBuilder request = delete("/api/users/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert204(request);
    }

    @Test
    void deletePaymentMethod_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = delete("/api/users/1/payment-methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void deletePaymentMethod_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = delete("/api/users/1/payment-methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void deletePaymentMethod_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = delete("/api/users/1/payment-methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ResourceNotFoundException())
                .when(userService).deletePaymentMethod(any(), any());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1")
    void deletePaymentMethod_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = delete("/api/users/1/payment-methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ServiceException())
                .when(userService).deletePaymentMethod(any(), any());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1")
    void deletePaymentMethod_HappyPath_ShouldRespond204() throws Exception {
        final RequestBuilder request = delete("/api/users/1/payment-methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert204(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = WRITE_USERS)
    void deletePaymentMethod_HasWriteAccess_ShouldRespond204() throws Exception {
        final RequestBuilder request = delete("/api/users/1/payment-methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert204(request);
    }

}
