package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.test.MockEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductToTagAssociationTest {

    @Test
    void new_NullProduct_ShouldThrowIllegalArgumentException() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ProductToTagAssociation.builder()
                        .product(null)
                        .tag(
                                MockEntity.productTag()
                                        .build()
                        )
                        .build()
        );
        assertEquals("product must not be null", exception.getMessage());
    }

    @Test
    void new_NullTag_ShouldThrowIllegalArgumentException() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ProductToTagAssociation.builder()
                        .product(
                                MockEntity.product()
                                        .build()
                        )
                        .tag(null)
                        .build()
        );
        assertEquals("tag must not be null", exception.getMessage());
    }

}
