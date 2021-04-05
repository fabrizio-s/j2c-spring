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

import static com.j2c.j2c.domain.enums.Authorities.WRITE_PRODUCTS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class ProductControllerTest extends BaseWebMvcTest {

    @Test
    void getAll_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/products");

        assert200(request);
    }

    @Test
    void get_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/products/1");

        assert200(request);
    }

    @Test
    void getVariants_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/products/1/variants");

        assert200(request);
    }

    @Test
    void getVariant_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/products/1/variants/1");

        assert200(request);
    }

    @Test
    void getVariantImages_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/products/1/variants/1/images");

        assert200(request);
    }

    @Test
    void getVariantImage_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/products/1/variants/1/images/1");

        assert200(request);
    }

    @Test
    void getProductTags_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/products/1/tags");

        assert200(request);
    }

    @Test
    void getAllCategories_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/categories");

        assert200(request);
    }

    @Test
    void getCategory_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/categories/1");

        assert200(request);
    }

    @Test
    void getSubCategories_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/categories/1/sub-categories");

        assert200(request);
    }

    @Test
    void getAllTags_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/tags");

        assert200(request);
    }

    @Test
    void getTag_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = MockMvcRequestBuilders.get("/api/tags/1");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void create_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/products")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void create_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/products")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void create_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/products")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void create_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/products")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void create_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/products")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.create(any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void create_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/products")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.create(any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void create_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/products")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.create(any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void create_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/products")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void update_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void update_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void update_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = patch("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void update_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = patch("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void update_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.update(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void update_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = patch("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.update(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void update_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = patch("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.update(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void update_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    void publish_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/products/1/publish")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void publish_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/products/1/publish")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void publish_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/products/1/publish")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(productService.publish(any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void publish_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/products/1/publish")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(productService.publish(any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void publish_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/products/1/publish")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert200(request);
    }

    @Test
    void unpublish_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/products/1/unpublish")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void unpublish_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/products/1/unpublish")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void unpublish_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/products/1/unpublish")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(productService.unpublish(any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void unpublish_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/products/1/unpublish")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        when(productService.unpublish(any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void unpublish_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/products/1/unpublish")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert200(request);
    }

    @Test
    void delete_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = delete("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void delete_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = delete("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void delete_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = delete("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ResourceNotFoundException())
                .when(productService).delete(any());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void delete_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = delete("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ServiceException())
                .when(productService).delete(any());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void delete_HappyPath_ShouldRespond204() throws Exception {
        final RequestBuilder request = delete("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert204(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createVariant_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/products/1/variants")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createVariant_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/products/1/variants")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void createVariant_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/products/1/variants")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createVariant_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/products/1/variants")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createVariant_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/products/1/variants")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.createVariant(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createVariant_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/products/1/variants")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.createVariant(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createVariant_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/products/1/variants")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.createVariant(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createVariant_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/products/1/variants")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void updateVariant_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/products/1/variants/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void updateVariant_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/products/1/variants/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void updateVariant_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = patch("/api/products/1/variants/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateVariant_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = patch("/api/products/1/variants/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void updateVariant_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/products/1/variants/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.updateVariant(any(), any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void updateVariant_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = patch("/api/products/1/variants/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.updateVariant(any(), any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void updateVariant_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = patch("/api/products/1/variants/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.updateVariant(any(), any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void updateVariant_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/products/1/variants/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    void deleteVariant_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = delete("/api/products/1/variants/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void deleteVariant_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = delete("/api/products/1/variants/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void deleteVariant_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = delete("/api/products/1/variants/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ResourceNotFoundException())
                .when(productService).deleteVariant(any(), any());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void deleteVariant_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = delete("/api/products/1/variants/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ServiceException())
                .when(productService).deleteVariant(any(), any());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void deleteVariant_HappyPath_ShouldRespond204() throws Exception {
        final RequestBuilder request = delete("/api/products/1/variants/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert204(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createCategory_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createCategory_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void createCategory_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createCategory_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createCategory_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.createCategory(any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createCategory_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.createCategory(any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createCategory_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.createCategory(any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createCategory_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void updateCategory_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void updateCategory_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void updateCategory_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = patch("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateCategory_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = patch("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void updateCategory_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.updateCategory(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void updateCategory_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = patch("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.updateCategory(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void updateCategory_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = patch("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.updateCategory(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void updateCategory_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createSubCategory_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/categories/1/sub-categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createSubCategory_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/categories/1/sub-categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void createSubCategory_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/categories/1/sub-categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createSubCategory_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/categories/1/sub-categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createSubCategory_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/categories/1/sub-categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.createSubCategory(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createSubCategory_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/categories/1/sub-categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.createSubCategory(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createSubCategory_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/categories/1/sub-categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.createSubCategory(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createSubCategory_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/categories/1/sub-categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    void deleteCategory_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = delete("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void deleteCategory_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = delete("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void deleteCategory_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = delete("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ResourceNotFoundException())
                .when(productService).deleteCategory(any());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void deleteCategory_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = delete("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ServiceException())
                .when(productService).deleteCategory(any());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void deleteCategory_HappyPath_ShouldRespond204() throws Exception {
        final RequestBuilder request = delete("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert204(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createTag_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createTag_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void createTag_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void createTag_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createTag_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.createTag(any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createTag_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.createTag(any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createTag_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.createTag(any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void createTag_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void updateTag_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/tags/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void updateTag_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/tags/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(BAD_REQUEST_BODY);

        assert400(request);
    }

    @Test
    void updateTag_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = patch("/api/tags/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void updateTag_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = patch("/api/tags/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void updateTag_InvalidInputException_ShouldRespond400() throws Exception {
        final RequestBuilder request = patch("/api/tags/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.updateTag(any(), any()))
                .thenThrow(new InvalidInputException(Collections.emptySet(), Collections.emptySet(), null));

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void updateTag_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = patch("/api/tags/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.updateTag(any(), any()))
                .thenThrow(new ResourceNotFoundException());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void updateTag_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = patch("/api/tags/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        when(productService.updateTag(any(), any()))
                .thenThrow(new ServiceException());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void updateTag_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = patch("/api/tags/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{}");

        assert200(request);
    }

    @Test
    void deleteTag_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = delete("/api/tags/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void deleteTag_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = delete("/api/tags/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void deleteTag_ResourceNotFoundException_ShouldRespond404() throws Exception {
        final RequestBuilder request = delete("/api/tags/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ResourceNotFoundException())
                .when(productService).deleteTag(any());

        assert404(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void deleteTag_ServiceException_ShouldRespond422() throws Exception {
        final RequestBuilder request = delete("/api/tags/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        doThrow(new ServiceException())
                .when(productService).deleteTag(any());

        assert422(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_PRODUCTS)
    void deleteTag_HappyPath_ShouldRespond204() throws Exception {
        final RequestBuilder request = delete("/api/tags/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        assert204(request);
    }

}
