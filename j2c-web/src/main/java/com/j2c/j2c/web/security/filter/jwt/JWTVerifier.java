package com.j2c.j2c.web.security.filter.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.j2c.j2c.web.util.WebConstants.Bearer;
import static com.j2c.j2c.web.util.WebConstants.TokenAuthorities;

@Component
@RequiredArgsConstructor
public class JWTVerifier {

    private final Key key;

    public Optional<JWTAuthenticationToken> verify(final String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        final Claims claims = decode(stripPrefix(token));
        final JWTAuthenticationToken authentication = toAuthentication(claims);
        return Optional.of(authentication);
    }

    private Claims decode(final String token) {
        try {
            final Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return claimsJws.getBody();
        } catch (final ExpiredJwtException exception) {
            throw new ExpiredTokenException(exception);
        } catch (final MalformedJwtException exception) {
            throw new MalformedTokenException(exception);
        } catch (final SignatureException exception) {
            throw new InvalidSignatureException(exception);
        } catch (final JwtException exception) {
            throw new JWTDecodingException(exception.getMessage(), exception);
        }
    }

    private static String stripPrefix(final String token) {
        if (!token.startsWith(Bearer)) {
            throw new InvalidTokenException("Expected token prefix value of '" + Bearer.trim() + "'");
        }
        final String cleanToken = token.replaceFirst(Bearer, "");
        if (tokenContainsLeadingOrTrailingSpaces(cleanToken)) {
            throw new InvalidTokenException("Unexpected leading/trailing blank spaces during JWT verification");
        }
        return cleanToken;
    }

    private static boolean tokenContainsLeadingOrTrailingSpaces(final String token) {
        return token.length() != token.trim().length();
    }

    private static JWTAuthenticationToken toAuthentication(final Claims claims) {
        final Long principal = Long.valueOf(claims.getSubject());
        final Set<? extends GrantedAuthority> authorities = getAuthorities( claims );
        return new JWTAuthenticationToken(principal, authorities);
    }

    @SuppressWarnings("unchecked")
    private static Set<? extends GrantedAuthority> getAuthorities(final Claims claims) {
        final Collection<String> authorities = claims.get(TokenAuthorities, Collection.class);
        return authorities.stream()
                .filter(Objects::nonNull)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

}
