package com.eainde.synapse.forms.domain.rules;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record VisibilityRule(
        @Valid @NotNull RuleBlock when,
        boolean visible // The 'then' action
) {}
