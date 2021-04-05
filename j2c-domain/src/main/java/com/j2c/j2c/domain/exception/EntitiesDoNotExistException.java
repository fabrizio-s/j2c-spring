package com.j2c.j2c.domain.exception;

import com.j2c.j2c.domain.entity.Entity;
import lombok.Getter;

import java.util.Set;

@Getter
public class EntitiesDoNotExistException extends RepositoryException {

    private final Class<? extends Entity<?>> entityType;
    private final Set<Object> ids;

    public EntitiesDoNotExistException(final Class<? extends Entity<?>> entityType, final Set<Object> ids) {
        super(String.format("%s with ids '%s' do not exist", entityType, ids), entityType);
        this.entityType = entityType;
        this.ids = ids;
    }

}
