package com.eainde.synapse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Field implements LayoutElement {
    private String type;
    private String key;
}
