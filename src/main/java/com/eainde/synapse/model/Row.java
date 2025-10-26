package com.eainde.synapse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Row implements LayoutElement {
    private String type;
    private List<LayoutElement> elements;
}
