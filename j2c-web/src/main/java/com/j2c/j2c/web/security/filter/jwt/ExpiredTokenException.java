package com.j2c.j2c.web.security.filter.jwt;

import org.springframework.security.core.AuthenticationException;

class ExpiredTokenException extends AuthenticationException {

    public ExpiredTokenException(final Throwable cause) {
        super("Token expired", cause);
    }

}
