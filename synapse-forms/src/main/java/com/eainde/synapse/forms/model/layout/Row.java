package com.eainde.synapse.forms.model.layout;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Row(
        @Valid @NotEmpty List<LayoutElement> elements
) implements LayoutElement {
    @Override
    public String getType() {
        return "Row";
    }
}
