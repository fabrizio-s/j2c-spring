package com.j2c.j2c.service.application.impl;

import com.j2c.j2c.domain.entity.*;
import com.j2c.j2c.service.dto.ProductCategoryDTO;
import com.j2c.j2c.service.dto.ProductTagDTO;
import com.j2c.j2c.service.input.*;
import com.j2c.j2c.service.input.CreateProductCategoryForm.CreateProductCategoryFormBuilder;
import com.j2c.j2c.service.input.CreateProductForm.CreateProductFormBuilder;
import com.j2c.j2c.service.input.CreateProductTagForm.CreateProductTagFormBuilder;
import com.j2c.j2c.service.input.CreateProductVariantForm.CreateProductVariantFormBuilder;
import com.j2c.j2c.service.input.UpdateProductCategoryForm.UpdateProductCategoryFormBuilder;
import com.j2c.j2c.service.input.UpdateProductForm.UpdateProductFormBuilder;
import com.j2c.j2c.service.dto.ProductDTO;
import com.j2c.j2c.service.dto.ProductVariantDTO;
import com.j2c.j2c.service.exception.InvalidInputException;
import com.j2c.j2c.service.exception.ResourceNotFoundException;
import com.j2c.j2c.service.exception.ServiceException;
import com.j2c.j2c.service.input.UpdateProductTagForm.UpdateProductTagFormBuilder;
import com.j2c.j2c.service.input.UpdateProductVariantForm.UpdateProductVariantFormBuilder;
import com.j2c.j2c.service.test.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static com.j2c.j2c.service.exception.J2cServiceErrorMessages.*;
import static com.j2c.j2c.domain.exception.DomainErrorMessages.*;
import static com.j2c.j2c.service.test.MockEntity.*;
import static com.j2c.j2c.service.test.TestUtils.nullable;
import static com.j2c.j2c.service.test.TestUtils.removeFirst;
import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest extends BaseServiceTest {

    @Autowired
    private ProductServiceImpl service;

    @Autowired
    private ProductServiceStubber stubber;

    @Autowired
    private MockBeanProvider mockBeanProvider;

    @Test
    public void create_NullForm_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.create(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void create_AnyTagIdIsNull_ShouldThrowInvalidInputException() {
        final CreateProductForm form = hpCreateProductForm()
                .tagIds(new HashSet<>(Arrays.asList(1L, null)))
                .build();
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.create(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("tag ids must not contain null elements"))
        );
    }

    @Test
    public void create_NullName_ShouldThrowInvalidInputException() {
        final CreateProductForm form = hpCreateProductForm()
                .name(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.create(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void create_BlankName_ShouldThrowInvalidInputException() {
        final CreateProductForm form = hpCreateProductForm()
                .name("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.create(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void create_BlankDescription_ShouldThrowInvalidInputException() {
        final CreateProductForm form = hpCreateProductForm()
                .description("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.create(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("description must not be blank"))
        );
    }

    @Test
    public void create_NullDigital_ShouldThrowInvalidInputException() {
        final CreateProductForm form = hpCreateProductForm()
                .digital(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.create(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("digital must not be null"))
        );
    }

    @Test
    public void create_NonPositiveMass_ShouldThrowInvalidInputException() {
        final CreateProductForm form = hpCreateProductForm()
                .mass(-1)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.create(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("mass must be greater than 0"))
        );
    }

    @Test
    public void create_NullPrice_ShouldThrowInvalidInputException() {
        final CreateProductForm form = hpCreateProductForm()
                .price(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.create(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("price must not be null"))
        );
    }

    @Test
    public void create_NegativePrice_ShouldThrowInvalidInputException() {
        final CreateProductForm form = hpCreateProductForm()
                .price(-1L)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.create(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("price must be greater than or equal to 0"))
        );
    }

    @Test
    public void create_ImageDoesNotExist_ShouldThrowResourceNotFoundException() {
        final CreateProductForm form = hpCreateProductForm().build();

        stubber.create()
                .categoryForNewProduct(categoryWithId(form.getCategoryId()))
                .tagsForNewProduct(tagsWithIds(form.getTagIds()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.create(form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "uploaded image", form.getImageId())));
    }

    @Test
    public void create_CategoryDoesNotExist_ShouldThrowResourceNotFoundException() {
        final CreateProductForm form = hpCreateProductForm().build();

        stubber.create()
                .imageForNewProduct(uploadedImageWithId(form.getImageId()))
                .tagsForNewProduct(tagsWithIds(form.getTagIds()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.create(form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "product category", form.getCategoryId())));
    }

    @Test
    public void create_AnyTagDoesNotExist_ShouldThrowResourceNotFoundException() {
        final CreateProductForm form = hpCreateProductForm().build();

        stubber.create()
                .imageForNewProduct(uploadedImageWithId(form.getImageId()))
                .categoryForNewProduct(categoryWithId(form.getCategoryId()))
                .tagsForNewProduct(removeFirst(tagsWithIds(form.getTagIds())))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.create(form)
        );
        assertTrue(exception.getMessage().matches(String.format(RESOURCES_NOT_FOUND, "product tag", "[0-9]+")));
    }

    @Test
    public void create_DigitalIsFalseAndNullMass_ShouldThrowServiceException() {
        final CreateProductForm form = hpCreateProductForm()
                .digital(false)
                .mass(null)
                .build();

        stubber.create()
                .imageForNewProduct(uploadedImageWithId(form.getImageId()))
                .categoryForNewProduct(categoryWithId(form.getCategoryId()))
                .tagsForNewProduct(tagsWithIds(form.getTagIds()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.create(form)
        );
        assertTrue(exception.getMessage().matches(String.format(VARIANT_REQUIRES_MASS, "[0-9]+")));
    }

    @Test
    public void create_HappyPath_ShouldReturnCreatedProductAndVariant() {
        final CreateProductForm form = hpCreateProductForm().build();

        final UploadedImage uploadedImage = uploadedImageWithId(form.getImageId());
        stubber.create()
                .imageForNewProduct(uploadedImage)
                .categoryForNewProduct(categoryWithId(form.getCategoryId()))
                .tagsForNewProduct(tagsWithIds(form.getTagIds()))
                .stub();

        final ProductDTO productDTO = service.create(form);

        assertNotNull(productDTO);
        assertEquals(form.getName(), productDTO.getName());
        assertEquals(form.getDescription(), productDTO.getDescription());
        assertEquals(uploadedImage.getFilename(), productDTO.getImageFilename());
        assertEquals(form.getDigital(), productDTO.isDigital());
        assertEquals(form.getPrice(), productDTO.getDefaultPrice());
        assertEquals(form.getCategoryId(), productDTO.getCategoryId());
        assertEquals(form.getTagIds().size(), productDTO.getTags().size());

        final ProductVariantDTO variantDTO = productDTO.getDefaultVariant();

        assertNotNull(variantDTO);
        assertEquals(form.getMass(), variantDTO.getMass());

        verify(mockBeanProvider.getProductRepository(), times(1))
                .save(any(Product.class));
        verify(mockBeanProvider.getProductToTagAssociationRepository(), times(1))
                .saveAll(anyIterable());
        verify(mockBeanProvider.getImageStore(), times(1))
                .assignToProduct(anySet(), any(Long.class));
    }

    @Test
    public void create_NullDescription_ShouldReturnNullProductDescription() {
        final CreateProductForm form = hpCreateProductForm()
                .description(null)
                .build();

        stubber.create()
                .imageForNewProduct(uploadedImageWithId(form.getImageId()))
                .categoryForNewProduct(categoryWithId(form.getCategoryId()))
                .tagsForNewProduct(tagsWithIds(form.getTagIds()))
                .stub();

        final ProductDTO productDTO = service.create(form);

        assertNull(productDTO.getDescription());
    }

    @Test
    public void create_NullImageId_ShouldReturnNullProductImageFilename() {
        final CreateProductForm form = hpCreateProductForm()
                .imageId(null)
                .build();

        stubber.create()
                .imageForNewProduct(uploadedImageWithId(form.getImageId()))
                .categoryForNewProduct(categoryWithId(form.getCategoryId()))
                .tagsForNewProduct(tagsWithIds(form.getTagIds()))
                .stub();

        final ProductDTO productDTO = service.create(form);

        assertNull(productDTO.getImageFilename());
    }

    @Test
    public void create_NullCategoryId_ShouldReturnNullProductCategoryId() {
        final CreateProductForm form = hpCreateProductForm()
                .categoryId(null)
                .build();

        stubber.create()
                .imageForNewProduct(uploadedImageWithId(form.getImageId()))
                .categoryForNewProduct(categoryWithId(form.getCategoryId()))
                .tagsForNewProduct(tagsWithIds(form.getTagIds()))
                .stub();

        final ProductDTO productDTO = service.create(form);

        assertNull(productDTO.getCategoryId());
    }

    @Test
    public void create_NullTagIds_ShouldReturnEmptyProductTags() {
        final CreateProductForm form = hpCreateProductForm()
                .tagIds(null)
                .build();

        stubber.create()
                .imageForNewProduct(uploadedImageWithId(form.getImageId()))
                .categoryForNewProduct(categoryWithId(form.getCategoryId()))
                .tagsForNewProduct(tagsWithIds(form.getTagIds()))
                .stub();

        final ProductDTO productDTO = service.create(form);

        assertTrue(productDTO.getTags().isEmpty());
    }

    @Test
    public void create_DigitalIsTrue_ShouldReturnNullVariantMass() {
        final CreateProductForm form = hpCreateProductForm()
                .digital(true)
                .build();

        stubber.create()
                .imageForNewProduct(uploadedImageWithId(form.getImageId()))
                .categoryForNewProduct(categoryWithId(form.getCategoryId()))
                .tagsForNewProduct(tagsWithIds(form.getTagIds()))
                .stub();

        final ProductVariantDTO variantDTO = service.create(form).getDefaultVariant();

        assertNull(variantDTO.getMass());
    }

    @Test
    public void update_NullProductId_ShouldThrowInvalidInputException() {
        final UpdateProductForm form = hpUpdateProductForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.update(null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("product id must not be null"))
        );
    }

    @Test
    public void update_NullForm_ShouldThrowInvalidInputException() {
        final Long productId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.update(productId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void update_BlankName_ShouldThrowInvalidInputException() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm()
                .name("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.update(productId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void update_BlankDescription_ShouldThrowInvalidInputException() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm()
                .description("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.update(productId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("description must not be blank"))
        );
    }

    @Test
    public void update_NegativePrice_ShouldThrowInvalidInputException() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm()
                .price(-1L)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.update(productId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("price must be greater than or equal to 0"))
        );
    }

    @Test
    public void update_ProductDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm().build();

        stubber.update()
                .newImageForProduct(uploadedImageWithId(form.getNewImageId()))
                .newCategoryForProduct(categoryWithId(form.getCategoryId()))
                .tagsToAdd(tagsWithIds(form.getTagsToAdd()))
                .tagsToRemove(tagsWithIds(form.getTagsToRemove()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(productId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(RESOURCE_NOT_FOUND, "product", productId)));
    }

    @Test
    public void update_ImageDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm().build();

        final Product productToUpdate = productWithId(productId);
        stubber.update()
                .productToUpdate(productToUpdate)
                .newDefaultVariant(variantWithIdForProduct(form.getDefaultVariantId(), productToUpdate))
                .newCategoryForProduct(categoryWithId(form.getCategoryId()))
                .tagsToAdd(tagsWithIds(form.getTagsToAdd()))
                .tagsToRemove(tagsWithIds(form.getTagsToRemove()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(productId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(RESOURCE_NOT_FOUND, "uploaded image", form.getNewImageId())));
    }

    @Test
    public void update_VariantDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm().build();

        stubber.update()
                .productToUpdate(productWithId(productId))
                .newImageForProduct(uploadedImageWithId(form.getNewImageId()))
                .newCategoryForProduct(categoryWithId(form.getCategoryId()))
                .tagsToAdd(tagsWithIds(form.getTagsToAdd()))
                .tagsToRemove(tagsWithIds(form.getTagsToRemove()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(productId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(RESOURCE_NOT_FOUND, "product variant", form.getDefaultVariantId())));
    }

    @Test
    public void update_CategoryDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm().build();

        final Product productToUpdate = productWithId(productId);
        stubber.update()
                .productToUpdate(productToUpdate)
                .newImageForProduct(uploadedImageWithId(form.getNewImageId()))
                .newDefaultVariant(variantWithIdForProduct(form.getDefaultVariantId(), productToUpdate))
                .tagsToAdd(tagsWithIds(form.getTagsToAdd()))
                .tagsToRemove(tagsWithIds(form.getTagsToRemove()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(productId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(RESOURCE_NOT_FOUND, "product category", form.getCategoryId())));
    }

    @Test
    public void update_AnyTagToAddDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm().build();

        final Product productToUpdate = productWithId(productId);
        stubber.update()
                .productToUpdate(productToUpdate)
                .newImageForProduct(uploadedImageWithId(form.getNewImageId()))
                .newDefaultVariant(variantWithIdForProduct(form.getDefaultVariantId(), productToUpdate))
                .newCategoryForProduct(categoryWithId(form.getCategoryId()))
                .tagsToAdd(removeFirst(tagsWithIds(form.getTagsToAdd())))
                .tagsToRemove(tagsWithIds(form.getTagsToRemove()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(productId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(RESOURCES_NOT_FOUND, "product tag", "[0-9]+")));
    }

    @Test
    public void update_DefaultVariantDoesNotBelongToProduct_ShouldThrowServiceException() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm().build();

        stubber.update()
                .productToUpdate(productWithId(productId))
                .newImageForProduct(uploadedImageWithId(form.getNewImageId()))
                .newDefaultVariant(variantWithIdForProduct(form.getDefaultVariantId(), product().build()))
                .newCategoryForProduct(categoryWithId(form.getCategoryId()))
                .tagsToAdd(tagsWithIds(form.getTagsToAdd()))
                .tagsToRemove(tagsWithIds(form.getTagsToRemove()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.update(productId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(CANNOT_SET_UNOWNED_DEFAULT_VARIANT, productId)));
    }

    @Test
    public void update_ChangingDefaultVariantWhenCurrentHasNoName_ShouldThrowServiceException() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm().build();

        final Product productToUpdate = product()
                .id(productId)
                .defaultVariant(
                        productVariant()
                                .nullName(true)
                                .build()
                )
                .build();
        stubber.update()
                .productToUpdate(productToUpdate)
                .newImageForProduct(uploadedImageWithId(form.getNewImageId()))
                .newDefaultVariant(variantWithIdForProduct(form.getDefaultVariantId(), productToUpdate))
                .newCategoryForProduct(categoryWithId(form.getCategoryId()))
                .tagsToAdd(tagsWithIds(form.getTagsToAdd()))
                .tagsToRemove(tagsWithIds(form.getTagsToRemove()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.update(productId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(CANNOT_SET_DEFAULT_VARIANT_IF_CURRENT_HAS_NO_NAME, "[0-9]+")));
    }

    @Test
    public void update_HappyPath_ShouldReturnUpdatedProduct() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm().build();

        final Product productToUpdate = productWithId(productId);
        final UploadedImage uploadedImage = uploadedImageWithId(form.getNewImageId());
        stubber.update()
                .productToUpdate(productToUpdate)
                .newImageForProduct(uploadedImage)
                .newDefaultVariant(variantWithIdForProduct(form.getDefaultVariantId(), productToUpdate))
                .newCategoryForProduct(categoryWithId(form.getCategoryId()))
                .tagsToAdd(tagsWithIds(form.getTagsToAdd()))
                .tagsToRemove(tagsWithIds(form.getTagsToRemove()))
                .stub();

        final ProductDTO productDTO = service.update(productId, form);

        assertNotNull(productDTO);
        assertEquals(form.getName(), productDTO.getName());
        assertEquals(form.getDescription(), productDTO.getDescription());
        assertEquals(uploadedImage.getFilename(), productDTO.getImageFilename());
        assertEquals(form.getPrice(), productDTO.getDefaultPrice());
        assertEquals(form.getDefaultVariantId(), productDTO.getDefaultVariantId());
        assertEquals(form.getCategoryId(), productDTO.getCategoryId());
        assertEquals(form.getTagsToAdd().size(), productDTO.getAddedTags().size());
        assertEquals(form.getTagsToRemove().size(), productDTO.getRemovedTags().size());

        verify(mockBeanProvider.getProductToTagAssociationRepository(), times(1))
                .saveAll(anyIterable());
        verify(mockBeanProvider.getProductToTagAssociationRepository(), times(1))
                .deleteAll(anyIterable());
        verify(mockBeanProvider.getImageStore(), times(1))
                .assignToProduct(anySet(), any(Long.class));
    }

    @Test
    public void update_NullName_ShouldNotUpdateProductName() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm()
                .name(null)
                .build();

        final Product productToUpdate = productWithId(productId);
        stubber.update()
                .productToUpdate(productToUpdate)
                .newImageForProduct(uploadedImageWithId(form.getNewImageId()))
                .newDefaultVariant(variantWithIdForProduct(form.getDefaultVariantId(), productToUpdate))
                .newCategoryForProduct(categoryWithId(form.getCategoryId()))
                .tagsToAdd(tagsWithIds(form.getTagsToAdd()))
                .tagsToRemove(tagsWithIds(form.getTagsToRemove()))
                .stub();

        final ProductDTO productDTO = service.update(productId, form);

        assertNotNull(productDTO.getName());
    }

    @Test
    public void update_NullImageId_ShouldNotUpdateProductImageFilename() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm()
                .newImageId(null)
                .build();

        final Product productToUpdate = productWithId(productId);
        stubber.update()
                .productToUpdate(productToUpdate)
                .newImageForProduct(uploadedImageWithId(form.getNewImageId()))
                .newDefaultVariant(variantWithIdForProduct(form.getDefaultVariantId(), productToUpdate))
                .newCategoryForProduct(categoryWithId(form.getCategoryId()))
                .tagsToAdd(tagsWithIds(form.getTagsToAdd()))
                .tagsToRemove(tagsWithIds(form.getTagsToRemove()))
                .stub();

        final ProductDTO productDTO = service.update(productId, form);

        assertNotNull(productDTO.getImageFilename());
    }

    @Test
    public void update_NullDescription_ShouldNotUpdateProductDescription() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm()
                .description(null)
                .build();

        final Product productToUpdate = productWithId(productId);
        stubber.update()
                .productToUpdate(productToUpdate)
                .newImageForProduct(uploadedImageWithId(form.getNewImageId()))
                .newDefaultVariant(variantWithIdForProduct(form.getDefaultVariantId(), productToUpdate))
                .newCategoryForProduct(categoryWithId(form.getCategoryId()))
                .tagsToAdd(tagsWithIds(form.getTagsToAdd()))
                .tagsToRemove(tagsWithIds(form.getTagsToRemove()))
                .stub();

        final ProductDTO productDTO = service.update(productId, form);

        assertNotNull(productDTO.getDescription());
    }

    @Test
    public void update_NullPrice_ShouldNotUpdateProductDefaultPrice() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm()
                .price(null)
                .build();

        final Product productToUpdate = productWithId(productId);
        stubber.update()
                .productToUpdate(productToUpdate)
                .newImageForProduct(uploadedImageWithId(form.getNewImageId()))
                .newDefaultVariant(variantWithIdForProduct(form.getDefaultVariantId(), productToUpdate))
                .newCategoryForProduct(categoryWithId(form.getCategoryId()))
                .tagsToAdd(tagsWithIds(form.getTagsToAdd()))
                .tagsToRemove(tagsWithIds(form.getTagsToRemove()))
                .stub();

        final ProductDTO productDTO = service.update(productId, form);

        assertNotNull(productDTO.getDefaultPrice());
    }

    @Test
    public void update_NullDefaultVariantId_ShouldNotUpdateProductDefaultVariant() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm()
                .defaultVariantId(null)
                .build();

        final Product productToUpdate = productWithId(productId);
        stubber.update()
                .productToUpdate(productToUpdate)
                .newImageForProduct(uploadedImageWithId(form.getNewImageId()))
                .newDefaultVariant(variantWithIdForProduct(form.getDefaultVariantId(), productToUpdate))
                .newCategoryForProduct(categoryWithId(form.getCategoryId()))
                .tagsToAdd(tagsWithIds(form.getTagsToAdd()))
                .tagsToRemove(tagsWithIds(form.getTagsToRemove()))
                .stub();

        final ProductDTO productDTO = service.update(productId, form);

        assertNotNull(productDTO.getDefaultVariantId());
    }

    @Test
    public void update_NullCategoryId_ShouldNotUpdateProductCategory() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm()
                .categoryId(null)
                .build();

        final Product productToUpdate = productWithId(productId);
        stubber.update()
                .productToUpdate(productToUpdate)
                .newImageForProduct(uploadedImageWithId(form.getNewImageId()))
                .newDefaultVariant(variantWithIdForProduct(form.getDefaultVariantId(), productToUpdate))
                .newCategoryForProduct(categoryWithId(form.getCategoryId()))
                .tagsToAdd(tagsWithIds(form.getTagsToAdd()))
                .tagsToRemove(tagsWithIds(form.getTagsToRemove()))
                .stub();

        final ProductDTO productDTO = service.update(productId, form);

        assertNotNull(productDTO.getCategoryId());
    }

    @Test
    public void update_NullTagsToAdd_ShouldReturnEmptyAddedTags() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm()
                .tagsToAdd(null)
                .build();

        final Product productToUpdate = productWithId(productId);
        stubber.update()
                .productToUpdate(productToUpdate)
                .newImageForProduct(uploadedImageWithId(form.getNewImageId()))
                .newDefaultVariant(variantWithIdForProduct(form.getDefaultVariantId(), productToUpdate))
                .newCategoryForProduct(categoryWithId(form.getCategoryId()))
                .tagsToAdd(tagsWithIds(form.getTagsToAdd()))
                .tagsToRemove(tagsWithIds(form.getTagsToRemove()))
                .stub();

        final ProductDTO productDTO = service.update(productId, form);

        assertTrue(productDTO.getAddedTags().isEmpty());
    }

    @Test
    public void update_NullTagsToRemove_ShouldReturnEmptyRemovedTags() {
        final Long productId = 1L;
        final UpdateProductForm form = hpUpdateProductForm()
                .tagsToRemove(null)
                .build();

        final Product productToUpdate = productWithId(productId);
        stubber.update()
                .productToUpdate(productToUpdate)
                .newImageForProduct(uploadedImageWithId(form.getNewImageId()))
                .newDefaultVariant(variantWithIdForProduct(form.getDefaultVariantId(), productToUpdate))
                .newCategoryForProduct(categoryWithId(form.getCategoryId()))
                .tagsToAdd(tagsWithIds(form.getTagsToAdd()))
                .tagsToRemove(tagsWithIds(form.getTagsToRemove()))
                .stub();

        final ProductDTO productDTO = service.update(productId, form);

        assertTrue(productDTO.getRemovedTags().isEmpty());
    }

    @Test
    public void publish_NullProductId_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.publish(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("product id must not be null"))
        );
    }

    @Test
    public void publish_ProductDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long productId = 1L;

        stubber.publish()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.publish(productId)
        );
        assertTrue(exception.getMessage().matches(String.format(RESOURCE_NOT_FOUND, "product", productId)));
    }

    @Test
    public void publish_ProductIsAlreadyPublished_ShouldThrowServiceException() {
        final Long productId = 1L;

        stubber.publish()
                .productToPublish(
                        product()
                                .id(productId)
                                .published(true)
                                .lastUnpublished(now().minusDays(5))
                                .build()
                )
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.publish(productId)
        );
        assertTrue(exception.getMessage().matches(String.format(ALREADY_PUBLISHED, productId)));
    }

    @Test
    public void publish_ProductHasNoDefaultVariant_ShouldThrowServiceException() {
        final Long productId = 1L;

        stubber.publish()
                .productToPublish(
                        product()
                                .id(productId)
                                .published(false)
                                .lastUnpublished(now().minusDays(5))
                                .nullDefaultVariant(true)
                                .build()
                )
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.publish(productId)
        );
        assertTrue(exception.getMessage().matches(String.format(CANNOT_PUBLISH_WITHOUT_DEFAULT_VARIANT, productId)));
    }

    @Test
    public void publish_LastUnpublishedIsWithinXConfiguredMinutes_ShouldThrowServiceException() {
        final Long productId = 1L;

        stubber.publish()
                .productToPublish(
                        product()
                                .id(productId)
                                .published(false)
                                .lastUnpublished(now().minusSeconds(1))
                                .build()
                )
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.publish(productId)
        );
        assertTrue(exception.getMessage().matches(String.format(CANNOT_PUBLISH_BEFORE_X_MINUTES, productId, "[0-9]+")));
    }

    @Test
    public void publish_HappyPath_ShouldReturnPublishedProduct() {
        final Long productId = 1L;

        stubber.publish()
                .productToPublish(
                        product()
                                .id(productId)
                                .published(false)
                                .lastUnpublished(now().minusDays(5))
                                .build()
                )
                .stub();

        final ProductDTO productDTO = service.publish(productId);

        assertNotNull(productDTO);
        assertTrue(productDTO.isPublished());
    }

    @Test
    public void unpublish_NullProductId_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.unpublish(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("product id must not be null"))
        );
    }

    @Test
    public void unpublish_ProductDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long productId = 1L;

        stubber.unpublish()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.unpublish(productId)
        );
        assertTrue(exception.getMessage().matches(String.format(RESOURCE_NOT_FOUND, "product", productId)));
    }

    @Test
    public void unpublish_ProductIsAlreadyUnpublished_ShouldThrowServiceException() {
        final Long productId = 1L;

        stubber.unpublish()
                .productToUnpublish(productWithId(productId))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.unpublish(productId)
        );
        assertTrue(exception.getMessage().matches(String.format(ALREADY_UNPUBLISHED, productId)));
    }

    @Test
    public void unpublish_HappyPath_ShouldReturnUpdatedProduct() {
        final Long productId = 1L;

        stubber.unpublish()
                .productToUnpublish(
                        product()
                                .id(productId)
                                .published(true)
                                .build()
                )
                .stub();

        final ProductDTO productDTO = service.unpublish(productId);

        assertNotNull(productDTO);
        assertFalse(productDTO.isPublished());
    }

    @Test
    public void delete_NullProductId_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.delete(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("product id must not be null"))
        );
    }

    @Test
    public void delete_ProductDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long productId = 1L;

        stubber.delete()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(productId)
        );
        assertTrue(exception.getMessage().matches(String.format(RESOURCE_NOT_FOUND, "product", productId)));
    }

    @Test
    public void delete_HappyPath_ShouldDeleteProductAndAllChildEntities() {
        final Long productId = 1L;

        stubber.delete()
                .productToDelete(
                        product()
                                .id(productId)
                                .published(false)
                                .build()
                )
                .stub();

        service.delete(productId);

        verify(mockBeanProvider.getProductRepository(), times(1))
                .delete(any(Product.class));
        verify(mockBeanProvider.getImageStore(), times(1))
                .removeProductImages(any(Long.class), anySet());
    }

    @Test
    public void createVariant_NullProductId_ShouldThrowInvalidInputException() {
        final CreateProductVariantForm form = hpCreateProductVariantForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createVariant(null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("product id must not be null"))
        );
    }

    @Test
    public void createVariant_NullForm_ShouldThrowInvalidInputException() {
        final Long productId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createVariant(productId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void createVariant_BlankDefaultVariantName_ShouldThrowInvalidInputException() {
        final Long productId = 1L;
        final CreateProductVariantForm form = hpCreateProductVariantForm()
                .defaultVariantName("        ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createVariant(productId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("default variant name must not be blank"))
        );
    }

    @Test
    public void createVariant_NullName_ShouldThrowInvalidInputException() {
        final Long productId = 1L;
        final CreateProductVariantForm form = hpCreateProductVariantForm()
                .name(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createVariant(productId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void createVariant_BlankName_ShouldThrowInvalidInputException() {
        final Long productId = 1L;
        final CreateProductVariantForm form = hpCreateProductVariantForm()
                .name("    ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createVariant(productId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void createVariant_NonPositiveMass_ShouldThrowInvalidInputException() {
        final Long productId = 1L;
        final CreateProductVariantForm form = hpCreateProductVariantForm()
                .mass(-1)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createVariant(productId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("mass must be greater than 0"))
        );
    }

    @Test
    public void createVariant_NegativePrice_ShouldThrowInvalidInputException() {
        final Long productId = 1L;
        final CreateProductVariantForm form = hpCreateProductVariantForm()
                .price(-1L)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createVariant(productId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("price must be greater than or equal to 0"))
        );
    }

    @Test
    public void createVariant_ProductDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long productId = 1L;
        final CreateProductVariantForm form = hpCreateProductVariantForm().build();

        stubber.createVariant()
                .imagesForNewVariant(uploadedImagesWithIds(form.getImageIds()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.createVariant(productId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "product", productId)));
    }

    @Test
    public void createVariant_AnyImageDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long productId = 1L;
        final CreateProductVariantForm form = hpCreateProductVariantForm().build();

        stubber.createVariant()
                .parentProduct(productWithId(productId))
                .imagesForNewVariant(removeFirst(uploadedImagesWithIds(form.getImageIds())))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.createVariant(productId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(RESOURCES_NOT_FOUND, "uploaded image", "\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b")));
    }

    @Test
    public void createVariant_ProductDefaultVariantNameIsNull_ShouldThrowServiceException() {
        final Long productId = 1L;
        final CreateProductVariantForm form = hpCreateProductVariantForm().build();

        stubber.createVariant()
                .parentProduct(
                        product()
                                .id(productId)
                                .defaultVariant(
                                        productVariant()
                                                .nullName(true)
                                                .build()
                                )
                                .build()
                )
                .imagesForNewVariant(uploadedImagesWithIds(form.getImageIds()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.createVariant(productId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(DEFAULT_VARIANT_NAME_REQUIRED, productId)));
    }

    @Test
    public void createVariant_NullMassAndProductIsNotDigital_ShouldThrowServiceException() {
        final Long productId = 1L;
        final CreateProductVariantForm form = hpCreateProductVariantForm()
                .mass(null)
                .build();

        stubber.createVariant()
                .parentProduct(
                        product()
                                .id(productId)
                                .digital(false)
                                .build()
                )
                .imagesForNewVariant(uploadedImagesWithIds(form.getImageIds()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.createVariant(productId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(VARIANT_REQUIRES_MASS, productId)));
    }

    @Test
    public void createVariant_HappyPath_ShouldReturnCreatedProductVariant() {
        final Long productId = 1L;
        final CreateProductVariantForm form = hpCreateProductVariantForm().build();

        stubber.createVariant()
                .parentProduct(productWithId(productId))
                .imagesForNewVariant(uploadedImagesWithIds(form.getImageIds()))
                .stub();

        final ProductVariantDTO variantDTO = service.createVariant(productId, form);

        assertNotNull(variantDTO);
        assertEquals(form.getName(), variantDTO.getName());
        assertEquals(form.getMass(), variantDTO.getMass());
        assertEquals(form.getPrice(), variantDTO.getPrice());
        assertEquals(form.getImageIds().size(), variantDTO.getImages().size());

        verify(mockBeanProvider.getProductVariantImageRepository(), times(1))
                .saveAll(anyIterable());
    }

    @Test
    public void createVariant_PositiveMassAndProductIsDigital_ShouldIgnoreAndSetMassToNull() {
        final Long productId = 1L;
        final CreateProductVariantForm form = hpCreateProductVariantForm().build();

        stubber.createVariant()
                .parentProduct(
                        product()
                                .id(productId)
                                .digital(true)
                                .build()
                )
                .imagesForNewVariant(uploadedImagesWithIds(form.getImageIds()))
                .stub();

        final ProductVariantDTO variantDTO = service.createVariant(productId, form);

        assertNull(variantDTO.getMass());
    }

    @Test
    public void updateVariant_NullVariantId_ShouldThrowInvalidInputException() {
        final Long productId = 1L;
        final UpdateProductVariantForm form = hpUpdateProductVariantForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateVariant(productId, null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("variant id must not be null"))
        );
    }

    @Test
    public void updateVariant_NullForm_ShouldThrowInvalidInputException() {
        final Long productId = 1L;
        final Long variantId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateVariant(productId, variantId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void updateVariant_BlankName_ShouldThrowInvalidInputException() {
        final Long productId = 1L;
        final Long variantId = 1L;
        final UpdateProductVariantForm form = hpUpdateProductVariantForm()
                .name("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateVariant(productId, variantId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void updateVariant_NonPositiveMass_ShouldThrowInvalidInputException() {
        final Long productId = 1L;
        final Long variantId = 1L;
        final UpdateProductVariantForm form = hpUpdateProductVariantForm()
                .mass(-1)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateVariant(productId, variantId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("mass must be greater than 0"))
        );
    }

    @Test
    public void updateVariant_NegativePrice_ShouldThrowInvalidInputException() {
        final Long productId = 1L;
        final Long variantId = 1L;
        final UpdateProductVariantForm form = hpUpdateProductVariantForm()
                .price(-1L)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateVariant(productId, variantId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("price must be greater than or equal to 0"))
        );
    }

    @Test
    public void updateVariant_ProductDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long productId = 1L;
        final Long variantId = 1L;
        final UpdateProductVariantForm form = hpUpdateProductVariantForm().build();

        stubber.updateVariant()
                .imagesToAdd(uploadedImagesWithIds(form.getImagesToAddIds()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateVariant(productId, variantId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "product", productId)));
    }

    @Test
    public void updateVariant_VariantDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long productId = 1L;
        final Long variantId = 1L;
        final UpdateProductVariantForm form = hpUpdateProductVariantForm().build();

        final Product parentProduct = productWithId(productId);
        stubber.updateVariant()
                .parentProduct(parentProduct)
                .imagesToAdd(uploadedImagesWithIds(form.getImagesToAddIds()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateVariant(productId, variantId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "product variant", variantId)));
    }

    @Test
    public void updateVariant_VariantDoesNotBelongToProduct_ShouldThrowServiceException() {
        final Long productId = 1L;
        final Long variantId = 1L;
        final UpdateProductVariantForm form = hpUpdateProductVariantForm().build();

        final ProductVariant variantToUpdate = variantWithIdForProduct(variantId, product().build());
        stubber.updateVariant()
                .parentProduct(productWithId(productId))
                .variantToUpdate(variantToUpdate)
                .imagesToAdd(uploadedImagesWithIds(form.getImagesToAddIds()))
                .imagesToRemove(variantImagesWithIdsForVariant(form.getImagesToRemoveIds(), variantToUpdate))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.updateVariant(productId, variantId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(VARIANT_DOES_NOT_BELONG_TO_PRODUCT, variantId, productId)));
    }

    @Test
    public void updateVariant_AnyImageToAddDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long productId = 1L;
        final Long variantId = 1L;
        final UpdateProductVariantForm form = hpUpdateProductVariantForm().build();

        final Product parentProduct = productWithId(productId);
        final ProductVariant variant = variantWithIdForProduct(variantId, parentProduct);
        stubber.updateVariant()
                .parentProduct(parentProduct)
                .variantToUpdate(variant)
                .imagesToAdd(removeFirst(uploadedImagesWithIds(form.getImagesToAddIds())))
                .imagesToRemove(variantImagesWithIdsForVariant(form.getImagesToRemoveIds(), variant))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateVariant(productId, variantId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(RESOURCES_NOT_FOUND, "uploaded image", "\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b")));
    }

    @Test
    public void updateVariant_AnyImageToRemoveDoesNotBelongToVariant_ShouldThrowServiceException() {
        final Long productId = 1L;
        final Long variantId = 1L;
        final UpdateProductVariantForm form = hpUpdateProductVariantForm().build();

        final Product parentProduct = productWithId(productId);
        final ProductVariant variant = variantWithIdForProduct(variantId, parentProduct);
        stubber.updateVariant()
                .parentProduct(parentProduct)
                .variantToUpdate(variant)
                .imagesToAdd(uploadedImagesWithIds(form.getImagesToAddIds()))
                .imagesToRemove(variantImagesWithIdsForVariant(form.getImagesToRemoveIds(), productVariant().build()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.updateVariant(productId, variantId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(IMAGE_DOES_NOT_BELONG_TO_VARIANT, "[0-9]+", "[0-9]+")));
    }

    @Test
    public void updateVariant_HappyPath_ShouldReturnUpdatedVariant() {
        final Long productId = 1L;
        final Long variantId = 1L;
        final UpdateProductVariantForm form = hpUpdateProductVariantForm().build();

        final Product parentProduct = productWithId(productId);
        final ProductVariant variant = variantWithIdForProduct(variantId, parentProduct);
        stubber.updateVariant()
                .parentProduct(parentProduct)
                .variantToUpdate(variant)
                .imagesToAdd(uploadedImagesWithIds(form.getImagesToAddIds()))
                .imagesToRemove(variantImagesWithIdsForVariant(form.getImagesToRemoveIds(), variant))
                .stub();

        final ProductVariantDTO variantDTO = service.updateVariant(productId, variantId, form);

        assertNotNull(variantDTO);
        assertEquals(form.getName(), variantDTO.getName());
        assertEquals(form.getMass(), variantDTO.getMass());
        assertEquals(form.getPrice(), variantDTO.getPrice());
        assertEquals(form.getImagesToAddIds().size(), variantDTO.getAddedImages().size());

        verify(mockBeanProvider.getProductVariantImageRepository(), times(1))
                .saveAll(anyIterable());
        verify(mockBeanProvider.getImageStore(), times(1))
                .assignToProduct(anySet(), any(Long.class));
    }

    @Test
    public void updateVariant_NullName_ShouldNotUpdateField() {
        final Long productId = 1L;
        final Long variantId = 1L;
        final UpdateProductVariantForm form = hpUpdateProductVariantForm()
                .name(null)
                .build();

        final Product parentProduct = productWithId(productId);
        final ProductVariant variant = variantWithIdForProduct(variantId, parentProduct);
        stubber.updateVariant()
                .parentProduct(parentProduct)
                .variantToUpdate(variant)
                .imagesToAdd(uploadedImagesWithIds(form.getImagesToAddIds()))
                .imagesToRemove(variantImagesWithIdsForVariant(form.getImagesToRemoveIds(), variant))
                .stub();

        final ProductVariantDTO variantDTO = service.updateVariant(productId, variantId, form);

        assertNotEquals(form.getName(), variantDTO.getName());
    }

    @Test
    public void updateVariant_NullMass_ShouldNotUpdateField() {
        final Long productId = 1L;
        final Long variantId = 1L;
        final UpdateProductVariantForm form = hpUpdateProductVariantForm()
                .mass(null)
                .build();

        final Product parentProduct = productWithId(productId);
        final ProductVariant variant = variantWithIdForProduct(variantId, parentProduct);
        stubber.updateVariant()
                .parentProduct(parentProduct)
                .variantToUpdate(variant)
                .imagesToAdd(uploadedImagesWithIds(form.getImagesToAddIds()))
                .imagesToRemove(variantImagesWithIdsForVariant(form.getImagesToRemoveIds(), variant))
                .stub();

        final ProductVariantDTO variantDTO = service.updateVariant(productId, variantId, form);

        assertNotEquals(form.getMass(), variantDTO.getMass());
    }

    @Test
    public void updateVariant_MassIsNotNullButProductIsDigital_ShouldNotUpdateField() {
        final Long productId = 1L;
        final Long variantId = 1L;
        final UpdateProductVariantForm form = hpUpdateProductVariantForm().build();

        final Product parentProduct = product()
                .id(productId)
                .digital(true)
                .build();
        final ProductVariant variant = variantWithIdForProduct(variantId, parentProduct);
        stubber.updateVariant()
                .parentProduct(parentProduct)
                .variantToUpdate(variant)
                .imagesToAdd(uploadedImagesWithIds(form.getImagesToAddIds()))
                .imagesToRemove(variantImagesWithIdsForVariant(form.getImagesToRemoveIds(), variant))
                .stub();

        final ProductVariantDTO variantDTO = service.updateVariant(productId, variantId, form);

        assertNotEquals(form.getMass(), variantDTO.getMass());
    }

    @Test
    public void updateVariant_NullPrice_ShouldNotUpdateField() {
        final Long productId = 1L;
        final Long variantId = 1L;
        final UpdateProductVariantForm form = hpUpdateProductVariantForm()
                .price(null)
                .build();

        final Product parentProduct = productWithId(productId);
        final ProductVariant variant = variantWithIdForProduct(variantId, parentProduct);
        stubber.updateVariant()
                .parentProduct(parentProduct)
                .variantToUpdate(variant)
                .imagesToAdd(uploadedImagesWithIds(form.getImagesToAddIds()))
                .imagesToRemove(variantImagesWithIdsForVariant(form.getImagesToRemoveIds(), variant))
                .stub();

        final ProductVariantDTO variantDTO = service.updateVariant(productId, variantId, form);

        assertNotEquals(form.getPrice(), variantDTO.getPrice());
    }

    @Test
    public void deleteVariant_NullVariantId_ShouldThrowInvalidInputException() {
        final Long productId = 1L;
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.deleteVariant(productId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("variant id must not be null"))
        );
    }

    @Test
    public void deleteVariant_ProductDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long productId = 1L;
        final Long variantId = 1L;

        stubber.deleteVariant()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteVariant(productId, variantId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "product", productId)));
    }

    @Test
    public void deleteVariant_VariantDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long productId = 1L;
        final Long variantId = 1L;

        final Product parentProduct = productWithId(productId);
        stubber.deleteVariant()
                .parentProduct(parentProduct)
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteVariant(productId, variantId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "product variant", variantId)));
    }

    @Test
    public void deleteVariant_VariantDoesNotBelongToProduct_ShouldThrowServiceException() {
        final Long productId = 1L;
        final Long variantId = 1L;

        stubber.deleteVariant()
                .parentProduct(productWithId(productId))
                .variantToDelete(variantWithIdForProduct(variantId, product().build()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.deleteVariant(productId, variantId)
        );
        assertTrue(exception.getMessage().contains(String.format(VARIANT_DOES_NOT_BELONG_TO_PRODUCT, variantId, productId)));
    }

    @Test
    public void deleteVariant_DefaultVariant_ShouldThrowServiceException() {
        final Long productId = 1L;
        final Long variantId = 1L;

        final Product parentProduct = product()
                .id(productId)
                .defaultVariant(
                        productVariant()
                                .id(variantId)
                                .build()
                )
                .build();
        stubber.deleteVariant()
                .parentProduct(parentProduct)
                .variantToDelete(parentProduct.getDefaultVariant())
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.deleteVariant(productId, variantId)
        );
        assertTrue(exception.getMessage().contains(String.format(CANNOT_REMOVE_DEFAULT_VARIANT, variantId)));
    }

    @Test
    public void deleteVariant_HappyPath_ShouldDeleteVariant() {
        final Long productId = 1L;
        final Long variantId = 1L;

        final Product parentProduct = productWithId(productId);
        stubber.deleteVariant()
                .parentProduct(parentProduct)
                .variantToDelete(variantWithIdForProduct(variantId, parentProduct))
                .stub();

        service.deleteVariant(productId, variantId);
    }

    @Test
    public void createCategory_NullForm_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createCategory(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void createCategory_NullName_ShouldThrowInvalidInputException() {
        final CreateProductCategoryForm form = hpCreateProductCategoryForm()
                .name(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createCategory(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void createCategory_BlankName_ShouldThrowInvalidInputException() {
        final CreateProductCategoryForm form = hpCreateProductCategoryForm()
                .name("    ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createCategory(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void createCategory_BlankDescription_ShouldThrowInvalidInputException() {
        final CreateProductCategoryForm form = hpCreateProductCategoryForm()
                .description("    ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createCategory(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("description must not be blank"))
        );
    }

    @Test
    public void createCategory_UploadedImageDoesNotExist_ShouldThrowResourceNotFoundException() {
        final CreateProductCategoryForm form = hpCreateProductCategoryForm().build();

        stubber.createCategory().stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.createCategory(form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "uploaded image", form.getImageId())));
    }

    @Test
    public void createCategory_HappyPath_ShouldReturnCreatedCategory() {
        final CreateProductCategoryForm form = hpCreateProductCategoryForm().build();

        final UploadedImage uploadedImage = uploadedImageWithId(form.getImageId());
        stubber.createCategory()
                .imageForNewCategory(uploadedImage)
                .stub();

        final ProductCategoryDTO categoryDTO = service.createCategory(form);

        assertNotNull(categoryDTO);
        assertEquals(form.getName(), categoryDTO.getName());
        assertEquals(form.getDescription(), categoryDTO.getDescription());
        assertEquals(uploadedImage.getFilename(), categoryDTO.getImageFilename());

        verify(mockBeanProvider.getProductCategoryRepository(), times(1))
                .save(any(ProductCategory.class));
        verify(mockBeanProvider.getImageStore(), times(1))
                .assignToCategory(any(UUID.class), any(Long.class));
    }

    @Test
    public void createCategory_NullDescription_ShouldReturnNullCategoryDescription() {
        final CreateProductCategoryForm form = hpCreateProductCategoryForm()
                .description(null)
                .build();

        stubber.createCategory()
                .imageForNewCategory(uploadedImageWithId(form.getImageId()))
                .stub();

        final ProductCategoryDTO categoryDTO = service.createCategory(form);

        assertNull(categoryDTO.getDescription());
    }

    @Test
    public void createCategory_NullImageId_ShouldReturnNullCategoryImageFilename() {
        final CreateProductCategoryForm form = hpCreateProductCategoryForm()
                .imageId(null)
                .build();

        stubber.createCategory()
                .imageForNewCategory(uploadedImageWithId(form.getImageId()))
                .stub();

        final ProductCategoryDTO categoryDTO = service.createCategory(form);

        assertNull(categoryDTO.getImageFilename());
    }

    @Test
    public void updateCategory_NullCategoryId_ShouldThrowInvalidInputException() {
        final UpdateProductCategoryForm form = hpUpdateProductCategoryForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateCategory(null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("category id must not be null"))
        );
    }

    @Test
    public void updateCategory_NullForm_ShouldThrowInvalidInputException() {
        final Long categoryId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateCategory(categoryId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void updateCategory_CategoryDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long categoryId = 1L;
        final UpdateProductCategoryForm form = hpUpdateProductCategoryForm().build();

        stubber.updateCategory()
                .newImageForCategory(uploadedImageWithId(form.getNewImageId()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateCategory(categoryId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "product category", categoryId)));
    }

    @Test
    public void updateCategory_UploadedImageDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long categoryId = 1L;
        final UpdateProductCategoryForm form = hpUpdateProductCategoryForm().build();

        stubber.updateCategory()
                .categoryToUpdate(categoryWithId(categoryId))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateCategory(categoryId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "uploaded image", form.getNewImageId())));
    }

    @Test
    public void updateCategory_HappyPath_ShouldReturnUpdatedCategory() {
        final Long categoryId = 1L;
        final UpdateProductCategoryForm form = hpUpdateProductCategoryForm().build();

        final UploadedImage uploadedImage = uploadedImageWithId(form.getNewImageId());
        stubber.updateCategory()
                .categoryToUpdate(categoryWithId(categoryId))
                .newImageForCategory(uploadedImage)
                .stub();

        final ProductCategoryDTO categoryDTO = service.updateCategory(categoryId, form);

        assertNotNull(categoryDTO);
        assertEquals(form.getName(), categoryDTO.getName());
        assertEquals(form.getDescription(), categoryDTO.getDescription());
        assertEquals(uploadedImage.getFilename(), categoryDTO.getImageFilename());

        verify(mockBeanProvider.getImageStore(), times(1))
                .assignToCategory(any(UUID.class), any(Long.class));
    }

    @Test
    public void updateCategory_NullName_ShouldNotUpdateCategoryName() {
        final Long categoryId = 1L;
        final UpdateProductCategoryForm form = hpUpdateProductCategoryForm()
                .name(null)
                .build();

        stubber.updateCategory()
                .categoryToUpdate(categoryWithId(categoryId))
                .newImageForCategory(uploadedImageWithId(form.getNewImageId()))
                .stub();

        final ProductCategoryDTO categoryDTO = service.updateCategory(categoryId, form);

        assertNotNull(categoryDTO.getName());
    }

    @Test
    public void updateCategory_NullDescription_ShouldNotUpdateCategoryDescription() {
        final Long categoryId = 1L;
        final UpdateProductCategoryForm form = hpUpdateProductCategoryForm()
                .description(null)
                .build();

        stubber.updateCategory()
                .categoryToUpdate(categoryWithId(categoryId))
                .newImageForCategory(uploadedImageWithId(form.getNewImageId()))
                .stub();

        final ProductCategoryDTO categoryDTO = service.updateCategory(categoryId, form);

        assertNotNull(categoryDTO.getDescription());
    }

    @Test
    public void updateCategory_NullNewImageId_ShouldNotUpdateCategoryImageFilename() {
        final Long categoryId = 1L;
        final UpdateProductCategoryForm form = hpUpdateProductCategoryForm()
                .newImageId(null)
                .build();

        stubber.updateCategory()
                .categoryToUpdate(categoryWithId(categoryId))
                .newImageForCategory(uploadedImageWithId(form.getNewImageId()))
                .stub();

        final ProductCategoryDTO categoryDTO = service.updateCategory(categoryId, form);

        assertNotNull(categoryDTO.getImageFilename());
    }

    @Test
    public void createSubCategory_NullForm_ShouldThrowInvalidInputException() {
        final Long parentCategoryId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createSubCategory(parentCategoryId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void createSubCategory_NullParentCategoryId_ShouldThrowInvalidInputException() {
        final CreateProductCategoryForm form = hpCreateProductCategoryForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createSubCategory(null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("parent category id must not be null"))
        );
    }

    @Test
    public void createSubCategory_NullName_ShouldThrowInvalidInputException() {
        final Long parentCategoryId = 1L;
        final CreateProductCategoryForm form = hpCreateProductCategoryForm()
                .name(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createSubCategory(parentCategoryId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void createSubCategory_BlankName_ShouldThrowInvalidInputException() {
        final Long parentCategoryId = 1L;
        final CreateProductCategoryForm form = hpCreateProductCategoryForm()
                .name("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createSubCategory(parentCategoryId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void createSubCategory_BlankDescription_ShouldThrowInvalidInputException() {
        final Long parentCategoryId = 1L;
        final CreateProductCategoryForm form = hpCreateProductCategoryForm()
                .description("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createSubCategory(parentCategoryId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("description must not be blank"))
        );
    }

    @Test
    public void createSubCategory_ParentCategoryDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long parentCategoryId = 1L;
        final CreateProductCategoryForm form = hpCreateProductCategoryForm().build();

        stubber.createSubCategory()
                .imageForNewCategory(uploadedImageWithId(form.getImageId()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.createSubCategory(parentCategoryId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "product category", parentCategoryId)));
    }

    @Test
    public void createSubCategory_ImageDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long parentCategoryId = 1L;
        final CreateProductCategoryForm form = hpCreateProductCategoryForm().build();

        stubber.createSubCategory()
                .parentCategory(categoryWithId(parentCategoryId))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.createSubCategory(parentCategoryId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "uploaded image", form.getImageId())));
    }

    @Test
    public void createSubCategory_HappyPath_ShouldReturnCreatedCategory() {
        final Long parentCategoryId = 1L;
        final CreateProductCategoryForm form = hpCreateProductCategoryForm().build();

        stubber.createSubCategory()
                .parentCategory(categoryWithId(parentCategoryId))
                .imageForNewCategory(uploadedImageWithId(form.getImageId()))
                .stub();

        final ProductCategoryDTO categoryDTO = service.createSubCategory(parentCategoryId, form);

        assertNotNull(categoryDTO);
        assertEquals(form.getName(), categoryDTO.getName());
        assertEquals(form.getDescription(), categoryDTO.getDescription());

        verify(mockBeanProvider.getProductCategoryRepository(), times(1))
                .save(any(ProductCategory.class));
        verify(mockBeanProvider.getImageStore(), times(1))
                .assignToCategory(any(UUID.class), any(Long.class));
    }

    @Test
    public void createSubCategory_NullDescription_ShouldNotSaveField() {
        final Long parentCategoryId = 1L;
        final CreateProductCategoryForm form = hpCreateProductCategoryForm()
                .description(null)
                .build();

        stubber.createSubCategory()
                .parentCategory(categoryWithId(parentCategoryId))
                .imageForNewCategory(uploadedImageWithId(form.getImageId()))
                .stub();

        final ProductCategoryDTO categoryDTO = service.createSubCategory(parentCategoryId, form);

        assertNull(categoryDTO.getDescription());
    }

    @Test
    public void createSubCategory_NullImageId_ShouldReturnNullCategoryImageFilename() {
        final Long parentCategoryId = 1L;
        final CreateProductCategoryForm form = hpCreateProductCategoryForm()
                .imageId(null)
                .build();

        stubber.createSubCategory()
                .parentCategory(categoryWithId(parentCategoryId))
                .imageForNewCategory(uploadedImageWithId(form.getImageId()))
                .stub();

        final ProductCategoryDTO categoryDTO = service.createSubCategory(parentCategoryId, form);

        assertNull(categoryDTO.getImageFilename());
    }

    @Test
    public void deleteCategory_NullCategoryId_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.deleteCategory(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("category id must not be null"))
        );
    }

    @Test
    public void deleteCategory_CategoryDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long categoryId = 1L;

        stubber.deleteCategory().stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteCategory(categoryId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "product category", categoryId)));
    }

    @Test
    public void deleteCategory_HappyPath_ShouldDeleteCategory() {
        final Long categoryId = 1L;

        stubber.deleteCategory()
                .categoryToDelete(categoryWithId(categoryId))
                .stub();

        service.deleteCategory(categoryId);

        verify(mockBeanProvider.getProductCategoryRepository(), times(1))
                .delete(any(ProductCategory.class));
    }

    @Test
    public void deleteCategory_IsRootCategory_ShouldRemoveAllImages() {
        final Long categoryId = 1L;

        stubber.deleteCategory()
                .categoryToDelete(rootCategoryWithId(categoryId))
                .stub();

        service.deleteCategory(categoryId);

        verify(mockBeanProvider.getImageStore(), times(1))
                .removeCategoryImages(any(Long.class), anySet());
    }

    @Test
    public void createTag_NullForm_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createTag(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void createTag_NullName_ShouldThrowInvalidInputException() {
        final CreateProductTagForm form = hpCreateProductTagForm()
                .name(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createTag(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void createTag_BlankName_ShouldThrowInvalidInputException() {
        final CreateProductTagForm form = hpCreateProductTagForm()
                .name("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createTag(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void createTag_HappyPath_ShouldReturnCreatedTag() {
        final CreateProductTagForm form = hpCreateProductTagForm().build();

        final ProductTagDTO tagDTO = service.createTag(form);

        assertNotNull(tagDTO);
        assertEquals(form.getName(), tagDTO.getName());

        verify(mockBeanProvider.getProductTagRepository(), times(1))
                .save(any(ProductTag.class));
    }

    @Test
    public void updateTag_NullTagId_ShouldThrowInvalidInputException() {
        final UpdateProductTagForm form = hpUpdateProductTagForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateTag(null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("tag id must not be null"))
        );
    }

    @Test
    public void updateTag_NullForm_ShouldThrowInvalidInputException() {
        final Long tagId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateTag(tagId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void updateTag_BlankName_ShouldThrowInvalidInputException() {
        final Long tagId = 1L;
        final UpdateProductTagForm form = hpUpdateProductTagForm()
                .name("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateTag(tagId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("name must not be blank"))
        );
    }

    @Test
    public void updateTag_TagDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long tagId = 1L;
        final UpdateProductTagForm form = hpUpdateProductTagForm().build();

        stubber.updateTag().stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateTag(tagId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "product tag", tagId)));
    }

    @Test
    public void updateTag_HappyPath_ShouldReturnUpdatedTag() {
        final Long tagId = 1L;
        final UpdateProductTagForm form = hpUpdateProductTagForm().build();

        stubber.updateTag()
                .tagToUpdate(tagWithId(tagId))
                .stub();

        final ProductTagDTO tagDTO = service.updateTag(tagId, form);

        assertNotNull(tagDTO);
        assertEquals(form.getName(), tagDTO.getName());
    }

    @Test
    public void updateTag_NullName_ShouldNotUpdateTagName() {
        final Long tagId = 1L;
        final UpdateProductTagForm form = hpUpdateProductTagForm()
                .name(null)
                .build();

        stubber.updateTag()
                .tagToUpdate(tagWithId(tagId))
                .stub();

        final ProductTagDTO tagDTO = service.updateTag(tagId, form);

        assertNotNull(tagDTO.getName());
    }

    @Test
    public void deleteTag_NullTagId_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.deleteTag(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("tag id must not be null"))
        );
    }

    @Test
    public void deleteTag_TagDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long tagId = 1L;

        stubber.deleteTag().stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteTag(tagId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "product tag", tagId)));
    }

    @Test
    public void deleteTag_HappyPath_ShouldDeleteTagAndProductTagAssociations() {
        final Long tagId = 1L;

        stubber.deleteTag()
                .tagToDelete(tagWithId(tagId))
                .stub();

        service.deleteTag(tagId);

        verify(mockBeanProvider.getProductTagRepository(), times(1))
                .delete(any(ProductTag.class));
    }

    private static Product productWithId(final Long id) {
        return product()
                .id(id)
                .build();
    }

    private static ProductVariant variantWithIdForProduct(final Long id, final Product product) {
        return productVariant()
                .id(id)
                .product(product)
                .build();
    }

    private static ProductCategory categoryWithId(final Long id) {
        return productCategory()
                .id(id)
                .build();
    }

    private static ProductCategory rootCategoryWithId(final Long categoryId) {
        final ProductCategory category = productCategory()
                .id(categoryId)
                .nullRootCategory(true)
                .build();
        assertTrue(category.isRootCategory());
        return category;
    }

    private static ProductTag tagWithId(final Long id) {
        return productTag()
                .id(id)
                .build();
    }

    private static List<ProductTag> tagsWithIds(final Set<Long> ids) {
        return nullable(ids)
                .map(ProductServiceImplTest::tagWithId)
                .collect(Collectors.toList());
    }

    private static UploadedImage uploadedImageWithId(final UUID id) {
        return uploadedImage()
                .id(id)
                .build();
    }

    private static List<UploadedImage> uploadedImagesWithIds(final Set<UUID> ids) {
        return nullable(ids)
                .map(ProductServiceImplTest::uploadedImageWithId)
                .collect(Collectors.toList());
    }

    private static List<ProductVariantImage> variantImagesWithIdsForVariant(final Set<Long> ids, final ProductVariant variant) {
        return nullable(ids)
                .map(
                        id -> productVariantImage()
                                .id(id)
                                .variant(variant)
                                .build()
                )
                .collect(Collectors.toList());
    }

    private static UpdateProductFormBuilder hpUpdateProductForm() {
        return UpdateProductForm.builder()
                .name("Product Name")
                .newImageId(UUID.randomUUID())
                .description("Product Description.")
                .price(10L)
                .defaultVariantId(67L)
                .categoryId(75L)
                .tagsToAdd(Set.of(7L, 103L, 401L))
                .tagsToRemove(Set.of(9L, 23L));
    }

    private static CreateProductFormBuilder hpCreateProductForm() {
        return CreateProductForm.builder()
                .name("Product Name")
                .description("Product Description.")
                .imageId(UUID.randomUUID())
                .digital(false)
                .mass(1)
                .price(10L)
                .categoryId(75L)
                .tagIds(Set.of(7L, 103L));
    }

    private static CreateProductVariantFormBuilder hpCreateProductVariantForm() {
        return CreateProductVariantForm.builder()
                .name("Product Name")
                .imageIds(Set.of(UUID.randomUUID(), UUID.randomUUID()))
                .mass(1)
                .price(10L);
    }

    private static UpdateProductVariantFormBuilder hpUpdateProductVariantForm() {
        return UpdateProductVariantForm.builder()
                .name("Product Variant Name")
                .mass(1)
                .price(35L)
                .imagesToAddIds(Set.of(UUID.randomUUID(), UUID.randomUUID()))
                .imagesToRemoveIds(Set.of(734L, 98L, 8511L));
    }

    private static CreateProductCategoryFormBuilder hpCreateProductCategoryForm() {
        return CreateProductCategoryForm.builder()
                .name("Product Variant Name")
                .description("Product Category Description.")
                .imageId(UUID.randomUUID());
    }

    private static UpdateProductCategoryFormBuilder hpUpdateProductCategoryForm() {
        return UpdateProductCategoryForm.builder()
                .name("Product Variant Name")
                .description("Product Category Description.")
                .newImageId(UUID.randomUUID());
    }

    private static CreateProductTagFormBuilder hpCreateProductTagForm() {
        return CreateProductTagForm.builder()
                .name("Product Tag Name");
    }

    private static UpdateProductTagFormBuilder hpUpdateProductTagForm() {
        return UpdateProductTagForm.builder()
                .name("Product Tag Name");
    }

}
