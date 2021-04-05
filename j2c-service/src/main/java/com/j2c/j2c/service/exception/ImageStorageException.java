package com.j2c.j2c.service.exception;

public class ImageStorageException extends J2cServiceException {

    public ImageStorageException() {
    }

    public ImageStorageException(final String message) {
        super(message);
    }

    public ImageStorageException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ImageStorageException(final Throwable cause) {
        super(cause);
    }

}
