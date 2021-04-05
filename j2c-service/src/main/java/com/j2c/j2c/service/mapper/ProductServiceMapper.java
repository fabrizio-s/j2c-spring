package com.j2c.j2c.service.mapper;

import com.google.common.collect.ImmutableSet;
import com.j2c.j2c.domain.entity.*;
import com.j2c.j2c.service.domain.product.CreateProductResult;
import com.j2c.j2c.service.domain.product.CreateProductVariantResult;
import com.j2c.j2c.service.domain.product.UpdateProductResult;
import com.j2c.j2c.service.domain.product.UpdateProductVariantResult;
import com.j2c.j2c.service.dto.*;
import com.j2c.j2c.service.dto.ProductVariantDTO.ProductVariantDTOBuilder;
import org.springframework.data.domain.Page;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ProductServiceMapper {

    private final ProductDTOMapper productDTOMapper;
    private final ProductVariantDTOMapper variantDTOMapper;
    private final ProductVariantImageDTOMapper variantImageDTOMapper;
    private final ProductCategoryDTOMapper categoryDTOMapper;
    private final ProductTagDTOMapper tagDTOMapper;

    public ProductDTO toProductDTO(@NonNull final Product product) {
        return productDTOMapper.fromEntity(product).build();
    }

    public Page<ProductDTO> toProductDTO(final Page<Product> products) {
        return productDTOMapper.fromEntities(products);
    }

    public ProductDTO toProductDTO(@NonNull final CreateProductResult result) {
        final ProductDTO.ProductDTOBuilder builder = productDTOMapper.fromEntity(result.getCreatedProduct());
        final ProductVariantDTO defaultVariant = toVariantDTO(result.getCreatedVariant());
        builder.defaultVariant(defaultVariant);
        builder.tags(getTagIds(result.getAddedTags()));
        return builder.build();
    }

    public ProductDTO toProductDTO(@NonNull final UpdateProductResult result) {
        final ProductDTO.ProductDTOBuilder builder = productDTOMapper.fromEntity(result.getUpdatedProduct());
        builder.addedTags(getTagIds(result.getAddedTags()));
        builder.removedTags(getTagIds(result.getRemovedTags()));
        return builder.build();
    }

    public ProductVariantDTO toVariantDTO(@NonNull final ProductVariant variant) {
        return variantDTOMapper.fromEntity(variant).build();
    }

    public Page<ProductVariantDTO> toVariantDTO(final Page<ProductVariant> variants) {
        return variantDTOMapper.fromEntities(variants);
    }

    public ProductVariantDTO toVariantDTO(@NonNull final CreateProductVariantResult result) {
        final ProductVariantDTOBuilder builder = variantDTOMapper.fromEntity(result.getCreatedVariant());
        final List<ProductVariantImageDTO> imagesDTO = variantImageDTOMapper.fromEntities(result.getCreatedImages());
        builder.images(imagesDTO);
        return builder.build();
    }

    public ProductVariantDTO toVariantDTO(@NonNull final UpdateProductVariantResult result) {
        final ProductVariantDTOBuilder builder = variantDTOMapper.fromEntity(result.getUpdatedVariant());
        final List<ProductVariantImageDTO> imagesDTO = variantImageDTOMapper.fromEntities(result.getAddedImages());
        builder.addedImages(imagesDTO);
        return builder.build();
    }

    public ProductVariantImageDTO toVariantImageDTO(@NonNull final ProductVariantImage variantImage) {
        return variantImageDTOMapper.fromEntity(variantImage).build();
    }

    public Page<ProductVariantImageDTO> toVariantImageDTO(final Page<ProductVariantImage> variantImages) {
        return variantImageDTOMapper.fromEntities(variantImages);
    }

    public ProductCategoryDTO toCategoryDTO(@NonNull final ProductCategory category) {
        return categoryDTOMapper.fromEntity(category).build();
    }

    public Page<ProductCategoryDTO> toCategoryDTO(final Page<ProductCategory> categories) {
        return categoryDTOMapper.fromEntities(categories);
    }

    public ProductTagDTO toTagDTO(@NonNull final ProductTag tag) {
        return tagDTOMapper.fromEntity(tag).build();
    }

    public Page<ProductTagDTO> toTagDTO(final Page<ProductTag> tags) {
        return tagDTOMapper.fromEntities(tags);
    }

    private static Set<Long> getTagIds(final List<ProductTag> tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptySet();
        }
        return tags.stream()
                .map(ProductTag::getId)
                .collect(ImmutableSet.toImmutableSet());
    }

}
