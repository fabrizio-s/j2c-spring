package com.j2c.j2c.domain.exception;

import com.j2c.j2c.domain.entity.Entity;
import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

    private final Entity<?> entity;

    public DomainException(final String message, final Entity<?> entity) {
        super(message);
        this.entity = entity;
    }

}
