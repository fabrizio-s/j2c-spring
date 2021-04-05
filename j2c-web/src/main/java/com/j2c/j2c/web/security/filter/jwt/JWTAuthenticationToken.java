package com.j2c.j2c.web.security.filter.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

class JWTAuthenticationToken extends AbstractAuthenticationToken {

    private final Long principal;

    public JWTAuthenticationToken(
            final Long principal,
            final Collection<? extends GrantedAuthority> authorities
    ) {
        super(authorities);
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Long getPrincipal() {
        return principal;
    }

}
