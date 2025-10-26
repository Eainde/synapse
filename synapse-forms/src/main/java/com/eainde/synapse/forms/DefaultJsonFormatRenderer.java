package com.eainde.synapse.forms;

import com.eainde.synapse.forms.exception.RenderingException;
import com.eainde.synapse.forms.exception.ValidationError;
import com.eainde.synapse.forms.mapper.CanonicalToTargetMapper;
import com.eainde.synapse.forms.model.CanonicalFormMessage;
import com.eainde.synapse.forms.validation.SchemaValidator;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default implementation of the renderer.
 * Orchestrates bean validation, mapping, and JSON schema validation.
 */
public class DefaultJsonFormatRenderer implements JsonFormatRenderer {

    private final Validator beanValidator;
    private final SchemaValidator schemaValidator;
    private final Map<TargetFormat, CanonicalToTargetMapper> mappers;

    public DefaultJsonFormatRenderer(Validator beanValidator,
                                     SchemaValidator schemaValidator,
                                     Map<TargetFormat, CanonicalToTargetMapper> mappers) {
        this.beanValidator = beanValidator;
        this.schemaValidator = schemaValidator;
        this.mappers = mappers;
    }

    @Override
    public String render(CanonicalFormMessage message, TargetFormat format) throws RenderingException {
        JsonNode node = renderToNode(message, format);
        return node.toString();
    }

    @Override
    public JsonNode renderToNode(CanonicalFormMessage message, TargetFormat format) throws RenderingException {
        // 1. Get the correct mapper
        CanonicalToTargetMapper mapper = mappers.get(format);
        if (mapper == null) {
            throw new RenderingException("No mapper configured for format: " + format, "ERR_NO_MAPPER", null);
        }

        // 2. Perform Jakarta Bean Validation on the input POJO
        validateBean(message);

        // 3. Perform the mapping (POJO -> JsonNode)
        JsonNode outputNode = mapper.map(message);

        // 4. Perform JSON Schema validation on the output JsonNode
        List<ValidationError> schemaErrors = schemaValidator.validate(outputNode, format);
        if (!schemaErrors.isEmpty()) {
            throw new RenderingException("Output JSON failed schema validation.", "ERR_SCHEMA_VALIDATION", schemaErrors);
        }

        // 5. Return the valid node
        return outputNode;
    }

    private void validateBean(CanonicalFormMessage message) {
        Set<ConstraintViolation<CanonicalFormMessage>> violations = beanValidator.validate(message);
        if (!violations.isEmpty()) {
            List<ValidationError> errors = violations.stream()
                    .map(v -> new ValidationError(
                            "ERR_BEAN_VALIDATION",
                            v.getMessage(),
                            v.getPropertyPath().toString()
                    ))
                    .collect(Collectors.toList());
            throw new RenderingException("Input CanonicalFormMessage failed validation.", "ERR_BEAN_VALIDATION", errors);
        }
    }
}
