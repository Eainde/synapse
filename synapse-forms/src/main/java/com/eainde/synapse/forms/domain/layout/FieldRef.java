package com.eainde.synapse.forms.domain.layout;

import com.eainde.synapse.forms.domain.rules.VisibilityRule;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FieldRef(
        @NotBlank(message = "Field reference key cannot be blank")
        String key,
        List<@Valid VisibilityRule> visibilityRules
) implements LayoutElement {

    public FieldRef(String key) { // <-- ADDED 'public'
        this(key, null);
    }

    @Override
    public String getType() {
        return "Field";
    }

    @Override
    public List<VisibilityRule> getVisibilityRules() {
        return visibilityRules;
    }
}
