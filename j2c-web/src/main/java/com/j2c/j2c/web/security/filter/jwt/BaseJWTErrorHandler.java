package com.j2c.j2c.web.security.filter.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;

import static com.j2c.j2c.web.util.WebConstants.Bearer;

abstract class BaseJWTErrorHandler {

    protected void handle(
            final HttpServletResponse response,
            final HttpStatus status,
            final Map<String, String> parameters
    ) {
        final String wwwAuthenticate = computeWWWAuthenticateHeaderValue(parameters);
        setResponseHeaders(response, status, wwwAuthenticate);
    }

    private String computeWWWAuthenticateHeaderValue(final Map<String, String> parameters) {
        final StringBuilder wwwAuthenticate = new StringBuilder();
        final Iterator<Map.Entry<String, String>> iterator = parameters.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, String> entry = iterator.next();
            wwwAuthenticate.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
            if (iterator.hasNext()) {
                wwwAuthenticate.append(", ");
            }
        }
        return wwwAuthenticate.toString();
    }

    private void setResponseHeaders(final HttpServletResponse response, final HttpStatus status, final String wwwAuthenticate) {
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, Bearer + wwwAuthenticate);
        response.setStatus(status.value());
    }

}
