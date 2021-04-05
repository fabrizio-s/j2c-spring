package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.test.MockEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTagTest {

    @Test
    void new_NullName_ShouldThrowIllegalArgumentException() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ProductTag.builder()
                        .name(null)
                        .build()
        );
        assertEquals("name must not be null", exception.getMessage());
    }

    @Test
    void setName_Null_ShouldThrowIllegalArgumentException() {
        final ProductTag tag = MockEntity.productTag().build();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tag.setName(null)
        );
        assertEquals("name must not be null", exception.getMessage());
    }

}
