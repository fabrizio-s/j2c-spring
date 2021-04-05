package com.j2c.j2c.web.security.filter.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JWTAccessDeniedHandler extends BaseJWTErrorHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AccessDeniedException exception
    ) {
        handle(response, HttpStatus.FORBIDDEN, parameters());
    }

    private Map<String, String> parameters() {
        final Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("realm", "j2c");
        parameters.put("error", "insufficient_scope");
        parameters.put("error_description", "The request requires higher privileges than provided by the access token.");
        parameters.put("error_uri", "https://tools.ietf.org/html/rfc6750#section-3.1");
        return parameters;
    }

}
