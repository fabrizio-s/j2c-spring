package com.j2c.j2c.web.security.filter.jwt;

import org.springframework.security.core.AuthenticationException;

class JWTDecodingException extends AuthenticationException {

    public JWTDecodingException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

}
