package com.j2c.j2c.domain.entity;

public interface Entity<ID> {

    ID getId();

    default boolean isSameAs(final Entity<ID> other) {
        if (other == null) {
            return false;
        }
        final ID id = getId();
        return id != null && id.equals(other.getId());
    }

}
