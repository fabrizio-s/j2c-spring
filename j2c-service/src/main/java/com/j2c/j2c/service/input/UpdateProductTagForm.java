package com.j2c.j2c.service.input;

import com.j2c.j2c.service.validation.NullOrNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Size;

import static com.j2c.j2c.domain.entity.MaxLengths.PRODUCTCATEGORY_NAME_MAXLENGTH;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateProductTagForm {

    @NullOrNotBlank
    @Size(max = PRODUCTCATEGORY_NAME_MAXLENGTH)
    @Schema(example = "Pets")
    private final String name;

}
