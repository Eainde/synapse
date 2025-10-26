package com.eainde.synapse.forms.model.fields;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * Defines the structure of items within an ArrayField (i.e., table columns).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ObjectItem(
        @Valid @NotNull Map<String, FieldDefinition> fields
) {
    public String getType() {
        return "object";
    }
}
