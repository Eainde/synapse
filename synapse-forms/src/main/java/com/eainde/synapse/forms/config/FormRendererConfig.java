package com.eainde.synapse.forms.config;

import com.eainde.synapse.forms.DefaultJsonFormatRenderer;
import com.eainde.synapse.forms.JsonFormatRenderer;
import com.eainde.synapse.forms.TargetFormat;
import com.eainde.synapse.forms.adapter.JsonFormAdapter;
import com.eainde.synapse.forms.adapter.SynapseFormV1Adapter;
import com.eainde.synapse.forms.adapter.SynapseFormV2Adapter;
import com.eainde.synapse.forms.validation.SchemaValidator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Map;

/**
 * Configuration class to build the renderer.
 * Can be used in a Spring @Configuration class or manually.
 */
public class FormRendererConfig {

    /**
     * Creates a fully configured ObjectMapper for deterministic output.
     */
    public ObjectMapper formObjectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                // Configure for stable, deterministic output
                .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * Creates the Jakarta Bean Validator.
     */
    public Validator beanValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator();
        }
    }

    /**
     * Creates the JSON Schema validator.
     */
    public SchemaValidator schemaValidator() {
        return new SchemaValidator();
    }

    /**
     * Creates the V1 mapper.
     */
    public SynapseFormV1Adapter dynamicFormV1Mapper(ObjectMapper objectMapper) {
        return new SynapseFormV1Adapter(objectMapper);
    }

    /**
     * Creates the V2 mapper.
     */
    public SynapseFormV2Adapter dynamicFormV2Mapper(ObjectMapper objectMapper) {
        return new SynapseFormV2Adapter(objectMapper);
    }

    /**
     * Assembles and returns the main JsonFormatRenderer.
     */
    public JsonFormatRenderer jsonFormatRenderer() {
        ObjectMapper objectMapper = formObjectMapper();
        Validator validator = beanValidator();
        SchemaValidator schemaValidator = schemaValidator();

        // This is the extensible registry
        Map<TargetFormat, JsonFormAdapter> mappers = Map.of(
                TargetFormat.SYNAPSE_FORM_V1, dynamicFormV1Mapper(objectMapper),
                TargetFormat.SYNAPSE_FORM_V2, dynamicFormV2Mapper(objectMapper)
        );

        return new DefaultJsonFormatRenderer(validator, schemaValidator, mappers);
    }
}
