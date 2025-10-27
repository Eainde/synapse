package com.eainde.synapse.forms.domain.rules;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Condition(
        @NotBlank String field,
        @NotNull Operator operator,
        Object value // Can be string, number, boolean, or list
) {}
