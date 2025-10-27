package com.eainde.synapse.forms.domain.layout;

import com.eainde.synapse.forms.domain.rules.VisibilityRule;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

/**
 * Polymorphic interface for all layout components (Group, Row, FieldRef).
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Group.class, name = "Group"),
        @JsonSubTypes.Type(value = Row.class, name = "Row"),
        @JsonSubTypes.Type(value = FieldRef.class, name = "Field")
})
public interface LayoutElement {
    String getType();
    List<VisibilityRule> getVisibilityRules();
}
