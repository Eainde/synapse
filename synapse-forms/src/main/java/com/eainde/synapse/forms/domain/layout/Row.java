package com.eainde.synapse.forms.domain.layout;

import com.eainde.synapse.forms.domain.rules.VisibilityRule;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Row(
        @Valid @NotEmpty List<LayoutElement> elements,
        List<@Valid VisibilityRule> visibilityRules
) implements LayoutElement {

    /**
     * Overloaded constructor for V1/non-conditional rows.
     */
    public Row(List<LayoutElement> elements) {
        this(elements, null);
    }

    @Override
    public String getType() { return "Row"; }

    @Override
    public List<VisibilityRule> getVisibilityRules() { return visibilityRules; }
}
