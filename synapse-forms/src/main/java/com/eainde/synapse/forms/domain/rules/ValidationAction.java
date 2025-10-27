package com.eainde.synapse.forms.domain.rules;

import com.fasterxml.jackson.annotation.JsonInclude;

// This is the "then" block for a validation rule
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ValidationAction(
        Boolean required,
        Integer minimum,
        Integer maximum
) {}
