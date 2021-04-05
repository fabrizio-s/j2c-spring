package com.j2c.j2c.web.security.filter.jwt;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.j2c.j2c.web.util.WebConstants.Bearer;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JWTVerifierTest {

    private JWTVerifier jwtVerifier;
    private static final String SECRET = "rbcjXddtz1kJnfAU1dAV0vX5PfM5r0bT/Q5F6xl+eSo7FyIJixsxIQ9Nv9JySxFl2qlvbqeOCAZ0wLGynryxwQ==";
    private static final String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aG9yaXRpZXMiOlsiVEVTVCJdfQ.qSVQ1nAqmO5hZg-LF0nvHbkZ4jwk4roXP8w3eOhZbI-6RZJVcePyoq3ZFYiJPshGdpYMfIOL8gJwzkgWZUaPiQ";

    @BeforeEach
    public void setUp() {
        jwtVerifier = new JWTVerifier(Keys.hmacShaKeyFor(SECRET.getBytes()));
    }

    @Test
    public void verify_TokenWithPrefix_ShouldReturnNonEmptyOptional() {
        final Optional<JWTAuthenticationToken> optional = jwtVerifier.verify(Bearer + token);

        assertTrue(optional.isPresent());
    }

    @Test
    public void verify_NullToken_ShouldReturnEmptyOptional() {
        final Optional<JWTAuthenticationToken> optional = jwtVerifier.verify(null);

        assertTrue(optional.isEmpty());
    }

    @Test
    public void verify_EmptyToken_ShouldReturnEmptyOptional() {
        final Optional<JWTAuthenticationToken> optional = jwtVerifier.verify("");

        assertTrue(optional.isEmpty());
    }

    @Test
    public void verify_BlankToken_ShouldReturnEmptyOptional() {
        final Optional<JWTAuthenticationToken> optional = jwtVerifier.verify("   ");

        assertTrue(optional.isEmpty());
    }

    @Test
    public void verify_TokenWithoutPrefix_ShouldThrowMissingTokenPrefixException() {
        assertThrows(
                InvalidTokenException.class,
                () -> jwtVerifier.verify(token)
        );
    }

    @Test
    public void verify_TokenContainsLeadingSpaces_ShouldThrowInvalidTokenException() {
        final Throwable throwable = assertThrows(
                InvalidTokenException.class,
                () -> jwtVerifier.verify(Bearer + "  " + token)
        );
        assertTrue(throwable.getMessage().startsWith("Unexpected leading/trailing blank spaces"));
    }

    @Test
    public void verify_TokenContainsTrailingSpaces_ShouldThrowInvalidTokenException() {
        final Throwable throwable = assertThrows(
                InvalidTokenException.class,
                () -> jwtVerifier.verify(Bearer + token + "  ")
        );
        assertTrue(throwable.getMessage().startsWith("Unexpected leading/trailing blank spaces"));
    }

    @Test
    public void verify_TokenWithMultiplePrefixes_ShouldThrowJWTDecodingException() {
        assertThrows(
                JWTDecodingException.class,
                () -> jwtVerifier.verify(Bearer + Bearer + Bearer + token)
        );
    }

    @Test
    public void verify_InvalidToken_ShouldThrowMalformedTokenException() {
        assertThrows(
                MalformedTokenException.class,
                () -> jwtVerifier.verify(Bearer + "iNvAlIdToKeN")
        );
    }

}
