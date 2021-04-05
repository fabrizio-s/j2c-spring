package com.j2c.j2c.service.input;

import com.j2c.j2c.domain.enums.RoleType;
import com.j2c.j2c.service.validation.NullOrNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Size;

import static com.j2c.j2c.domain.entity.MaxLengths.EMAIL_MAXLENGTH;
import static com.j2c.j2c.domain.entity.MaxLengths.USER_PASSWORD_MAXLENGTH;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateUserForm {

    @NullOrNotBlank
    @Size(max = EMAIL_MAXLENGTH)
    @Schema(example = "john.doe@example.com")
    private final String email;

    @NullOrNotBlank
    @Size(max = USER_PASSWORD_MAXLENGTH)
    @Schema(example = "doe!john")
    private final String password;

    private final Boolean enabled;

    private final Boolean verified;

    private final RoleType role;

}
