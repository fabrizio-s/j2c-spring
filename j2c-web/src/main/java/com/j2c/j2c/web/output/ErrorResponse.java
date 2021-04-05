package com.j2c.j2c.web.output;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
public class ErrorResponse {

    private final Date timestamp = new Date();

    private final Integer status;

    private final String error;

    private final String message;

    @Builder
    private ErrorResponse(
            final HttpStatus status,
            final String message
    ) {
        if (status != null) {
            this.status = status.value();
            this.error = status.getReasonPhrase();
        } else {
            this.status = null;
            this.error = null;
        }
        this.message = message;
    }

}
