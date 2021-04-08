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
import static com.j2c.j2c.domain.enums.Authorities.WRITE_CHECKOUT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class CheckoutControllerTest extends BaseWebMvcTest {

    @Test
    void getAll_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/checkouts");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void getAll_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/checkouts");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = READ_ACCESS)
    void getAll_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/checkouts");

        assert200(request);
    }

    @Test
    void get_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/checkouts/1");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void get_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/checkouts/1");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void get_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/checkouts/1");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = READ_ACCESS)
    void get_HasReadAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/checkouts/1");

        assert200(request);
    }

    @Test
    void getLines_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/checkouts/1/lines");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void getLines_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/checkouts/1/lines");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void getLines_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/checkouts/1/lines");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = READ_ACCESS)
    void getLines_HasReadAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/checkouts/1/lines");

        assert200(request);
    }

    @Test
    void getLine_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/checkouts/1/lines/1");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void getLine_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/checkouts/1/lines/1");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void getLine_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/checkouts/1/lines/1");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = READ_ACCESS)
    void getLine_HasReadAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/checkouts/1/lines/1");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1")
    void checkout_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/checkouts")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void checkout_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/checkouts")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void checkout_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/checkouts")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void checkout_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/checkouts")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.checkout(any(), any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void checkout_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/checkouts")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.checkout(any(), any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1")
    void checkout_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/checkouts")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.checkout(any(), any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1")
    void checkout_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/checkouts")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createShippingAddress_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createShippingAddress_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void createShippingAddress_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void createShippingAddress_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createShippingAddress_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.createShippingAddress(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createShippingAddress_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.createShippingAddress(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createShippingAddress_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.createShippingAddress(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createShippingAddress_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = WRITE_CHECKOUT)
    void createShippingAddress_HasCheckoutWriteAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateShippingAddress_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateShippingAddress_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void updateShippingAddress_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = patch("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void updateShippingAddress_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = patch("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateShippingAddress_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.updateShippingAddress(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateShippingAddress_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = patch("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.updateShippingAddress(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateShippingAddress_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = patch("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.updateShippingAddress(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateShippingAddress_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = WRITE_CHECKOUT)
    void updateShippingAddress_HasCheckoutWriteAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1")
    void setShippingAddress_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void setShippingAddress_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void setShippingAddress_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void setShippingAddress_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void setShippingAddress_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.setShippingAddress(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void setShippingAddress_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.setShippingAddress(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1")
    void setShippingAddress_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.setShippingAddress(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1")
    void setShippingAddress_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = WRITE_CHECKOUT)
    void setShippingAddress_HasCheckoutWriteAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/shipping-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1")
    void setShippingMethod_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/shipping-method")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void setShippingMethod_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/shipping-method")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void setShippingMethod_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/shipping-method")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void setShippingMethod_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/shipping-method")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void setShippingMethod_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/shipping-method")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.setShippingMethod(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void setShippingMethod_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/shipping-method")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.setShippingMethod(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1")
    void setShippingMethod_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/shipping-method")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.setShippingMethod(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1")
    void setShippingMethod_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/shipping-method")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = WRITE_CHECKOUT)
    void setShippingMethod_HasCheckoutWriteAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/shipping-method")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createAddress_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createAddress_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void createAddress_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void createAddress_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createAddress_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.createAddress(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createAddress_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.createAddress(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createAddress_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.createAddress(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createAddress_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = WRITE_CHECKOUT)
    void createAddress_HasCheckoutWriteAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateAddress_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateAddress_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void updateAddress_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = patch("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void updateAddress_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = patch("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateAddress_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.updateAddress(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateAddress_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = patch("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.updateAddress(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateAddress_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = patch("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.updateAddress(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateAddress_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = WRITE_CHECKOUT)
    void updateAddress_HasCheckoutWriteAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1")
    void setAddress_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void setAddress_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void setAddress_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void setAddress_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void setAddress_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.setAddress(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void setAddress_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.setAddress(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1")
    void setAddress_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.setAddress(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1")
    void setAddress_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = WRITE_CHECKOUT)
    void setAddress_HasCheckoutWriteAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = put("/api/checkouts/1/address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1")
    void useSingleAddress_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/single-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void useSingleAddress_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/single-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void useSingleAddress_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/single-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void useSingleAddress_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/single-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void useSingleAddress_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/single-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.useSingleAddress(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1")
    void useSingleAddress_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/single-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.useSingleAddress(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1")
    void useSingleAddress_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/single-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(checkoutService.useSingleAddress(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1")
    void useSingleAddress_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/single-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = WRITE_CHECKOUT)
    void useSingleAddress_HasCheckoutWriteAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/single-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    void complete_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/complete")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void complete_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/single-address")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void complete_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/complete")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(checkoutService.complete(any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1")
    void complete_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/complete")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(checkoutService.complete(any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1")
    void complete_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/complete")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert200(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = WRITE_CHECKOUT)
    void complete_HasCheckoutWriteAccess_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/checkouts/1/complete")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    void cancel_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = delete("/api/checkouts/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "2")
    void cancel_IsNotOwner_ShouldRespond403() throws Exception {
        final RequestBuilder request = delete("/api/checkouts/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void cancel_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = delete("/api/checkouts/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ResourceNotFoundException())
                .when(checkoutService).cancel(any());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1")
    void cancel_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = delete("/api/checkouts/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ServiceException())
                .when(checkoutService).cancel(any());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1")
    void cancel_HappyPath_ShouldRespond204() throws Exception {
        final RequestBuilder request = delete("/api/checkouts/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert204(request);
    }

    @Test
    @WithMockUser(value = "2", authorities = WRITE_CHECKOUT)
    void cancel_HasCheckoutWriteAccess_ShouldRespond204() throws Exception {
        final RequestBuilder request = delete("/api/checkouts/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert204(request);
    }

}
