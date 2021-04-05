package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.exception.DomainException;
import com.j2c.j2c.domain.test.MockEntity;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.domain.exception.DomainErrorMessages.VALID_USER_EMAIL_BUT_NULL_PASSWORD;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void new_NullRole_ShouldThrowIllegalArgumentException() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> User.builder()
                        .role(null)
                        .build()
        );
        assertEquals("role must not be null", exception.getMessage());
    }

    @Test
    void setEmail_Null_ShouldThrowIllegalArgumentException() {
        final User user = MockEntity.user().build();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> user.setEmail(null)
        );
        assertEquals("email must not be null", exception.getMessage());
    }

    @Test
    void setEmail_UserHasNoPassword_ShouldThrowDomainException() {
        final User user = MockEntity.user()
                .nullPassword(true)
                .build();

        final DomainException exception = assertThrows(
                DomainException.class,
                () -> user.setEmail("tester@example.com")
        );
        assertTrue(
                exception.getMessage().matches(String.format(VALID_USER_EMAIL_BUT_NULL_PASSWORD, ".+")),
                "Actual: " + exception.getMessage()
        );
    }

    @Test
    void setPassword_Null_ShouldThrowIllegalArgumentException() {
        final User user = MockEntity.user().build();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> user.setPassword(null)
        );
        assertEquals("password must not be null", exception.getMessage());
    }

    @Test
    void setRole_Null_ShouldThrowIllegalArgumentException() {
        final User user = MockEntity.user().build();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> user.setRole(null)
        );
        assertEquals("role must not be null", exception.getMessage());
    }

}
