package com.j2c.j2c.service.domain.product;

import com.j2c.j2c.domain.entity.*;
import com.j2c.j2c.domain.repository.*;
import com.j2c.j2c.service.input.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static com.j2c.j2c.domain.util.J2cUtils.optional;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class DomainProductService {

    private final ApplicationEventPublisher eventPublisher;
    private final UploadedImageRepository uploadedImageRepository;
    private final ProductTagRepository tagRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductToTagAssociationRepository productToTagAssociationRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductVariantImageRepository variantImageRepository;

    public CreateProductResult create(@NotNull @Valid final CreateProductForm form) {
        final Product product = productRepository.save(
                Product.builder()
                        .name(form.getName())
                        .description(form.getDescription())
                        .digital(form.getDigital())
                        .defaultPrice(form.getPrice())
                        .build()
        );

        product.setDefaultVariant(
                variantRepository.save(
                        product.newVariant()
                                .mass(form.getMass())
                                .add()
                )
        );

        setCategory(product, form.getCategoryId());

        setImage(product, form.getImageId());

        final List<ProductTag> addedTags = addTags(product, form.getTagIds());

        return CreateProductResult.builder()
                .createdProduct(product)
                .createdVariant(product.getDefaultVariant())
                .addedTags(addedTags)
                .build();
    }

    public UpdateProductResult update(
            @NotNull final Long productId,
            @NotNull @Valid final UpdateProductForm form
    ) {
        final Product product = productRepository.findById(productId);

        optional(form.getName()).ifPresent(product::setName);
        optional(form.getDescription()).ifPresent(product::setDescription);
        optional(form.getPrice()).ifPresent(product::setDefaultPrice);

        setImage(product, form.getNewImageId());

        setDefaultVariant(product, form.getDefaultVariantId());

        setCategory(product, form.getCategoryId());

        final List<ProductTag> addedTags = addTags(product, form.getTagsToAdd());

        final List<ProductTag> removedTags = removeTags(product, form.getTagsToRemove());

        return UpdateProductResult.builder()
                .updatedProduct(product)
                .addedTags(addedTags)
                .removedTags(removedTags)
                .build();
    }

    public Product publish(@NotNull final Long productId) {
        final Product product = productRepository.findById(productId);

        product.publish();

        return productRepository.save(product);
    }

    public Product unpublish(@NotNull final Long productId) {
        final Product product = productRepository.findById(productId);

        product.unpublish();

        return productRepository.save(product);
    }

    public void delete(@NotNull final Long productId) {
        final Product product = productRepository.findById(productId);

        removeProductImages(product, getAllProductImageFilenames(product));

        productRepository.remove(product);
    }

    public CreateProductVariantResult createVariant(
            @NotNull final Long productId,
            @NotNull @Valid final CreateProductVariantForm form
    ) {
        final Product product = productRepository.findById(productId);

        final ProductVariant variant = variantRepository.save(
                product.newVariant()
                        .defaultVariantName(form.getDefaultVariantName())
                        .name(form.getName())
                        .mass(form.getMass())
                        .price(form.getPrice())
                        .add()
        );

        final List<ProductVariantImage> images = addImages(variant, form.getImageIds());

        return CreateProductVariantResult.builder()
                .createdVariant(variant)
                .createdImages(images)
                .build();
    }

    public UpdateProductVariantResult updateVariant(
            @NotNull final Long productId,
            @NotNull final Long variantId,
            @NotNull @Valid final UpdateProductVariantForm form
    ) {
        final Product product = productRepository.findById(productId);

        final ProductVariant variant = variantRepository.findById(variantId)
                .verifyBelongsToProduct(product);

        optional(form.getName()).ifPresent(variant::setName);
        optional(form.getMass()).ifPresent(variant::setMass);
        optional(form.getPrice()).ifPresent(variant::setPrice);

        removeImages(variant, form.getImagesToRemoveIds());

        final List<ProductVariantImage> addedImages = addImages(variant, form.getImagesToAddIds());

        return UpdateProductVariantResult.builder()
                .updatedVariant(variant)
                .addedImages(addedImages)
                .build();
    }

    public void deleteVariant(@NotNull final Long productId, @NotNull final Long variantId) {
        final Product product = productRepository.findById(productId);

        final ProductVariant variant = variantRepository.findById(variantId)
                .verifyBelongsToProduct(product);

        product.removeVariant(variant);
    }

    public ProductCategory createCategory(@NotNull @Valid final CreateProductCategoryForm form) {
        final ProductCategory category = categoryRepository.save(
                ProductCategory.builder()
                        .name(form.getName())
                        .description(form.getDescription())
                        .build()
        );

        setImage(category, form.getImageId());

        return category;
    }

    public ProductCategory updateCategory(@NotNull final Long categoryId, @NotNull @Valid final UpdateProductCategoryForm form) {
        final ProductCategory category = categoryRepository.findById(categoryId);

        optional(form.getName()).ifPresent(category::setName);
        optional(form.getDescription()).ifPresent(category::setDescription);

        setImage(category, form.getNewImageId());

        return category;
    }

    public ProductCategory createSubCategory(
            @NotNull final Long parentCategoryId,
            @NotNull @Valid final CreateProductCategoryForm form
    ) {
        final ProductCategory parentCategory = categoryRepository.findById(parentCategoryId);

        final ProductCategory subCategory = categoryRepository.save(
                parentCategory.newSubCategory()
                        .name(form.getName())
                        .description(form.getDescription())
                        .add()
        );

        setImage(subCategory, form.getImageId());

        return subCategory;
    }

    public void deleteCategory(@NotNull final Long categoryId) {
        final ProductCategory category = categoryRepository.findById(categoryId);

        if (category.isRootCategory()) {
            removeCategoryImages(category, getAllCategoryImageFilenames(category));
        }

        categoryRepository.remove(category);
    }

    public ProductTag createTag(@NotNull @Valid final CreateProductTagForm form) {
        return tagRepository.save(
                ProductTag.builder()
                        .name(form.getName())
                        .build()
        );
    }

    public ProductTag updateTag(@NotNull final Long tagId, @NotNull @Valid final UpdateProductTagForm form) {
        final ProductTag tag = tagRepository.findById(tagId);

        optional(form.getName()).ifPresent(tag::setName);

        return tag;
    }

    public void deleteTag(@NotNull final Long tagId) {
        final ProductTag tag = tagRepository.findById(tagId);

        tagRepository.remove(tag);
    }

    private void setCategory(final Product product, final Long categoryId) {
        if (categoryId == null) {
            return;
        }
        final ProductCategory category = categoryRepository.findById(categoryId);

        product.setCategory(category);
    }

    private void setImage(final Product product, final UUID imageId) {
        if (imageId == null) {
            return;
        }
        final UploadedImage uploadedImage = uploadedImageRepository.findById(imageId);

        product.setImageFilename(uploadedImage.getFilename());

        assignProductImages(product, Collections.singletonList(uploadedImage));
    }

    private void setImage(final ProductCategory category,  final UUID imageId) {
        if (imageId == null) {
            return;
        }
        final UploadedImage uploadedImage = uploadedImageRepository.findById(imageId);

        category.setImageFilename(uploadedImage.getFilename());

        assignCategoryImage(category, uploadedImage);
    }

    private List<ProductVariantImage> addImages(final ProductVariant variant, final Set<UUID> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }
        final List<UploadedImage> uploadedImages = uploadedImageRepository.findAllById(ids);

        if (uploadedImages.isEmpty()) {
            return Collections.emptyList();
        }

        assignProductImages(variant.getProduct(), uploadedImages);

        return variantImageRepository.saveAll(
                uploadedImages.stream()
                        .map(uploadedImage -> ProductVariantImage.builder()
                                .variant(variant)
                                .filename(uploadedImage.getFilename())
                                .build())
                        .collect(Collectors.toList())
        );
    }

    private void removeImages(final ProductVariant variant, final Set<Long> imageIds) {
        if (imageIds == null) {
            return;
        }
        final List<ProductVariantImage> imagesToRemove = variantImageRepository.findAllByIdDoNotThrow(imageIds).stream()
                .map(img -> img.verifyBelongsToVariant(variant))
                .collect(Collectors.toList());

        if (imagesToRemove.isEmpty()) {
            return;
        }

        variantImageRepository.removeAll(imagesToRemove);
    }

    private List<ProductTag> addTags(final Product product, final Set<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Collections.emptyList();
        }
        final List<ProductTag> tags = tagRepository.findAllById(tagIds);

        final List<ProductToTagAssociation> associations = tags.stream()
                .map(t -> ProductToTagAssociation.builder()
                        .product(product)
                        .tag(t)
                        .build())
                .collect(Collectors.toList());

        return productToTagAssociationRepository.saveAll(associations).stream()
                .map(ProductToTagAssociation::getTag)
                .collect(Collectors.toList());
    }

    private List<ProductTag> removeTags(final Product product, final Set<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Collections.emptyList();
        }
        final List<ProductTag> tagsToRemove = tagRepository.findAllByIdDoNotThrow(tagIds);

        final List<ProductToTagAssociation> removedAssociations = tagsToRemove.stream()
                .map(t -> ProductToTagAssociation.builder()
                        .product(product)
                        .tag(t)
                        .build())
                .collect(Collectors.toList());

        productToTagAssociationRepository.removeAll(removedAssociations);

        return removedAssociations.stream()
                .map(ProductToTagAssociation::getTag)
                .collect(Collectors.toList());
    }

    private void setDefaultVariant(final Product product, final Long defaultVariantId) {
        if (defaultVariantId == null) {
            return;
        }
        final ProductVariant defaultVariant = variantRepository.findById(defaultVariantId);

        product.setDefaultVariant(defaultVariant);
    }

    private Set<String> getAllCategoryImageFilenames(final ProductCategory category) {
        final Set<String> allFilenames = new HashSet<>();
        if (category.getImageFilename() != null) {
            allFilenames.add(category.getImageFilename());
        }
        allFilenames.addAll(categoryRepository.findSubCategoryFilenames(category));
        return allFilenames;
    }

    private Set<String> getAllProductImageFilenames(final Product product) {
        final Set<String> allFilenames = new HashSet<>();
        if (product.getImageFilename() != null) {
            allFilenames.add(product.getImageFilename());
        }
        allFilenames.addAll(variantImageRepository.findFilenamesByProductId(product.getId()));
        return allFilenames;
    }

    private void assignCategoryImage(final ProductCategory category, final UploadedImage image) {
        if (image == null) {
            return;
        }
        eventPublisher.publishEvent(
                AssignProductCategoryImageEvent.builder()
                        .uploadedImageId(image.getId())
                        .rootCategoryId(category.getRootCategoryId())
                        .build()
        );
    }

    private void removeCategoryImages(final ProductCategory category, final Set<String> filenames) {
        if (filenames == null || filenames.isEmpty()) {
            return;
        }
        eventPublisher.publishEvent(
                RemoveProductCategoryImagesEvent.builder()
                        .imageFilenames(filenames)
                        .rootCategoryId(category.getRootCategoryId())
                        .build()
        );
    }

    private void assignProductImages(final Product product, final List<UploadedImage> images) {
        if (images == null || images.isEmpty()) {
            return;
        }
        final Set<UUID> imageIds = images.stream()
                .map(UploadedImage::getId)
                .collect(Collectors.toSet());
        eventPublisher.publishEvent(
                AssignProductImagesEvent.builder()
                        .uploadedImageIds(imageIds)
                        .productId(product.getId())
                        .build()
        );
    }

    private void removeProductImages(final Product product, final Set<String> filenames) {
        if (filenames == null || filenames.isEmpty()) {
            return;
        }
        eventPublisher.publishEvent(
                RemoveProductImagesEvent.builder()
                        .imageFilenames(filenames)
                        .productId(product.getId())
                        .build()
        );
    }

}
