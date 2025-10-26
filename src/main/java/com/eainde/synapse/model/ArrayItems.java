package com.eainde.synapse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArrayItems {
    private String type;
    private Map<String, FieldDefinition> fields;
}
