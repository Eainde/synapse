package com.eainde.synapse.forms;

/**
 * Enum representing the target output formats.
 * This allows the library to be extended with new versions or legacy formats.
 */
public enum TargetFormat {
    /**
     * The v1.0 schema for dynamic forms.
     */
    SYNAPSE_FORM_V1("synapse-form-v1.0.0.schema.json");

    private final String schemaName;

    TargetFormat(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getSchemaName() {
        return schemaName;
    }
}
