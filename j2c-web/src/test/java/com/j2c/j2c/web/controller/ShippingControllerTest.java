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

import static com.j2c.j2c.domain.enums.Authorities.WRITE_SHIPPING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class ShippingControllerTest extends BaseWebMvcTest {

    @Test
    void getAllZones_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/shipping-zones");

        assert200(request);
    }

    @Test
    void getZone_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/shipping-zones/1");

        assert200(request);
    }

    @Test
    void getZoneCountries_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/shipping-zones/1/countries");

        assert200(request);
    }

    @Test
    void getMethods_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/shipping-zones/1/methods");

        assert200(request);
    }

    @Test
    void getMethod_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/shipping-zones/1/methods/1");

        assert200(request);
    }

    @Test
    void getAllCountries_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/shipping-countries");

        assert200(request);
    }

    @Test
    void getCountry_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/shipping-countries/UK");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void createZone_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/shipping-zones")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void createZone_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/shipping-zones")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void createZone_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/shipping-zones")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createZone_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/shipping-zones")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void createZone_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/shipping-zones")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(shippingService.createZone(any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void createZone_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/shipping-zones")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(shippingService.createZone(any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void createZone_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/shipping-zones")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(shippingService.createZone(any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void createZone_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/shipping-zones")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void updateZone_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/shipping-zones/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void updateZone_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/shipping-zones/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void updateZone_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = patch("/api/shipping-zones/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateZone_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = patch("/api/shipping-zones/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void updateZone_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/shipping-zones/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(shippingService.updateZone(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void updateZone_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = patch("/api/shipping-zones/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(shippingService.updateZone(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void updateZone_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = patch("/api/shipping-zones/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(shippingService.updateZone(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void updateZone_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/shipping-zones/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    void deleteZone_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = delete("/api/shipping-zones/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void deleteZone_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = delete("/api/shipping-zones/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void deleteZone_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = delete("/api/shipping-zones/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ResourceNotFoundException())
                .when(shippingService).deleteZone(any());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void deleteZone_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = delete("/api/shipping-zones/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ServiceException())
                .when(shippingService).deleteZone(any());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void deleteZone_HappyPath_ShouldRespond204() throws Exception {
        final RequestBuilder request = delete("/api/shipping-zones/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert204(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void createMethod_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/shipping-zones/1/methods")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void createMethod_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/shipping-zones/1/methods")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void createMethod_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/shipping-zones/1/methods")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createMethod_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/shipping-zones/1/methods")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void createMethod_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/shipping-zones/1/methods")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(shippingService.createMethod(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void createMethod_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/shipping-zones/1/methods")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(shippingService.createMethod(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void createMethod_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/shipping-zones/1/methods")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(shippingService.createMethod(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void createMethod_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/shipping-zones/1/methods")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void updateMethod_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/shipping-zones/1/methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void updateMethod_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/shipping-zones/1/methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void updateMethod_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = patch("/api/shipping-zones/1/methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateMethod_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = patch("/api/shipping-zones/1/methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void updateMethod_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/shipping-zones/1/methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(shippingService.updateMethod(any(), any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void updateMethod_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = patch("/api/shipping-zones/1/methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(shippingService.updateMethod(any(), any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void updateMethod_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = patch("/api/shipping-zones/1/methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(shippingService.updateMethod(any(), any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void updateMethod_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/shipping-zones/1/methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    void deleteMethod_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = delete("/api/shipping-zones/1/methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void deleteMethod_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = delete("/api/shipping-zones/1/methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void deleteMethod_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = delete("/api/shipping-zones/1/methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ResourceNotFoundException())
                .when(shippingService).deleteMethod(any(), any());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void deleteMethod_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = delete("/api/shipping-zones/1/methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ServiceException())
                .when(shippingService).deleteMethod(any(), any());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_SHIPPING)
    void deleteMethod_HappyPath_ShouldRespond204() throws Exception {
        final RequestBuilder request = delete("/api/shipping-zones/1/methods/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert204(request);
    }

}
