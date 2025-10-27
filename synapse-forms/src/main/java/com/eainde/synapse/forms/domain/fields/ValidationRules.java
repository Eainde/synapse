package com.eainde.synapse.forms.domain.fields;

import com.eainde.synapse.forms.domain.rules.ValidationRule;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.List;

/**
 * Represents validation rules for a field.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ValidationRules(
        Boolean required,
        @Min(0) Integer minimum,
        @Min(0) Integer maximum,
        List<@Valid ValidationRule> rules
) {
    // Builder for convenience
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Boolean required;
        private Integer minimum;
        private Integer maximum;
        private List<ValidationRule> rules;

        public Builder required(boolean required) { this.required = required; return this; }
        public Builder minimum(int minimum) { this.minimum = minimum; return this; }
        public Builder maximum(int maximum) { this.maximum = maximum; return this; }
        public Builder rules(List<ValidationRule> rules) { this.rules = rules; return this; }

        public ValidationRules build() {
            return new ValidationRules(required, minimum, maximum, rules);
        }
    }
}
