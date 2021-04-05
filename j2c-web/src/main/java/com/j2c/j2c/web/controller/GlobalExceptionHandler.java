package com.j2c.j2c.web.controller;

import com.j2c.j2c.service.exception.*;
import com.j2c.j2c.web.output.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(responseCode = "400", ref = "400")
    public ErrorResponse handle(final InvalidInputException exception) {
        final String message = String.join(",", exception.getErrors());
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(message)
                .build();
    }

    @Hidden
    @ExceptionHandler({
            UnsatisfiedServletRequestParameterException.class,
            PropertyReferenceException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle5xx(final RuntimeException exception) {
        // UnsatisfiedServletRequestParameterException happens when a required 'action' parameter is not provided
        // PropertyReferenceException happens when an unknown property is used as sort parameter for Pageable
        final Throwable rootCause = getRootCause(exception);
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(rootCause.getMessage())
                .build();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(responseCode = "404", ref = "404")
    public ErrorResponse handle(final ResourceNotFoundException exception) {
        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler({ServiceException.class, ImageStorageException.class, GatewayException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ApiResponse(responseCode = "422", ref = "422")
    public ErrorResponse handle(final RuntimeException exception) {
        return ErrorResponse.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ApiResponse(responseCode = "409", ref = "409")
    public ErrorResponse handle(final ResourceAlreadyExistsException exception) {
        return ErrorResponse.builder()
                .status(HttpStatus.CONFLICT)
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(AuthenticationException.class)
    @ApiResponse(responseCode = "401", ref = "401")
    public void handle(final AuthenticationException exception) {
        // workaround to include 401 response in swagger-ui for all endpoints
    }

    @ExceptionHandler(AuthorizationException.class)
    @ApiResponse(responseCode = "403", ref = "403")
    public void handle(final AuthorizationException exception) {
        // workaround to include 403 response in swagger-ui for all endpoints
    }

    @Override
    protected @NonNull ResponseEntity<Object> handleExceptionInternal(
            final @NonNull Exception exception,
            Object body,
            final @NonNull HttpHeaders headers,
            final @NonNull HttpStatus status,
            final @NonNull WebRequest request
    ) {
        if (body == null) {
            body = ErrorResponse.builder()
                    .status(status)
                    .build();
        }
        return super.handleExceptionInternal(exception, body, headers, status, request);
    }

    private static Throwable getRootCause(Throwable throwable) {
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        return throwable;
    }

    private static class AuthenticationException extends RuntimeException {}
    private static class AuthorizationException extends RuntimeException {}

}
