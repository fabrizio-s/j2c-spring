package com.j2c.j2c.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UploadedImageTest {

    @Test
    void new_NullFilename_ShouldThrowIllegalArgumentException() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> UploadedImage.builder()
                        .filename(null)
                        .build()
        );
        assertEquals("filename must not be null", exception.getMessage());
    }

}
