package com.j2c.j2c.web.security.filter.jwt;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.j2c.j2c.domain.util.J2cUtils.optional;
import static com.j2c.j2c.web.util.WebConstants.AuthURI;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTVerifier jwtVerifier;
    private final JWTAuthenticationEntryPoint authenticationEntryPoint;
    private static final RequestMatcher authURLMatcher = new AntPathRequestMatcher(AuthURI);

    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain
    ) throws IOException, ServletException {
        if (requestPathMatchesAuthURL(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            verifyToken(request)
                    .ifPresent(a -> SecurityContextHolder.getContext().setAuthentication(a));
            filterChain.doFilter(request, response);
        } catch (final AuthenticationException exception) {
            commenceAuthenticationEntryPoint(request, response, exception);
        }
    }

    private Optional<JWTAuthenticationToken> verifyToken(final HttpServletRequest request) {
        final Optional<String> token = extractToken(request);
        return token.map(this::verifyToken);
    }

    private void commenceAuthenticationEntryPoint(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException exception
    ) {
        SecurityContextHolder.clearContext();
        authenticationEntryPoint.commence(request, response, exception);
    }

    private JWTAuthenticationToken verifyToken(final String token) {
        return jwtVerifier.verify(token)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Missing token"));
    }

    private static boolean requestPathMatchesAuthURL(final HttpServletRequest request) {
        return authURLMatcher.matches(request);
    }

    private static Optional<String> extractToken(final HttpServletRequest request) {
        return optional(request)
                .map(r -> r.getHeader(HttpHeaders.AUTHORIZATION));
    }

}
