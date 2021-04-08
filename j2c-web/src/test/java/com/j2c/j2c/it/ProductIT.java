package com.j2c.j2c.it;

import com.fasterxml.jackson.databind.JsonNode;
import com.j2c.j2c.it.util.BaseIT;
import com.j2c.j2c.service.dto.*;
import com.j2c.j2c.service.input.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductIT extends BaseIT {

    @Test
    void getAll() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/products",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), ProductDTO.class);
    }

    @Test
    void get() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/products/1",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ProductDTO.class);
    }

    @Test
    void getVariants() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/products/1/variants",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), ProductVariantDTO.class);
    }

    @Test
    void getVariant() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/products/1/variants/1",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ProductVariantDTO.class);
    }

    @Test
    void getVariantImages() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/products/1/variants/1/images",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), ProductVariantImageDTO.class);
    }

    @Test
    void getVariantImage() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/products/1/variants/1/images/1",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ProductVariantImageDTO.class);
    }

    @Test
    void getProductTags() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/products/1/tags",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), ProductTagDTO.class);
    }

    @Test
    void getAllCategories() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/categories",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), ProductCategoryDTO.class);
    }

    @Test
    void getCategory() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/categories/1",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ProductCategoryDTO.class);
    }

    @Test
    void getSubCategories() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/categories/1/sub-categories",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), ProductCategoryDTO.class);
    }

    @Test
    void getAllTags() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/tags",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertPageOfTypeIsNotEmpty(response.getBody(), ProductTagDTO.class);
    }

    @Test
    void getTag() {
        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/tags/1",
                HttpMethod.GET,
                null,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ProductTagDTO.class);
    }

    @Test
    void create() {
        final CreateProductForm body = CreateProductForm.builder()
                .name("Test Product")
                .digital(false)
                .mass(100)
                .price(100L)
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/products",
                HttpMethod.POST,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ProductDTO.class);
    }

    @Test
    void update() {
        final ProductDTO product = testDataCreator.createProduct();

        final UpdateProductForm body = UpdateProductForm.builder()
                .name("New Test Product Name")
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/products/" + product.getId(),
                HttpMethod.PATCH,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ProductDTO.class);
    }

    @Test
    void publish() {
        final ProductDTO product = testDataCreator.createProduct();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/products/" + product.getId() + "/publish",
                HttpMethod.POST,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ProductDTO.class);
    }

    @Test
    void unpublish() {
        final ProductDTO product = testDataCreator.createProduct();
        productService.publish(product.getId());

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/products/" + product.getId() + "/unpublish",
                HttpMethod.POST,
                null,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ProductDTO.class);
    }

    @Test
    void delete() {
        final ProductDTO product = testDataCreator.createProduct();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/products/" + product.getId(),
                HttpMethod.DELETE,
                null,
                adminToken
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createVariant() {
        final ProductDTO product = testDataCreator.createProduct();

        final CreateProductVariantForm body = CreateProductVariantForm.builder()
                .defaultVariantName("New Default Variant Name")
                .name("Test Variant")
                .mass(100)
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/products/" + product.getId() + "/variants",
                HttpMethod.POST,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ProductVariantDTO.class);
    }

    @Test
    void updateVariant() {
        final ProductDTO product = testDataCreator.createProduct();

        final UpdateProductVariantForm body = UpdateProductVariantForm.builder()
                .name("New Test Variant Name")
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/products/" + product.getId() + "/variants/" + product.getDefaultVariant().getId(),
                HttpMethod.PATCH,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ProductVariantDTO.class);
    }

    @Test
    void deleteVariant() {
        final ProductDTO product = testDataCreator.createProduct();
        final ProductVariantDTO variant = testDataCreator.createVariantForProduct(product);

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/products/" + product.getId() + "/variants/" + variant.getId(),
                HttpMethod.DELETE,
                null,
                adminToken
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createCategory() {
        final CreateProductCategoryForm body = CreateProductCategoryForm.builder()
                .name("Test Category")
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/categories",
                HttpMethod.POST,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ProductCategoryDTO.class);
    }

    @Test
    void updateCategory() {
        final ProductCategoryDTO category = testDataCreator.createCategory();

        final UpdateProductCategoryForm body = UpdateProductCategoryForm.builder()
                .name("New Test Category Name")
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/categories/" + category.getId(),
                HttpMethod.PATCH,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ProductCategoryDTO.class);
    }

    @Test
    void createSubCategory() {
        final ProductCategoryDTO category = testDataCreator.createCategory();

        final CreateProductCategoryForm body = CreateProductCategoryForm.builder()
                .name("Test Sub Category")
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/categories/" + category.getId() + "/sub-categories",
                HttpMethod.POST,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ProductCategoryDTO.class);
    }

    @Test
    void deleteCategory() {
        final ProductCategoryDTO category = testDataCreator.createCategory();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/categories/" + category.getId(),
                HttpMethod.DELETE,
                null,
                adminToken
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createTag() {
        final CreateProductTagForm body = CreateProductTagForm.builder()
                .name("Test Tag")
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/tags",
                HttpMethod.POST,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ProductTagDTO.class);
    }

    @Test
    void updateTag() {
        final ProductTagDTO tag = testDataCreator.createTag();

        final UpdateProductTagForm body = UpdateProductTagForm.builder()
                .name("New Test Tag")
                .build();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/tags/" + tag.getId(),
                HttpMethod.PATCH,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), ProductTagDTO.class);
    }

    @Test
    void deleteTag() {
        final ProductTagDTO tag = testDataCreator.createTag();

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/tags/" + tag.getId(),
                HttpMethod.DELETE,
                null,
                adminToken
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

}
