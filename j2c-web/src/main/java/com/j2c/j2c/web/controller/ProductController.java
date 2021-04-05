package com.j2c.j2c.web.controller;

import com.j2c.j2c.domain.entity.Product;
import com.j2c.j2c.service.application.ProductService;
import com.j2c.j2c.service.dto.*;
import com.j2c.j2c.service.input.*;
import com.j2c.j2c.web.security.annotation.HasProductWriteAccess;
import com.querydsl.core.types.Predicate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.j2c.j2c.domain.enums.Authorities.WRITE_PRODUCTS;

@RestController
@RequiredArgsConstructor
@Tag(name = "Products", description = "Endpoints related to products")
public class ProductController {

    private final ProductService productService;

    @GetMapping(value = "/api/products",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves all products")
    public Page<ProductDTO> getAll(
            @QuerydslPredicate(root = Product.class) final Predicate predicate,
            @ParameterObject final Pageable pageable
    ) {
        return productService.findAll(predicate, pageable);
    }

    @GetMapping(value = "/api/products/{productId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves a single product by its id")
    public ProductDTO get(@PathVariable final Long productId) {
        return productService.find(productId);
    }

    @GetMapping(value = "/api/products/{productId}/variants",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves the variants of the product with the specified id")
    public Page<ProductVariantDTO> getVariants(
            @PathVariable final Long productId,
            @ParameterObject final Pageable pageable
    ) {
        return productService.findVariants(productId, pageable);
    }

    @GetMapping(value = "/api/products/{productId}/variants/{variantId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves a single variant by id belonging to the specified product")
    public ProductVariantDTO getVariant(
            @PathVariable final Long productId,
            @PathVariable final Long variantId
    ) {
        return productService.findVariant(productId, variantId);
    }

    @GetMapping(value = "/api/products/{productId}/variants/{variantId}/images",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves the images belonging to the specified product variant")
    public Page<ProductVariantImageDTO> getVariantImages(
            @PathVariable final Long productId,
            @PathVariable final Long variantId,
            @ParameterObject final Pageable pageable
    ) {
        return productService.findVariantImages(productId, variantId, pageable);
    }

    @GetMapping(value = "/api/products/{productId}/variants/{variantId}/images/{variantImageId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves a single image by id belonging to the specified product variant")
    public ProductVariantImageDTO getVariantImage(
            @PathVariable final Long productId,
            @PathVariable final Long variantId,
            @PathVariable final Long variantImageId
    ) {
        return productService.findVariantImage(productId, variantId, variantImageId);
    }

    @GetMapping(value = "/api/products/{productId}/tags",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves the specified product's tags")
    public Page<ProductTagDTO> getProductTags(
            @PathVariable final Long productId,
            @ParameterObject final Pageable pageable
    ) {
        return productService.findProductTags(productId, pageable);
    }

    @GetMapping(value = "/api/categories",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves all product categories")
    public Page<ProductCategoryDTO> getAllCategories(@ParameterObject final Pageable pageable) {
        return productService.findAllCategories(pageable);
    }

    @GetMapping(value = "/api/categories/{categoryId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves a single category by its id")
    public ProductCategoryDTO getCategory(@PathVariable final Long categoryId) {
        return productService.findCategory(categoryId);
    }

    @GetMapping(value = "/api/categories/{categoryId}/sub-categories",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves all the sub-categories belonging to the category with the specified id")
    public Page<ProductCategoryDTO> getSubCategories(
            @PathVariable final Long categoryId,
            @ParameterObject final Pageable pageable
    ) {
        return productService.findSubCategories(categoryId, pageable);
    }

    @GetMapping(value = "/api/tags",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves all tags")
    public Page<ProductTagDTO> getAllTags(@ParameterObject final Pageable pageable) {
        return productService.findAllTags(pageable);
    }

    @GetMapping(value = "/api/tags/{tagId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves a single tag by its id")
    public ProductTagDTO getTag(@PathVariable final Long tagId) {
        return productService.findTag(tagId);
    }

    @PostMapping(value = "/api/products",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasProductWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Creates a product and a default variant",
            description = "Returns the created product and variant. " +
                    "Requires " + WRITE_PRODUCTS + " authority (Admin).")
    public ProductDTO create(@RequestBody final CreateProductForm payload) {
        return productService.create(payload);
    }

    @PatchMapping(value = "/api/products/{productId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasProductWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Updates a product",
            description = "Returns the updated product. " +
                    "Requires " + WRITE_PRODUCTS + " authority (Admin).")
    public ProductDTO update(
            @PathVariable final Long productId,
            @RequestBody final UpdateProductForm payload
    ) {
        return productService.update(productId, payload);
    }

    @PostMapping(value = "/api/products/{productId}/publish",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasProductWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Publishes a product",
            description = "Returns the updated product. " +
                    "Requires " + WRITE_PRODUCTS + " authority (Admin).")
    public ProductDTO publish(@PathVariable final Long productId) {
        return productService.publish(productId);
    }

    @PostMapping(value = "/api/products/{productId}/unpublish",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasProductWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Unpublishes a product",
            description = "Returns the updated product. " +
                    "Requires " + WRITE_PRODUCTS + " authority (Admin).")
    public ProductDTO unpublish(@PathVariable final Long productId) {
        return productService.unpublish(productId);
    }

    @DeleteMapping("/api/products/{productId}")
    @HasProductWriteAccess
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Deletes a product",
            description = "Also deletes all of the product's variants. " +
                    "Requires " + WRITE_PRODUCTS + " authority (Admin).")
    public void delete(@PathVariable final Long productId) {
        productService.delete(productId);
    }

    @PostMapping(value = "/api/products/{productId}/variants",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasProductWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Creates a variant of the product with the specified id",
            description = "Returns the created variant. " +
                    "If the product's default variant has no name, a name for it must be provided. " +
                    "Requires " + WRITE_PRODUCTS + " authority (Admin).")
    public ProductVariantDTO createVariant(
            @PathVariable final Long productId,
            @RequestBody final CreateProductVariantForm payload
    ) {
        return productService.createVariant(productId, payload);
    }

    @PatchMapping(value = "/api/products/{productId}/variants/{variantId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasProductWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Updates the variant by id of the specified product",
            description = "Returns the updated variant. " +
                    "Requires " + WRITE_PRODUCTS + " authority (Admin).")
    public ProductVariantDTO updateVariant(
            @PathVariable final Long productId,
            @PathVariable final Long variantId,
            @RequestBody final UpdateProductVariantForm payload
    ) {
        return productService.updateVariant(productId, variantId, payload);
    }

    @DeleteMapping("/api/products/{productId}/variants/{variantId}")
    @HasProductWriteAccess
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Deletes the variant by id belonging to the specified product",
            description = "Requires " + WRITE_PRODUCTS + " authority (Admin).")
    public void deleteVariant(
            @PathVariable final Long productId,
            @PathVariable final Long variantId
    ) {
        productService.deleteVariant(productId, variantId);
    }

    @PostMapping(value = "/api/categories",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasProductWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Creates a category",
            description = "Returns the created category. " +
                    "Requires " + WRITE_PRODUCTS + " authority (Admin).")
    public ProductCategoryDTO createCategory(@RequestBody final CreateProductCategoryForm payload) {
        return productService.createCategory(payload);
    }

    @PatchMapping(value = "/api/categories/{categoryId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasProductWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Updates the category with the specified id",
            description = "Returns the updated category. " +
                    "Requires " + WRITE_PRODUCTS + " authority (Admin).")
    public ProductCategoryDTO updateCategory(
            @PathVariable final Long categoryId,
            @RequestBody final UpdateProductCategoryForm payload
    ) {
        return productService.updateCategory(categoryId, payload);
    }

    @PostMapping(value = "/api/categories/{categoryId}/sub-categories",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasProductWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Creates a sub-category of the specified category",
            description = "Returns the created sub-category. " +
                    "Requires " + WRITE_PRODUCTS + " authority (Admin).")
    public ProductCategoryDTO createSubCategory(
            @PathVariable final Long categoryId,
            @RequestBody final CreateProductCategoryForm payload
    ) {
        return productService.createSubCategory(categoryId, payload);
    }

    @DeleteMapping("/api/categories/{categoryId}")
    @HasProductWriteAccess
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Deletes the category with the specified id and all of its",
            description = "Also deletes all of the category's sub-categories." +
                    "Requires " + WRITE_PRODUCTS + " authority (Admin).")
    public void deleteCategory(@PathVariable final Long categoryId) {
        productService.deleteCategory(categoryId);
    }

    @PostMapping(value = "/api/tags",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasProductWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Creates a tag",
            description = "Returns the created tag." +
                    "Requires " + WRITE_PRODUCTS + " authority (Admin).")
    public ProductTagDTO createTag(@RequestBody final CreateProductTagForm payload) {
        return productService.createTag(payload);
    }

    @PatchMapping(value = "/api/tags/{tagId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasProductWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Updates the tag with the specified id",
            description = "Returns the updated tag." +
                    "Requires " + WRITE_PRODUCTS + " authority (Admin).")
    public ProductTagDTO updateTag(
            @PathVariable final Long tagId,
            @RequestBody final UpdateProductTagForm payload
    ) {
        return productService.updateTag(tagId, payload);
    }

    @DeleteMapping("/api/tags/{tagId}")
    @HasProductWriteAccess
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Deletes the tag with the specified id",
            description = "Requires " + WRITE_PRODUCTS + " authority (Admin).")
    public void deleteTag(@PathVariable final Long tagId) {
        productService.deleteTag(tagId);
    }

}
