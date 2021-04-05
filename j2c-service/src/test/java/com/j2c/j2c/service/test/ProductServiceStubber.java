package com.j2c.j2c.service.test;

import com.j2c.j2c.domain.entity.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

import java.util.*;

import static com.j2c.j2c.service.test.TestUtils.nullOrEmpty;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@TestComponent
@RequiredArgsConstructor
public class ProductServiceStubber {

    private final MockBeanProvider mockBeanProvider;

    @Builder(builderClassName = "MockCreate",
            builderMethodName = "create",
            buildMethodName = "stub")
    private void _create(
            final UploadedImage imageForNewProduct,
            final ProductCategory categoryForNewProduct,
            final List<ProductTag> tagsForNewProduct
    ) {
        mockFindUploadedImageById(imageForNewProduct);
        mockFindCategoryById(categoryForNewProduct);
        mockFindTagsByIds(tagsForNewProduct);
    }

    @Builder(builderClassName = "MockUpdate",
            builderMethodName = "update",
            buildMethodName = "stub")
    private void _update(
            final Product productToUpdate,
            final UploadedImage newImageForProduct,
            final ProductVariant newDefaultVariant,
            final ProductCategory newCategoryForProduct,
            final List<ProductTag> tagsToAdd,
            final List<ProductTag> tagsToRemove
    ) {
        mockFindProductById(productToUpdate);
        mockFindUploadedImageById(newImageForProduct);
        mockFindVariantById(newDefaultVariant);
        mockFindCategoryById(newCategoryForProduct);
        mockFindTagsByIds(tagsToAdd, tagsToRemove);
    }

    @Builder(builderClassName = "MockPublish",
            builderMethodName = "publish",
            buildMethodName = "stub")
    private void _publish(final Product productToPublish) {
        mockFindProductById(productToPublish);
    }

    @Builder(builderClassName = "MockUnpublish",
            builderMethodName = "unpublish",
            buildMethodName = "stub")
    private void _unpublish(final Product productToUnpublish) {
        mockFindProductById(productToUnpublish);
    }

    @Builder(builderClassName = "MockDelete",
            builderMethodName = "delete",
            buildMethodName = "stub")
    private void _delete(final Product productToDelete) {
        mockFindProductById(productToDelete);
        mockFindFilenamesByProductId();
    }

    @Builder(builderClassName = "MockCreateVariant",
            builderMethodName = "createVariant",
            buildMethodName = "stub")
    private void _createVariant(
            final Product parentProduct,
            final List<UploadedImage> imagesForNewVariant
    ) {
        mockFindProductById(parentProduct);
        mockFindUploadedImagesByIds(imagesForNewVariant);
    }

    @Builder(builderClassName = "MockUpdateVariant",
            builderMethodName = "updateVariant",
            buildMethodName = "stub")
    private void _updateVariant(
            final Product parentProduct,
            final ProductVariant variantToUpdate,
            final List<UploadedImage> imagesToAdd,
            final List<ProductVariantImage> imagesToRemove
    ) {
        mockFindProductById(parentProduct);
        mockFindVariantById(variantToUpdate);
        mockFindUploadedImagesByIds(imagesToAdd);
        mockFindVariantImagesByIds(imagesToRemove);
    }

    @Builder(builderClassName = "MockDeleteVariant",
            builderMethodName = "deleteVariant",
            buildMethodName = "stub")
    private void _deleteVariant(
            final Product parentProduct,
            final ProductVariant variantToDelete
    ) {
        mockFindProductById(parentProduct);
        mockFindVariantById(variantToDelete);
        mockFindFilenamesByVariantId();
    }

    @Builder(builderClassName = "MockCreateCategory",
            builderMethodName = "createCategory",
            buildMethodName = "stub")
    private void _createCategory(final UploadedImage imageForNewCategory) {
        mockFindUploadedImageById(imageForNewCategory);
    }

    @Builder(builderClassName = "MockUpdateCategory",
            builderMethodName = "updateCategory",
            buildMethodName = "stub")
    private void _updateCategory(
            final ProductCategory categoryToUpdate,
            final UploadedImage newImageForCategory
    ) {
        mockFindCategoryById(categoryToUpdate);
        mockFindUploadedImageById(newImageForCategory);
    }

    @Builder(builderClassName = "MockCreateSubCategory",
            builderMethodName = "createSubCategory",
            buildMethodName = "stub")
    private void _createSubCategory(
            final ProductCategory parentCategory,
            final UploadedImage imageForNewCategory
    ) {
        mockFindCategoryById(parentCategory);
        mockFindUploadedImageById(imageForNewCategory);
    }

    @Builder(builderClassName = "MockdeleteCategory",
            builderMethodName = "deleteCategory",
            buildMethodName = "stub")
    private void _deleteCategory(final ProductCategory categoryToDelete) {
        mockFindCategoryById(categoryToDelete);
    }

    @Builder(builderClassName = "MockUpdateTag",
            builderMethodName = "updateTag",
            buildMethodName = "stub")
    private void _updateTag(final ProductTag tagToUpdate) {
        mockFindTagById(tagToUpdate);
    }

    @Builder(builderClassName = "MockDeleteTag",
            builderMethodName = "deleteTag",
            buildMethodName = "stub")
    private void _deleteTag(final ProductTag tagToDelete) {
        mockFindTagById(tagToDelete);
    }

    private void mockFindUploadedImageById(final UploadedImage uploadedImage) {
        if (uploadedImage != null) {
            when(mockBeanProvider.getUploadedImageRepository().findById(uploadedImage.getId()))
                    .thenReturn(Optional.of(uploadedImage));
        }
    }

    private void mockFindVariantById(final ProductVariant variant) {
        if (variant != null) {
            when(mockBeanProvider.getProductVariantRepository().findById(variant.getId()))
                    .thenReturn(Optional.of(variant));
        }
    }

    private void mockFindCategoryById(final ProductCategory category) {
        if (category != null) {
            when(mockBeanProvider.getProductCategoryRepository().findById(category.getId()))
                    .thenReturn(Optional.of(category));
        }
    }

    private void mockFindTagById(final ProductTag tag) {
        if (tag != null) {
            when(mockBeanProvider.getProductTagRepository().findById(tag.getId()))
                    .thenReturn(Optional.of(tag));
        }
    }

    private void mockFindProductById(final Product product) {
        if (product != null) {
            when(mockBeanProvider.getProductRepository().findById(product.getId()))
                    .thenReturn(Optional.of(product));
        }
    }

    private void mockFindTagsByIds(final List<ProductTag> tags) {
        mockFindTagsByIds(tags, null);
    }

    private void mockFindTagsByIds(final List<ProductTag> tagsToAdd, final List<ProductTag> tagsToRemove) {
        if (!(nullOrEmpty(tagsToAdd) || nullOrEmpty(tagsToRemove))) {
            when(mockBeanProvider.getProductTagRepository().findAllById(anySet()))
                    .thenReturn(tagsToAdd)
                    .thenReturn(tagsToRemove);
        } else if (!nullOrEmpty(tagsToAdd)) {
            when(mockBeanProvider.getProductTagRepository().findAllById(anySet()))
                    .thenReturn(tagsToAdd);
        } else if (!nullOrEmpty(tagsToRemove)) {
            when(mockBeanProvider.getProductTagRepository().findAllById(anySet()))
                    .thenReturn(tagsToRemove);
        }
    }

    private void mockFindUploadedImagesByIds(final List<UploadedImage> uploadedImages) {
        if (uploadedImages != null) {
            when(mockBeanProvider.getUploadedImageRepository().findAllById(anySet()))
                    .thenReturn(uploadedImages);
        }
    }

    private void mockFindVariantImagesByIds(final List<ProductVariantImage> variantImages) {
        if (variantImages != null) {
            when(mockBeanProvider.getProductVariantImageRepository().findAllById(anySet()))
                    .thenReturn(variantImages);
        }
    }

    private void mockFindFilenamesByProductId() {
        when(mockBeanProvider.getProductVariantImageRepository().findFilenamesByProductId(anyLong()))
                .thenReturn(List.of(UUID.randomUUID().toString() + ".jpg"));
    }

    private void mockFindFilenamesByVariantId() {
        when(mockBeanProvider.getProductVariantImageRepository().findFilenamesByVariantId(anyLong()))
                .thenReturn(List.of(UUID.randomUUID().toString() + ".jpg"));
    }

}
