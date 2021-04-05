package com.j2c.j2c.web.security.filter.jwt;

import org.springframework.security.core.AuthenticationException;

class InvalidTokenException extends AuthenticationException {

    public InvalidTokenException(final String message) {
        super(message);
    }

}
