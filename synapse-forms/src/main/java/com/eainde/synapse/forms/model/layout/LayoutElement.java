package com.eainde.synapse.forms.model.layout;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
}
