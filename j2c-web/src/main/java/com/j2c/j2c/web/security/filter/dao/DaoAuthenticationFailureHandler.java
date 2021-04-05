package com.j2c.j2c.web.security.filter.dao;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class DaoAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException exception
    ) throws IOException {
        final HttpStatus status = translate(exception);
        response.sendError(status.value(), exception.getMessage());
    }

    private HttpStatus translate(final AuthenticationException exception) {
        if (InvalidAuthenticationRequestException.class.equals(exception.getClass())) {
            return HttpStatus.BAD_REQUEST;
        } else {
            return HttpStatus.UNAUTHORIZED;
        }
    }

}
