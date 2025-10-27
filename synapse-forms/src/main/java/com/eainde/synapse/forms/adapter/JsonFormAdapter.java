package com.eainde.synapse.forms.adapter;

import com.eainde.synapse.forms.domain.CanonicalFormMessage;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Interface for a mapper that transforms the canonical message into a target JsonNode.
 */
@FunctionalInterface
public interface JsonFormAdapter {
    JsonNode map(CanonicalFormMessage message);
}
