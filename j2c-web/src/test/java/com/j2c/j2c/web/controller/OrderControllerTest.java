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

import static com.j2c.j2c.domain.enums.Authorities.PROCESS_ORDERS;
import static com.j2c.j2c.domain.enums.Authorities.READ_ACCESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class OrderControllerTest extends BaseWebMvcTest {

    @Test
    void getAll_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void getAll_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = READ_ACCESS)
    void getAll_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders");

        assert200(request);
    }

    @Test
    void get_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void get_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = READ_ACCESS)
    void get_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1");

        assert200(request);
    }

    @Test
    void getLines_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1/lines");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void getLines_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1/lines");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = READ_ACCESS)
    void getLines_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1/lines");

        assert200(request);
    }

    @Test
    void getLine_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1/lines/1");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void getLine_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1/lines/1");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = READ_ACCESS)
    void getLine_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1/lines/1");

        assert200(request);
    }

    @Test
    void getFulfillments_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1/fulfillments");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void getFulfillments_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1/fulfillments");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = READ_ACCESS)
    void getFulfillments_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1/fulfillments");

        assert200(request);
    }

    @Test
    void getFulfillment_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1/fulfillments/1");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void getFulfillment_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1/fulfillments/1");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = READ_ACCESS)
    void getFulfillment_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1/fulfillments/1");

        assert200(request);
    }

    @Test
    void getFulfillmentLines_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1/fulfillments/1/lines");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void getFulfillmentLines_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1/fulfillments/1/lines");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = READ_ACCESS)
    void getFulfillmentLines_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1/fulfillments/1/lines");

        assert200(request);
    }

    @Test
    void getFulfillmentLine_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1/fulfillments/1/lines/1");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void getFulfillmentLine_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1/fulfillments/1/lines/1");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = READ_ACCESS)
    void getFulfillmentLine_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/orders/1/fulfillments/1/lines/1");

        assert200(request);
    }

    @Test
    void confirm_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/orders/1/confirm")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void confirm_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/orders/1/confirm")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void confirm_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/orders/1/confirm")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(orderService.confirm(any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void confirm_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/orders/1/confirm")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(orderService.confirm(any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void confirm_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/orders/1/confirm")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void createFulfillment_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void createFulfillment_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void createFulfillment_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createFulfillment_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void createFulfillment_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        when(orderService.createFulfillment(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void createFulfillment_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        when(orderService.createFulfillment(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void createFulfillment_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        when(orderService.createFulfillment(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void createFulfillment_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void addFulfillmentLines_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void addFulfillmentLines_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void addFulfillmentLines_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void addFulfillmentLines_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void addFulfillmentLines_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        when(orderService.addFulfillmentLines(any(), any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void addFulfillmentLines_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        when(orderService.addFulfillmentLines(any(), any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void addFulfillmentLines_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        when(orderService.addFulfillmentLines(any(), any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void addFulfillmentLines_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void updateFulfillmentLineQuantities_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void updateFulfillmentLineQuantities_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void updateFulfillmentLineQuantities_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = patch("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateFulfillmentLineQuantities_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = patch("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void updateFulfillmentLineQuantities_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        when(orderService.updateFulfillmentLineQuantities(any(), any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void updateFulfillmentLineQuantities_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = patch("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        when(orderService.updateFulfillmentLineQuantities(any(), any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void updateFulfillmentLineQuantities_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = patch("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        when(orderService.updateFulfillmentLineQuantities(any(), any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void updateFulfillmentLineQuantities_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void deleteFulfillmentLines_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = delete("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void deleteFulfillmentLines_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = delete("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void deleteFulfillmentLines_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = delete("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void deleteFulfillmentLines_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = delete("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void deleteFulfillmentLines_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = delete("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        when(orderService.deleteFulfillmentLines(any(), any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void deleteFulfillmentLines_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = delete("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        when(orderService.deleteFulfillmentLines(any(), any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void deleteFulfillmentLines_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = delete("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        when(orderService.deleteFulfillmentLines(any(), any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void deleteFulfillmentLines_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = delete("/api/orders/1/fulfillments/1/lines")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void completeFulfillment_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments/1/complete")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void completeFulfillment_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments/1/complete")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void completeFulfillment_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments/1/complete")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void completeFulfillment_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments/1/complete")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void completeFulfillment_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments/1/complete")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        when(orderService.completeFulfillment(any(), any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void completeFulfillment_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments/1/complete")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(orderService.completeFulfillment(any(), any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void completeFulfillment_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments/1/complete")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(orderService.completeFulfillment(any(), any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void completeFulfillment_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfillments/1/complete")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void updateTrackingNumber_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/orders/1/fulfillments/1/tracking-number")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void updateTrackingNumber_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/orders/1/fulfillments/1/tracking-number")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void updateTrackingNumber_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = patch("/api/orders/1/fulfillments/1/tracking-number")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateTrackingNumber_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = patch("/api/orders/1/fulfillments/1/tracking-number")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void updateTrackingNumber_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/orders/1/fulfillments/1/tracking-number")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[]");

        when(orderService.updateTrackingNumber(any(), any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void updateTrackingNumber_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = patch("/api/orders/1/fulfillments/1/tracking-number")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(orderService.updateTrackingNumber(any(), any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void updateTrackingNumber_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = patch("/api/orders/1/fulfillments/1/tracking-number")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(orderService.updateTrackingNumber(any(), any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void updateTrackingNumber_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/orders/1/fulfillments/1/tracking-number")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    void deleteFulfillment_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = delete("/api/orders/1/fulfillments/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void deleteFulfillment_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = delete("/api/orders/1/fulfillments/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void deleteFulfillment_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = delete("/api/orders/1/fulfillments/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(orderService.deleteFulfillment(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void deleteFulfillment_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = delete("/api/orders/1/fulfillments/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(orderService.deleteFulfillment(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void deleteFulfillment_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = delete("/api/orders/1/fulfillments/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert200(request);
    }

    @Test
    void fulfill_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfill")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void fulfill_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfill")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void fulfill_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfill")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(orderService.fulfill(any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void fulfill_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfill")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(orderService.fulfill(any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void fulfill_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/orders/1/fulfill")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert200(request);
    }

    @Test
    void undoFulfill_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/orders/1/undo-fulfill")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void undoFulfill_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/orders/1/undo-fulfill")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void undoFulfill_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/orders/1/undo-fulfill")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(orderService.undoFulfill(any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void undoFulfill_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/orders/1/undo-fulfill")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(orderService.undoFulfill(any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void undoFulfill_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/orders/1/undo-fulfill")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert200(request);
    }

    @Test
    void cancel_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/orders/1/cancel")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void cancel_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/orders/1/cancel")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void cancel_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/orders/1/cancel")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(orderService.cancel(any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void cancel_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/orders/1/cancel")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(orderService.cancel(any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void cancel_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/orders/1/cancel")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert200(request);
    }

    @Test
    void reinstate_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/orders/1/reinstate")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void reinstate_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/orders/1/reinstate")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void reinstate_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/orders/1/reinstate")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(orderService.reinstate(any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void reinstate_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/orders/1/reinstate")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(orderService.reinstate(any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = PROCESS_ORDERS)
    void reinstate_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/orders/1/reinstate")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert200(request);
    }

}
