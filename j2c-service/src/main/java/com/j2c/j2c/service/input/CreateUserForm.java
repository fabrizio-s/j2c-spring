package com.j2c.j2c.service.input;

import com.j2c.j2c.domain.enums.RoleType;
import com.j2c.j2c.service.validation.NullOrNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.j2c.j2c.domain.entity.MaxLengths.EMAIL_MAXLENGTH;
import static com.j2c.j2c.domain.entity.MaxLengths.USER_PASSWORD_MAXLENGTH;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateUserForm {

    @Email
    @NullOrNotBlank
    @Size(max = EMAIL_MAXLENGTH)
    @Schema(example = "mary.gold@example.com")
    private final String email;

    @NullOrNotBlank
    @Size(max = USER_PASSWORD_MAXLENGTH)
    @Schema(example = "maryssupersecretpassword")
    private final String password;

    @Schema(example = "true")
    private final boolean enabled;

    @Schema(example = "true")
    private final boolean verified;

    @NotNull
    private final RoleType role;

}
