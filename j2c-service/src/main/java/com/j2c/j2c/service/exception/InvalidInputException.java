package com.j2c.j2c.service.exception;

import lombok.Getter;

import javax.validation.ConstraintViolation;
import java.util.Set;

@Getter
public class InvalidInputException extends J2cServiceException {

    private final Set<String> errors;
    private final Set<ConstraintViolation<?>> constraintViolations;

    public InvalidInputException(final Set<String> errors, final Set<ConstraintViolation<?>> constraintViolations, final Throwable cause) {
        super(cause);
        this.errors = errors;
        this.constraintViolations = constraintViolations;
    }

}
