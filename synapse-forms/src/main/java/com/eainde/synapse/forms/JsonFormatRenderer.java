package com.eainde.synapse.forms;

import com.eainde.synapse.forms.exception.RenderingException;
import com.eainde.synapse.forms.model.CanonicalFormMessage;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Public API for the form rendering library.
 */
public interface JsonFormatRenderer {

    /**
     * Renders the canonical message into a JSON string for the specified target format.
     *
     * @param message The canonical form message object.
     * @param format  The desired output format.
     * @return A JSON string.
     * @throws RenderingException if validation or mapping fails.
     */
    String render(CanonicalFormMessage message, TargetFormat format) throws RenderingException;

    /**
     * Renders the canonical message into a Jackson JsonNode for the specified target format.
     *
     * @param message The canonical form message object.
     * @param format  The desired output format.
     * @return A Jackson JsonNode.
     * @throws RenderingException if validation or mapping fails.
     */
    JsonNode renderToNode(CanonicalFormMessage message, TargetFormat format) throws RenderingException;
}
