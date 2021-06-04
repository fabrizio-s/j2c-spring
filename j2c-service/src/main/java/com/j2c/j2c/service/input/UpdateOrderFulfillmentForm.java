package com.j2c.j2c.service.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateOrderFulfillmentForm {

    @Schema(description = "List of lines to add to the fulfillment")
    private final List<@NotNull @Valid Line> linesToAdd;

    @Schema(description = "List of lines with the new quantities")
    private final List<@NotNull @Valid Line> linesToUpdate;

    @Schema(description = "List of line ids to delete")
    private final Set<@NotNull Long> lineIdsToDelete;

}
