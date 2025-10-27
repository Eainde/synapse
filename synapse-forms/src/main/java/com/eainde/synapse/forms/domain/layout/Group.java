package com.eainde.synapse.forms.domain.layout;

import com.eainde.synapse.forms.domain.rules.VisibilityRule;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Group(
        String labelKey,
        @Valid @NotEmpty List<LayoutElement> elements,
        List<@Valid VisibilityRule> visibilityRules
) implements LayoutElement {

    public Group(String labelKey, List<LayoutElement> elements) {
        this(labelKey, elements, null);
    }
    @Override
    public String getType() {
        return "Group";
    }

    @Override public List<VisibilityRule> getVisibilityRules() { return visibilityRules; }
}
