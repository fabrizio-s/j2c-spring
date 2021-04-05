package com.j2c.j2c.domain.exception;

import com.j2c.j2c.domain.entity.Entity;
import lombok.Getter;

@Getter
public class EntityDoesNotExistException extends RepositoryException {

    private final Class<? extends Entity<?>> entityType;
    private final Object id;
    private final String customErrorMessage;

    public EntityDoesNotExistException(final Class<? extends Entity<?>> entityType, final Object id) {
        this(entityType, id, null);
    }

    public EntityDoesNotExistException(final Class<? extends Entity<?>> entityType, final Object id, final Throwable cause) {
        super(String.format("%s with id '%s' does not exist", entityType, id), entityType);
        this.entityType = entityType;
        this.id = id;
        this.customErrorMessage = null;
    }

    public EntityDoesNotExistException(final String errorMessage, final Class<? extends Entity<?>> entityType) {
        super(errorMessage, entityType);
        this.customErrorMessage = errorMessage;
        this.entityType = entityType;
        this.id = null;
    }

}
