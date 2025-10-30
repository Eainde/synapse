package com.eainde.synapse.forms.domain.rules;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * A recursive block for defining nested logic.
 * Replaces the flat v2 'ConditionBlock'.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RuleBlock(
        /**
         * The logical operator (AND, OR) that joins all
         * conditions and child rules in this block.
         */
        @NotNull LogicalOperator operator,

        /**
         * A list of simple (leaf) conditions.
         */
        List<@Valid Condition> conditions,

        /**
         * A list of nested child RuleBlocks.
         */
        List<@Valid RuleBlock> rules
) {}
