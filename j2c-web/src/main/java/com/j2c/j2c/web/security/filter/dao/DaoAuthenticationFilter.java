package com.j2c.j2c.web.security.filter.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j2c.j2c.web.input.AuthRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static com.j2c.j2c.web.util.WebConstants.AuthURI;

@Component
public class DaoAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final Validator validator;
    private final AuthenticationFailureHandler failureHandler;
    private final ObjectMapper objectMapper;

    public DaoAuthenticationFilter(
            final AuthenticationManager authenticationManager,
            final DaoAuthenticationFailureHandler failureHandler,
            final Validator validator,
            final ObjectMapper objectMapper
    ) {
        super(AuthURI);
        setAuthenticationManager(authenticationManager);
        this.failureHandler = failureHandler;
        this.validator = validator;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(
            final HttpServletRequest httpRequest,
            final HttpServletResponse httpResponse
    ) throws AuthenticationException {
        final AuthRequest request = extractAuthRequest(httpRequest);
        validate(request);
        return authenticate(request);
    }

    @Override
    protected void successfulAuthentication(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain,
            final Authentication authentication
    ) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException failed
    ) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        delegateToFailureHandler(request, response, failed);
    }

    private Authentication authenticate(final AuthRequest request) {
        final Authentication authentication = toAuthentication(request);
        return getAuthenticationManager().authenticate(authentication);
    }

    private void validate(final AuthRequest request) {
        final Set<ConstraintViolation<AuthRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            final String reasons = violations.stream()
                    .map(violation -> String.format("%s %s", violation.getPropertyPath().toString(), violation.getMessage()))
                    .collect(Collectors.joining(","));
            throw new InvalidAuthenticationRequestException(String.format("Invalid request: %s", reasons));
        }
    }

    private void delegateToFailureHandler(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException exception
    ) throws IOException, ServletException {
        this.failureHandler.onAuthenticationFailure(request, response, exception);
    }

    private AuthRequest extractAuthRequest(final HttpServletRequest request) {
        if (httpRequestIsNotValid(request)) {
            throw new AuthenticationServiceException("Invalid authentication request");
        }
        final String payload = payload(request);
        return toAuthRequest(payload);
    }

    private AuthRequest toAuthRequest(final String payload) {
        try {
            return objectMapper.readValue(payload, AuthRequest.class);
        } catch (final JsonProcessingException exception) {
            throw new InvalidAuthenticationRequestException("Request must contain only 'email' and 'password' fields", exception);
        }
    }

    private static Authentication toAuthentication(final AuthRequest request) {
        final String email = request.getEmail();
        final String password = request.getPassword();
        return new UsernamePasswordAuthenticationToken(email, password);
    }

    private static String payload(final HttpServletRequest request) {
        try {
            return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (final IOException exception) {
            throw new AuthenticationServiceException("I/O exception while reading authentication request body", exception);
        }
    }

    private static boolean httpRequestIsNotValid(final HttpServletRequest request) {
        return !request.getMethod().equalsIgnoreCase("POST");
    }

}
