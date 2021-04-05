package com.j2c.j2c.service.exception;

public class NoUserWithEmailExistsException extends J2cServiceException {

    public NoUserWithEmailExistsException() {
    }

    public NoUserWithEmailExistsException(final String message) {
        super(message);
    }

}
