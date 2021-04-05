package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.test.MockEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductCategoryTest {

    @Test
    void new_NullName_ShouldThrowIllegalArgumentException() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ProductCategory.builder()
                        .name(null)
                        .description("qwe")
                        .imageFilename("awdawd")
                        .build()
        );
        assertEquals("name must not be null", exception.getMessage());
    }

    @Test
    void isRootCategory_NullRoot_ShouldReturnTrue() {
        final ProductCategory category = MockEntity.productCategory()
                .nullRoot(true)
                .build();

        assertTrue(category.isRootCategory());
    }

    @Test
    void isRootCategory_RootIsNotNull_ShouldReturnFalse() {
        final ProductCategory category = MockEntity.productCategory().build();

        assertFalse(category.isRootCategory());
    }

    @Test
    void getRootCategoryId_IsRootCategory_ShouldReturnId() {
        final ProductCategory category = MockEntity.productCategory()
                .nullRoot(true)
                .build();

        assertEquals(category.getId(), category.getRootCategoryId());
    }

    @Test
    void getRootCategoryId_IsNotRootCategory_ShouldReturnRootCategoryId() {
        final ProductCategory category = MockEntity.productCategory().build();

        assertEquals(category.getRoot().getId(), category.getRootCategoryId());
    }

    @Test
    void newSubCategory_HappyPath_ShouldIncreaseRightAndSubCategoryLeftAndRight() {
        final ProductCategory category = MockEntity.productCategory().build();

        final int right = category.getRight();

        final ProductCategory subCategory= category.newSubCategory()
                .name("daw")
                .add();

        assertEquals(category.getRootCategoryId(), subCategory.getRootCategoryId());
        assertEquals(right, subCategory.getLeft());
        assertEquals(right + 1, subCategory.getRight());
        assertEquals(right + 2, category.getRight());
    }

    @Test
    void setName_Null_ShouldThrowIllegalArgumentException() {
        final ProductCategory category = MockEntity.productCategory().build();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> category.setName(null)
        );
        assertEquals("name must not be null", exception.getMessage());
    }

}
