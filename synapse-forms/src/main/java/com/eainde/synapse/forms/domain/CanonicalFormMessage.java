package com.eainde.synapse.forms.domain;

import com.eainde.synapse.forms.domain.fields.FieldDefinition;
import com.eainde.synapse.forms.domain.layout.LayoutElement;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;
import java.util.Map;

/**
 * The root canonical object representing a complete dynamic form definition.
 * This is the "common object" services will build and pass to the renderer.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"schemaVersion", "formId", "layout", "fields"})
public record CanonicalFormMessage(
        @NotBlank(message = "schemaVersion is required")
        @Pattern(regexp = "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$",
                message = "schemaVersion must be a valid semver string")
        String schemaVersion,

        @NotBlank(message = "formId is required")
        String formId,

        @Valid
        //@NotEmpty(message = "layout must not be empty")
        List<LayoutElement> layout,

        @Valid
        @NotNull(message = "fields map must not be null")
        Map<String, FieldDefinition> fields
) {
    // Builder pattern for easier construction
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String schemaVersion = "1.0.0"; // Default version
        private String formId;
        private List<LayoutElement> layout;
        private Map<String, FieldDefinition> fields;

        public Builder schemaVersion(String schemaVersion) {
            this.schemaVersion = schemaVersion;
            return this;
        }

        public Builder formId(String formId) {
            this.formId = formId;
            return this;
        }

        public Builder layout(List<LayoutElement> layout) {
            this.layout = layout;
            return this;
        }

        public Builder fields(Map<String, FieldDefinition> fields) {
            this.fields = fields;
            return this;
        }

        public CanonicalFormMessage build() {
            return new CanonicalFormMessage(schemaVersion, formId, layout, fields);
        }
    }
}
