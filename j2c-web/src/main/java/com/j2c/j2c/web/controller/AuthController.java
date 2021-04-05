package com.j2c.j2c.web.controller;

import com.j2c.j2c.domain.enums.RoleType;
import com.j2c.j2c.service.application.UserService;
import com.j2c.j2c.service.dto.UserDTO;
import com.j2c.j2c.service.input.CreateUserForm;
import com.j2c.j2c.web.input.AuthRequest;
import com.j2c.j2c.web.security.token.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static com.j2c.j2c.web.security.util.AuthenticationUtils.getUserId;
import static com.j2c.j2c.web.security.util.AuthenticationUtils.isAnonymous;
import static com.j2c.j2c.web.util.WebConstants.AuthURI;
import static com.j2c.j2c.web.util.WebConstants.Bearer;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints related to system authentication")
public class AuthController {

    private final UserService userService;
    private final TokenProvider tokenProvider;

    @PostMapping(value = AuthURI,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Submit user credentials to generate a token which can be used to access secured endpoints")
    @RequestBody(description = "Credentials of the user for whom to generate a token",
            required = true,
            content = @Content(schema = @Schema(implementation = AuthRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "204",
                    description = "Successfully authenticated and generated token for the user with the supplied credentials",
                    headers = @Header(name = HttpHeaders.AUTHORIZATION,
                            description = "Contains the token in the format 'Bearer \\<token\\>'",
                            schema = @Schema(example = "Bearer eyexJ9.eyJzdWIiOURfQNDVNIl19.PQkJ8vQ"))),
            @ApiResponse(responseCode = "400",
                    description = "When an empty request body is supplied, either email or password is misspelled or not provided, or email is invalid"),
            @ApiResponse(responseCode = "401",
                    description = "When either the user does not exist or the wrong password is supplied")
    })
    public void authenticate(final HttpServletResponse response) {
        setAuthorization(response);
    }

    @PostMapping(value = "/api/refresh")
    @PreAuthorize("authenticated")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Generate a new token by supplying an existing one",
            security = @SecurityRequirement(name = "JWT"))
    @ApiResponse(responseCode = "204",
            description = "Successfully generated a new token",
            headers = @Header(name = HttpHeaders.AUTHORIZATION,
                    description = "Contains the token in the format 'Bearer \\<token\\>'",
                    schema = @Schema(example = "Bearer eyexJ9.eyJzdWIiOURfQNDVNIl19.PQkJ8vQ")))
    public void refresh(final HttpServletResponse response) {
        setAuthorization(response);
    }

    @PostMapping(value = "/api/anonymous",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new unprivileged anonymous user")
    @ApiResponse(responseCode = "200",
            headers = @Header(name = HttpHeaders.AUTHORIZATION,
                    description = "Contains the token in the format 'Bearer \\<token\\>'",
                    schema = @Schema(example = "Bearer eyexJ9.eyJzdWIiOURfQNDVNIl19.PQkJ8vQ")))
    public UserDTO anonymous(final HttpServletResponse response) {
        final UserDTO anonymousCustomer = userService.create(
                CreateUserForm.builder()
                        .role(RoleType.Customer)
                        .build()
        );
        setAuthorization(response, anonymousCustomer.getId(), Collections.emptySet());
        return anonymousCustomer;
    }

    private void setAuthorization(final HttpServletResponse response) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAnonymous(authentication)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot provide token for anonymous user");
        }
        setAuthorization(response, getUserId(authentication), getAuthorities(authentication));
    }

    private void setAuthorization(
            final HttpServletResponse response,
            final Long userId,
            final Set<String> authorities
    ) {
        final String token = tokenProvider.create(userId, authorities);
        response.setHeader(HttpHeaders.AUTHORIZATION, Bearer + token);
    }

    private static Set<String> getAuthorities(final Authentication authentication) {
        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null) {
            return Collections.emptySet();
        }
        return authorities.stream()
                .filter(Objects::nonNull)
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

}
