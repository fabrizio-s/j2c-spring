package com.j2c.j2c.domain.exception;

import com.j2c.j2c.domain.entity.Entity;
import lombok.Getter;
import lombok.NonNull;

@Getter
public abstract class RepositoryException extends RuntimeException {

    protected final Class<? extends Entity<?>> entityType;

    public RepositoryException(final String message, @NonNull final Class<? extends Entity<?>> entityType) {
        this(message, entityType, null);
    }

    public RepositoryException(final String message, @NonNull final Class<? extends Entity<?>> entityType, final Throwable cause) {
        super(message, cause);
        this.entityType = entityType;
    }

}
