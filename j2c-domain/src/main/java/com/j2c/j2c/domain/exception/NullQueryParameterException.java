package com.j2c.j2c.domain.exception;

import com.j2c.j2c.domain.entity.Entity;

public class NullQueryParameterException extends RepositoryException {

    public NullQueryParameterException(final String message, final Class<? extends Entity<?>> type) {
        super(message, type);
    }

}
