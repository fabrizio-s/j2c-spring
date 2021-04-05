package com.j2c.j2c.service.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.j2c.j2c.domain.entity.MaxLengths.USER_PASSWORD_MAXLENGTH;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeUserPasswordForm {

    @NotBlank
    @Size(max = USER_PASSWORD_MAXLENGTH)
    @Schema(example = "n3w!p4ssw0rd")
    private final String password;

}
