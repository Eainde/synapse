package com.eainde.synapse.forms.validation;

import com.eainde.synapse.forms.TargetFormat;
import com.eainde.synapse.forms.exception.RenderingException;
import com.eainde.synapse.forms.exception.ValidationError;
import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles JSON Schema validation.
 * Loads and caches schemas from the classpath.
 */
public class SchemaValidator {

    private final Map<TargetFormat, JsonSchema> schemaCache = new EnumMap<>(TargetFormat.class);
    private final JsonSchemaFactory schemaFactory;

    public SchemaValidator() {
        // Use Draft 7, as specified in the schema
        this.schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        loadSchemas();
    }

    private void loadSchemas() {
        for (TargetFormat format : TargetFormat.values()) {
            try (InputStream schemaStream = getClass().getClassLoader().getResourceAsStream(format.getSchemaName())) {
                if (schemaStream == null) {
                    throw new RuntimeException("Could not find schema file: " + format.getSchemaName());
                }
                SchemaValidatorsConfig config = new SchemaValidatorsConfig();
                config.setHandleNullableField(false);

                JsonSchema schema = schemaFactory.getSchema(schemaStream, config);
                schemaCache.put(format, schema);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load schema: " + format.getSchemaName(), e);
            }
        }
    }

    /**
     * Validates a JsonNode against the cached schema for the given format.
     *
     * @param node   The JsonNode to validate.
     * @param format The target format (to select the correct schema).
     * @return A list of validation errors. Empty if valid.
     */
    public List<ValidationError> validate(JsonNode node, TargetFormat format) {
        JsonSchema schema = schemaCache.get(format);
        if (schema == null) {
            throw new RenderingException("No schema loaded for format: " + format, "ERR_NO_SCHEMA", null);
        }

        Set<ValidationMessage> messages = schema.validate(node);

        return messages.stream()
                .map(msg -> new ValidationError(
                        msg.getCode(),
                        msg.getMessage(),
                        msg.getInstanceLocation().toString() // JSON Pointer
                ))
                .collect(Collectors.toList());
    }
}
