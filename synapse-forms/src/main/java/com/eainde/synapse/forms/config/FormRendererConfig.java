package com.eainde.synapse.forms.config;

import com.eainde.synapse.forms.DefaultJsonFormatRenderer;
import com.eainde.synapse.forms.JsonFormatRenderer;
import com.eainde.synapse.forms.TargetFormat;
import com.eainde.synapse.forms.mapper.CanonicalToTargetMapper;
import com.eainde.synapse.forms.mapper.DynamicFormV1Mapper;
import com.eainde.synapse.forms.validation.SchemaValidator;
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
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
    public DynamicFormV1Mapper dynamicFormV1Mapper(ObjectMapper objectMapper) {
        return new DynamicFormV1Mapper(objectMapper);
    }

    /**
     * Assembles and returns the main JsonFormatRenderer.
     */
    public JsonFormatRenderer jsonFormatRenderer() {
        ObjectMapper objectMapper = formObjectMapper();
        Validator validator = beanValidator();
        SchemaValidator schemaValidator = schemaValidator();

        // This is the extensible registry
        Map<TargetFormat, CanonicalToTargetMapper> mappers = Map.of(
                TargetFormat.SYNAPSE_FORM_V1, dynamicFormV1Mapper(objectMapper)
        );

        return new DefaultJsonFormatRenderer(validator, schemaValidator, mappers);
    }
}
