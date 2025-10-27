package com.eainde.synapse.forms.domain.fields;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Represents an array/table field.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArrayField(
        @NotBlank String widget,
        @NotBlank String labelKey,
        @Valid @NotNull(message = "ArrayField 'items' must not be null") ObjectItem items,
        @Valid ValidationRules validation,
        List<String> permissions
) implements FieldDefinition {

    @Override
    public String getType() {
        return "array"; // Type is constant for ArrayField
    }

    @Override
    public String getWidget() { return widget; }
    @Override
    public String getLabelKey() { return labelKey; }
    @Override
    public ValidationRules getValidation() { return validation; }
    @Override
    public List<String> getPermissions() { return permissions; }

    // Builder for convenience
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String widget;
        private String labelKey;
        private ObjectItem items;
        private ValidationRules validation;
        private List<String> permissions;

        public Builder widget(String widget) { this.widget = widget; return this; }
        public Builder labelKey(String labelKey) { this.labelKey = labelKey; return this; }
        public Builder items(ObjectItem items) { this.items = items; return this; }
        public Builder validation(ValidationRules validation) { this.validation = validation; return this; }
        public Builder permissions(List<String> permissions) { this.permissions = permissions; return this; }

        public ArrayField build() {
            return new ArrayField(widget, labelKey, items, validation, permissions);
        }
    }
}
