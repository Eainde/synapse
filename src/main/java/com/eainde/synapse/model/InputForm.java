package com.eainde.synapse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InputForm {
    private String formId;
    private List<LayoutElement> layout;
    private Map<String, FieldDefinition> fields;

}
