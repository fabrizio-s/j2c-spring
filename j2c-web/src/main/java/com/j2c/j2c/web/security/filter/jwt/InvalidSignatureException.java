package com.j2c.j2c.web.security.filter.jwt;

import org.springframework.security.core.AuthenticationException;

class InvalidSignatureException extends AuthenticationException {

    public InvalidSignatureException(final Throwable cause) {
        super("Invalid token signature", cause);
    }

}
