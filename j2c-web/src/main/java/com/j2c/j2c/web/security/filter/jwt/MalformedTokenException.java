package com.j2c.j2c.web.security.filter.jwt;

import org.springframework.security.core.AuthenticationException;

class MalformedTokenException extends AuthenticationException {

    public MalformedTokenException(final Throwable cause) {
        super("Malformed token", cause);
    }

}
