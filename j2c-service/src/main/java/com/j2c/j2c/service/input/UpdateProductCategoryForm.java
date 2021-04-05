package com.j2c.j2c.service.input;

import com.j2c.j2c.service.validation.NullOrNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Size;
import java.util.UUID;

import static com.j2c.j2c.domain.entity.MaxLengths.PRODUCTCATEGORY_DESCRIPTION_MAXLENGTH;
import static com.j2c.j2c.domain.entity.MaxLengths.PRODUCTCATEGORY_NAME_MAXLENGTH;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateProductCategoryForm {

    @NullOrNotBlank
    @Size(max = PRODUCTCATEGORY_NAME_MAXLENGTH)
    @Schema(example = "Electronics")
    private final String name;

    @NullOrNotBlank
    @Size(max = PRODUCTCATEGORY_DESCRIPTION_MAXLENGTH)
    @Schema(example = "Electronic products.")
    private final String description;

    @Schema(description = "The id returned after successfully uploading an image")
    private final UUID newImageId;

}
