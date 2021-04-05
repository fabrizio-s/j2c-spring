package com.j2c.j2c.service.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

import static com.j2c.j2c.domain.entity.MaxLengths.EMAIL_MAXLENGTH;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCheckoutForm {

    @Email
    @NotBlank
    @Size(max = EMAIL_MAXLENGTH)
    @Schema(example = "jennifer.heisenberg@example.com")
    private final String email;

    @NotEmpty
    @Schema(description = "A line id here represents a product variant id, " +
            "and the quantity is how many of those variants the customer wishes to purchase")
    private final List<@NotNull @Valid Line> lines;

}
