package com.eainde.synapse.forms.adapter;

import com.eainde.synapse.forms.domain.CanonicalFormMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Mapper for DYNAMIC_FORM_V1.
 * This is a 1:1 mapping, so it just uses ObjectMapper to convert the POJO.
 */
public class SynapseFormV1Adapter implements JsonFormAdapter {

    private final ObjectMapper objectMapper;

    public SynapseFormV1Adapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public JsonNode map(CanonicalFormMessage message) {
        // For V1, the mapping is a direct 1:1 serialization.
        // A future V2 mapper might perform complex transformations here.
        return objectMapper.convertValue(message, JsonNode.class);
    }
}
