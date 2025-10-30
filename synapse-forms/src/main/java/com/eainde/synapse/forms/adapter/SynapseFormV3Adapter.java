package com.eainde.synapse.forms.adapter;

import com.eainde.synapse.forms.domain.CanonicalFormMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Concrete Adapter for DYNAMIC_FORM_V3.
 * This is a 1:1 serialization for the recursive model.
 */
public class SynapseFormV3Adapter implements JsonFormAdapter {
    private final ObjectMapper objectMapper;

    public SynapseFormV3Adapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public JsonNode map(CanonicalFormMessage message) {
        // For V1, the mapping is a direct 1:1 serialization.
        // A future V2 mapper might perform complex transformations here.
        return objectMapper.convertValue(message, JsonNode.class);
    }
}
