package com.j2c.j2c.service.exception;

public class ResourceAlreadyExistsException extends J2cServiceException {

    public ResourceAlreadyExistsException() {
    }

    public ResourceAlreadyExistsException(final String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ResourceAlreadyExistsException(final Throwable cause) {
        super(cause);
    }

}
