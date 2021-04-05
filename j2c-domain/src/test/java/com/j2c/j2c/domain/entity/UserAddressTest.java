package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.test.MockEntity;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.domain.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;

class UserAddressTest {

    @Test
    void new_NullUser_ShouldThrowIllegalArgumentException() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> UserAddress.builder()
                        .user(null)
                        .address(nextObject(Address.class))
                        .build()
        );
        assertEquals("user must not be null", exception.getMessage());
    }

    @Test
    void new_NullAddress_ShouldThrowIllegalArgumentException() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> UserAddress.builder()
                        .user(
                                MockEntity.user()
                                        .build()
                        )
                        .address(null)
                        .build()
        );
        assertEquals("address must not be null", exception.getMessage());
    }

    @Test
    void setAddress() {
        final UserAddress userAddress = MockEntity.userAddress().build();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userAddress.setAddress(null)
        );
        assertEquals("address must not be null", exception.getMessage());
    }

}
