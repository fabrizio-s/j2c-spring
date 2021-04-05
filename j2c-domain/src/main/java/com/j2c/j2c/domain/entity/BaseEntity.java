package com.j2c.j2c.domain.entity;

import javax.persistence.Persistence;
import javax.persistence.PersistenceUtil;

abstract class BaseEntity<ID> implements Entity<ID> {

    protected boolean isLoaded(final Object obj) {
        final PersistenceUtil persistenceUtil = Persistence.getPersistenceUtil();
        return persistenceUtil.isLoaded(obj);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" +
                "id=" + getId() +
                ')';
    }

}
