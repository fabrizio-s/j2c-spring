package com.j2c.j2c.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Hidden
@RestController
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class RestErrorController extends AbstractErrorController {

    private final ObjectMapper objectMapper;

    public RestErrorController(
            final ErrorAttributes errorAttributes,
            final ObjectMapper objectMapper
    ) {
        super(errorAttributes);
        this.objectMapper = objectMapper;
    }

    @RequestMapping
    public ResponseEntity<Map<String, Object>> error(final HttpServletRequest request) {
        // handles any error not handled in GlobalExceptionHandler
        final HttpStatus status = getStatus(request);
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity<>(status);
        }
        if (status.value() >= 500) {
            final Map<String, Object> body = getErrorAttributes(request);
            final String message = mapToString(body);
            log.error(message);
        }
        return new ResponseEntity<>(
                errorResponse(status),
                status
        );
    }

    @Override
    public String getErrorPath() {
        return null;
    }

    private Map<String, Object> getErrorAttributes(final HttpServletRequest request) {
        return getErrorAttributes(
                request,
                ErrorAttributeOptions.of(
                        ErrorAttributeOptions.Include.EXCEPTION,
                        ErrorAttributeOptions.Include.STACK_TRACE,
                        ErrorAttributeOptions.Include.MESSAGE,
                        ErrorAttributeOptions.Include.BINDING_ERRORS
                )
        );
    }

    private String mapToString(final Map<?, ?> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (final JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Map<String, Object> errorResponse(final HttpStatus status) {
        final Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        return response;
    }

}
