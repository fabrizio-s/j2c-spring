package com.j2c.j2c.service.exception;

public abstract class J2cServiceException extends RuntimeException {

    public J2cServiceException() {
    }

    public J2cServiceException(final String message) {
        super(message);
    }

    public J2cServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public J2cServiceException(final Throwable cause) {
        super(cause);
    }

}
