package com.j2c.j2c.web.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthRequest {

    @Email
    @NotBlank
    @Schema(example = "admin@j2c.com")
    private final String email;

    @NotBlank
    @Schema(example = "l337p4ssw0rd1234")
    private final String password;

}
