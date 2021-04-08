package com.j2c.j2c.web.security.token;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TokenProviderTest {

    private TokenProvider tokenProvider;

    private static final Long subject = 1L;
    private static final Set<String> authorities = singleton("TEST");
    private static final String SECRET = "rbcjXddtz1kJnfAU1dAV0vX5PfM5r0bT/Q5F6xl+eSo7FyIJixsxIQ9Nv9JySxFl2qlvbqeOCAZ0wLGynryxwQ==";

    @BeforeEach
    public void setUp() {
        tokenProvider = new TokenProvider(Keys.hmacShaKeyFor(SECRET.getBytes()));
    }

    @Test
    public void create_AllInputIsValid_ShouldReturnToken() {
        final String token = tokenProvider.create(subject, authorities);

        System.out.println("token = " + token);
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

}
