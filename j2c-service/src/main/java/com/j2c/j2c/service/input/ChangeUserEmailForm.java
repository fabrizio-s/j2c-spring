package com.j2c.j2c.service.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.j2c.j2c.domain.entity.MaxLengths.EMAIL_MAXLENGTH;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeUserEmailForm {

    @Email
    @NotBlank
    @Size(max = EMAIL_MAXLENGTH)
    @Schema(example = "peter.parker@example.com")
    private final String email;

}
