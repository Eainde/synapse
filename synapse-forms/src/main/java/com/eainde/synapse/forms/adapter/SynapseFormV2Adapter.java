package com.eainde.synapse.forms.adapter;

import com.eainde.synapse.forms.domain.CanonicalFormMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Mapper for DYNAMIC_FORM_V2.
 * Like the V1 mapper, this is a 1:1 serialization from the V2 POJO model.
 */
public class SynapseFormV2Adapter implements JsonFormAdapter {

    private final ObjectMapper objectMapper;

    public SynapseFormV2Adapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public JsonNode map(CanonicalFormMessage message) {
        return objectMapper.convertValue(message, JsonNode.class);
    }
}
