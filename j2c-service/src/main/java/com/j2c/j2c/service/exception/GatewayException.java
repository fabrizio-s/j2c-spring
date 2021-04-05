package com.j2c.j2c.service.exception;

public class GatewayException extends RuntimeException {

    public GatewayException() {
    }

    public GatewayException(final String message) {
        super(message);
    }

    public GatewayException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public GatewayException(final Throwable cause) {
        super(cause);
    }

}
