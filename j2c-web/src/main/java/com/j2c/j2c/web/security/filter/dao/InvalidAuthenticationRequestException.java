package com.j2c.j2c.web.security.filter.dao;

import org.springframework.security.core.AuthenticationException;

class InvalidAuthenticationRequestException extends AuthenticationException {

    public InvalidAuthenticationRequestException(final String msg) {
        super(msg);
    }

    public InvalidAuthenticationRequestException(final String msg, final Throwable t) {
        super(msg, t);
    }

}
