package com.j2c.j2c.web.security.filter.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JWTAuthenticationEntryPoint extends BaseJWTErrorHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException exception
    ) {
        handle(response, HttpStatus.UNAUTHORIZED, parameters(exception));
    }

    private Map<String, String> parameters(final AuthenticationException exception) {
        final Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("realm", "j2c");
        parameters.put("error", "invalid_token");
        parameters.put("error_description", exception.getMessage() + ".");
        return parameters;
    }

}
