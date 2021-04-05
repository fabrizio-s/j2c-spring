package com.j2c.j2c.domain.exception;

import com.j2c.j2c.domain.entity.Entity;

public class NullIdentityException extends RepositoryException {

    public NullIdentityException(final Class<? extends Entity<?>> type) {
        super("A null entity id cannot be used to perform this operation", type);
    }

}
