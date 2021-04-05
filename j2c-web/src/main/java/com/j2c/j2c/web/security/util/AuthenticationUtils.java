package com.j2c.j2c.web.security.util;

import lombok.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

public final class AuthenticationUtils {

    private AuthenticationUtils() {}

    public static boolean isAnonymous(final Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    public static Long getUserId(@NonNull final Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }

}
