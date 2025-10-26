package com.eainde.synapse.forms.mapper;

import com.eainde.synapse.forms.model.CanonicalFormMessage;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Interface for a mapper that transforms the canonical message into a target JsonNode.
 */
@FunctionalInterface
public interface CanonicalToTargetMapper {
    JsonNode map(CanonicalFormMessage message);
}
