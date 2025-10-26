package com.eainde.synapse.forms.model.layout;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FieldRef(
        @NotBlank(message = "Field reference key cannot be blank")
        String key
) implements LayoutElement {
    @Override
    public String getType() {
        return "Field";
    }
}
