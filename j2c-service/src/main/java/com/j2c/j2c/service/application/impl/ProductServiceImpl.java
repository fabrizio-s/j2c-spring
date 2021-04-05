package com.j2c.j2c.service.application.impl;

import com.j2c.j2c.domain.entity.*;
import com.j2c.j2c.domain.repository.*;
import com.j2c.j2c.service.application.ProductService;
import com.j2c.j2c.service.domain.product.*;
import com.j2c.j2c.service.dto.*;
import com.j2c.j2c.service.input.*;
import com.j2c.j2c.service.mapper.ProductServiceMapper;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Service
@Validated
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductServiceMapper mapper;
    private final DomainProductService domainService;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductVariantImageRepository variantImageRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductTagRepository tagRepository;

    @Override
    public ProductDTO find(@NotNull final Long productId) {
        final Product product = productRepository.findById(productId);
        return mapper.toProductDTO(product);
    }

    @Override
    public Page<ProductDTO> findAll(@NotNull final Predicate predicate, @NotNull final Pageable pageable) {
        final Page<Product> products = productRepository.findAll(predicate, pageable);
        return mapper.toProductDTO(products);
    }

    @Override
    public Page<ProductVariantDTO> findVariants(@NotNull final Long productId, @NotNull final Pageable pageable) {
        productRepository.verifyExistsById(productId);
        final Page<ProductVariant> variants = variantRepository.findAllByProductId(productId, pageable);
        return mapper.toVariantDTO(variants);
    }

    @Override
    public ProductVariantDTO findVariant(@NotNull final Long productId, @NotNull final Long variantId) {
        final Product product = productRepository.findById(productId);
        final ProductVariant variant = variantRepository.findById(variantId).verifyBelongsToProduct(product);
        return mapper.toVariantDTO(variant);
    }

    @Override
    public Page<ProductVariantImageDTO> findVariantImages(
            @NotNull final Long productId,
            @NotNull final Long variantId,
            @NotNull final Pageable pageable
    ) {
        final Product product = productRepository.findById(productId);
        variantRepository.findById(variantId).verifyBelongsToProduct(product);
        final Page<ProductVariantImage> variantImages = variantImageRepository.findAllByVariantId(variantId, pageable);
        return mapper.toVariantImageDTO(variantImages);
    }

    @Override
    public ProductVariantImageDTO findVariantImage(
            @NotNull final Long productId,
            @NotNull final Long variantId,
            @NotNull final Long variantImageId
    ) {
        final Product product = productRepository.findById(productId);
        final ProductVariant variant = variantRepository.findById(variantId).verifyBelongsToProduct(product);
        final ProductVariantImage variantImage = variantImageRepository.findById(variantImageId).verifyBelongsToVariant(variant);
        return mapper.toVariantImageDTO(variantImage);
    }

    @Override
    public Page<ProductTagDTO> findProductTags(@NotNull final Long productId, @NotNull final Pageable pageable) {
        productRepository.verifyExistsById(productId);
        final Page<ProductTag> tags = tagRepository.findAllByProductId(productId, pageable);
        return mapper.toTagDTO(tags);
    }

    @Override
    public Page<ProductCategoryDTO> findAllCategories(@NotNull final Pageable pageable) {
        final Page<ProductCategory> categories = categoryRepository.findAll(pageable);
        return mapper.toCategoryDTO(categories);
    }

    @Override
    public ProductCategoryDTO findCategory(@NotNull final Long categoryId) {
        final ProductCategory category = categoryRepository.findById(categoryId);
        return mapper.toCategoryDTO(category);
    }

    @Override
    public Page<ProductCategoryDTO> findSubCategories(@NotNull final Long categoryId, @NotNull final Pageable pageable) {
        final ProductCategory category = categoryRepository.findById(categoryId);
        final Page<ProductCategory> subCategories = categoryRepository.findSubCategories(category, pageable);
        return mapper.toCategoryDTO(subCategories);
    }

    @Override
    public Page<ProductTagDTO> findAllTags(@NotNull final Pageable pageable) {
        final Page<ProductTag> tags = tagRepository.findAll(pageable);
        return mapper.toTagDTO(tags);
    }

    @Override
    public ProductTagDTO findTag(@NotNull final Long tagId) {
        final ProductTag tag = tagRepository.findById(tagId);
        return mapper.toTagDTO(tag);
    }

    @Override
    public ProductDTO create(final CreateProductForm form) {
        final CreateProductResult result = domainService.create(form);
        return mapper.toProductDTO(result);
    }

    @Override
    public ProductDTO update(final Long productId, final UpdateProductForm form) {
        final UpdateProductResult result = domainService.update(productId, form);
        return mapper.toProductDTO(result);
    }

    @Override
    public ProductDTO publish(final Long productId) {
        final Product publishedProduct = domainService.publish(productId);
        return mapper.toProductDTO(publishedProduct);
    }

    @Override
    public ProductDTO unpublish(final Long productId) {
        final Product unpublishedProduct = domainService.unpublish(productId);
        return mapper.toProductDTO(unpublishedProduct);
    }

    @Override
    public void delete(final Long productId) {
        domainService.delete(productId);
    }

    @Override
    public ProductVariantDTO createVariant(final Long productId, final CreateProductVariantForm form) {
        final CreateProductVariantResult result = domainService.createVariant(productId, form);
        return mapper.toVariantDTO(result);
    }

    @Override
    public ProductVariantDTO updateVariant(final Long productId, final Long variantId, final UpdateProductVariantForm form) {
        final UpdateProductVariantResult result = domainService.updateVariant(productId, variantId, form);
        return mapper.toVariantDTO(result);
    }

    @Override
    public void deleteVariant(final Long productId, final Long variantId) {
        domainService.deleteVariant(productId, variantId);
    }

    @Override
    public ProductCategoryDTO createCategory(final CreateProductCategoryForm form) {
        final ProductCategory createdCategory = domainService.createCategory(form);
        return mapper.toCategoryDTO(createdCategory);
    }

    @Override
    public ProductCategoryDTO updateCategory(final Long categoryId, final UpdateProductCategoryForm form) {
        final ProductCategory updatedCategory = domainService.updateCategory(categoryId, form);
        return mapper.toCategoryDTO(updatedCategory);
    }

    @Override
    public ProductCategoryDTO createSubCategory(final Long parentCategoryId, final CreateProductCategoryForm form) {
        final ProductCategory createdSubCategory = domainService.createSubCategory(parentCategoryId, form);
        return mapper.toCategoryDTO(createdSubCategory);
    }

    @Override
    public void deleteCategory(final Long categoryId) {
        domainService.deleteCategory(categoryId);
    }

    @Override
    public ProductTagDTO createTag(final CreateProductTagForm form) {
        final ProductTag createdTag = domainService.createTag(form);
        return mapper.toTagDTO(createdTag);
    }

    @Override
    public ProductTagDTO updateTag(final Long tagId, final UpdateProductTagForm form) {
        final ProductTag updatedTag = domainService.updateTag(tagId, form);
        return mapper.toTagDTO(updatedTag);
    }

    @Override
    public void deleteTag(final Long tagId) {
        domainService.deleteTag(tagId);
    }

}
