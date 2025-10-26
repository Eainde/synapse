package com.eainde.synapse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldDefinition {
    private String type;
    private String widget;
    private String labelKey;
    private String optionsKey;
    private Validation validation;
    private List<String> permissions;
    private ArrayItems items;
}
