package com.eainde.synapse.forms.model.fields;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * Represents simple field types like string, number, and boolean.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SimpleField(
        @NotBlank String type,
        @NotBlank String widget,
        @NotBlank String labelKey,
        String optionsKey,
        @Valid ValidationRules validation,
        List<String> permissions
) implements FieldDefinition {

    @Override
    public String getType() { return type; }
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
        private String type;
        private String widget;
        private String labelKey;
        private String optionsKey;
        private ValidationRules validation;
        private List<String> permissions;

        public Builder type(String type) { this.type = type; return this; }
        public Builder widget(String widget) { this.widget = widget; return this; }
        public Builder labelKey(String labelKey) { this.labelKey = labelKey; return this; }
        public Builder optionsKey(String optionsKey) { this.optionsKey = optionsKey; return this; }
        public Builder validation(ValidationRules validation) { this.validation = validation; return this; }
        public Builder permissions(List<String> permissions) { this.permissions = permissions; return this; }

        public SimpleField build() {
            return new SimpleField(type, widget, labelKey, optionsKey, validation, permissions);
        }
    }
}
