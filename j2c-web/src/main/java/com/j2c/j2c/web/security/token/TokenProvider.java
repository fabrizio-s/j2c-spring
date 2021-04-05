package com.j2c.j2c.web.security.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.j2c.j2c.domain.util.J2cUtils.optional;
import static com.j2c.j2c.web.util.WebConstants.TokenAuthorities;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final Key key;
    private static final SignatureAlgorithm ALGORITHM = SignatureAlgorithm.HS512;
    private static final int DURATION = 30; // minutes

    public String create(@NonNull final Long id, Set<String> authorities) {
        authorities = optional(authorities).stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return Jwts.builder()
                .signWith(key, ALGORITHM)
                .setHeaderParam("typ", "JWT")
                .setSubject(id.toString())
                .setExpiration(calculateExpirationDate())
                .claim(TokenAuthorities, authorities)
                .compact();
    }

    private static Date calculateExpirationDate() {
        final LocalDateTime xMinutesFromNow = LocalDateTime.now().plusMinutes(DURATION);
        return Date.from(
                xMinutesFromNow.atZone(ZoneId.systemDefault())
                        .toInstant()
        );
    }

}
