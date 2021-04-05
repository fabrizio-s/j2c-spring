package com.j2c.j2c.service.application;

import com.j2c.j2c.service.dto.*;
import com.j2c.j2c.service.input.*;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.NotNull;

public interface ProductService {

    ProductDTO find(@NotNull Long productId);

    Page<ProductDTO> findAll(@NotNull Predicate predicate, @NotNull Pageable pageable);

    Page<ProductVariantDTO> findVariants(
            @NotNull Long productId,
            @NotNull Pageable pageable
    );

    ProductVariantDTO findVariant(@NotNull Long productId, @NotNull Long variantId);

    Page<ProductVariantImageDTO> findVariantImages(
            @NotNull Long productId,
            @NotNull Long variantId,
            @NotNull Pageable pageable
    );

    ProductVariantImageDTO findVariantImage(
            @NotNull Long productId,
            @NotNull Long variantId,
            @NotNull Long variantImageId
    );

    Page<ProductTagDTO> findProductTags(@NotNull Long productId, @NotNull Pageable pageable);

    Page<ProductCategoryDTO> findAllCategories(@NotNull Pageable pageable);

    ProductCategoryDTO findCategory(@NotNull Long categoryId);

    Page<ProductCategoryDTO> findSubCategories(@NotNull Long categoryId, @NotNull Pageable pageable);

    Page<ProductTagDTO> findAllTags(@NotNull Pageable pageable);

    ProductTagDTO findTag(@NotNull Long tagId);

    ProductDTO create(CreateProductForm form);

    ProductDTO update(
            Long productId,
            UpdateProductForm form
    );

    ProductDTO publish(Long productId);

    ProductDTO unpublish(Long productId);

    void delete(Long productId);

    ProductVariantDTO createVariant(
            Long productId,
            CreateProductVariantForm form
    );

    ProductVariantDTO updateVariant(
            Long productId,
            Long variantId,
            UpdateProductVariantForm form
    );

    void deleteVariant(Long productId, Long variantId);

    ProductCategoryDTO createCategory(CreateProductCategoryForm form);

    ProductCategoryDTO updateCategory(Long categoryId, UpdateProductCategoryForm form);

    ProductCategoryDTO createSubCategory(
            Long parentCategoryId,
            CreateProductCategoryForm form
    );

    void deleteCategory(Long categoryId);

    ProductTagDTO createTag(CreateProductTagForm form);

    ProductTagDTO updateTag(Long tagId, UpdateProductTagForm form);

    void deleteTag(Long tagId);

}
