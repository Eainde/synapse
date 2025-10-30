package com.eainde.synapse.forms.domain.rules;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ValidationRule(
        @Valid @NotNull RuleBlock when,
        @Valid @NotNull ValidationAction then
) {}
