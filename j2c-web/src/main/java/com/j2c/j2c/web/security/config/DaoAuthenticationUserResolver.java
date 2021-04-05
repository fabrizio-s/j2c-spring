package com.j2c.j2c.web.security.config;

import com.j2c.j2c.service.application.UserService;
import com.j2c.j2c.service.dto.UserDTO;
import com.j2c.j2c.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DaoAuthenticationUserResolver implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(final String email) {
        final UserDTO user = findUser(email);
        return toUserDetails(user);
    }

    private UserDTO findUser(final String email) {
        try {
            return userService.findWithAuthenticationDetails(email);
        } catch (final ResourceNotFoundException exception) {
            throw new UsernameNotFoundException(String.format("User with email '%s' not found", email), exception);
        }
    }

    private User toUserDetails(final UserDTO user) {
        final Set<? extends GrantedAuthority> authorities = user.getRole().getAuthorities().stream()
                .filter(Objects::nonNull)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        return new User(user.getId().toString(), user.getPassword(), authorities);
    }

}
