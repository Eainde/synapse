package com.eainde.synapse.forms.domain.rules;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import java.util.List;

//TODO not needed
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConditionBlock(
        @Valid List<Condition> allOf,
        @Valid List<Condition> anyOf
) {
    // Helper factory for simple 'AND' conditions
    // allOf means AND (All conditions in the list must be true).
    public static ConditionBlock allOf(Condition... conditions) {
        return new ConditionBlock(List.of(conditions), null);
    }

    // Helper factory for simple 'OR' conditions
    // anyOf means OR (At least one condition in the list must be true).
    public static ConditionBlock anyOf(Condition... conditions) {
        return new ConditionBlock(null, List.of(conditions));
    }
}
