package com.eainde.synapse.forms.domain.fields;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * Polymorphic base for field definitions.
 * Uses 'type' property to discriminate between subtypes.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SimpleField.class, name = "string"),
        @JsonSubTypes.Type(value = SimpleField.class, name = "number"),
        @JsonSubTypes.Type(value = SimpleField.class, name = "boolean"),
        @JsonSubTypes.Type(value = ArrayField.class, name = "array")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface FieldDefinition {
    @NotBlank(message = "Field type cannot be blank")
    String getType();

    @NotBlank(message = "Field widget cannot be blank")
    String getWidget();

    @NotBlank(message = "Field labelKey cannot be blank")
    String getLabelKey();

    @Valid
    ValidationRules getValidation();

    List<String> getPermissions();
}
