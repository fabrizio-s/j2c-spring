package com.j2c.j2c.web.security.filter.jwt;

import org.springframework.security.core.AuthenticationException;

class InvalidAuthorizationHeaderException extends AuthenticationException {

    public InvalidAuthorizationHeaderException(final Throwable cause) {
        super("Invalid http Authorization header", cause);
    }

}
