package com.eainde.synapse;

import com.eainde.synapse.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Map;

public class FormConverter {
    private final ObjectMapper objectMapper;

    public FormConverter() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Main conversion method.
     *
     * @param inputJson The input form definition as a JSON string.
     * @param userRole  The role of the user (e.g., "ANALYST").
     * @param labelMap  A map to resolve labelKeys (e.g., "kyc.sof.title" -> "Source of Funds").
     * @return An ObjectNode containing the generated jsonSchema and uiSchema.
     * @throws JsonProcessingException if the input JSON is invalid.
     */
    public ObjectNode convert(String inputJson, String userRole, Map<String, String> labelMap)
            throws JsonProcessingException {

        // 1. Parse the input JSON string into our POJO model
        InputForm inputForm = objectMapper.readValue(inputJson, InputForm.class);

        // 2. Create the root output node
        ObjectNode output = objectMapper.createObjectNode();

        // 3. Process the two main parts
        processJsonSchema(output.putObject("jsonSchema"), inputForm.getFields(), userRole, labelMap);
        processUiSchema(output.putObject("uiSchema"), inputForm.getLayout(), labelMap);

        return output;
    }

    // --- JSON Schema Generation ---

    private void processJsonSchema(ObjectNode jsonSchema, Map<String, FieldDefinition> fields,
                                   String userRole, Map<String, String> labelMap) {

        jsonSchema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
        jsonSchema.put("type", "object");

        ObjectNode properties = jsonSchema.putObject("properties");
        ArrayNode requiredFields = jsonSchema.putArray("required");

        for (Map.Entry<String, FieldDefinition> entry : fields.entrySet()) {
            String fieldKey = entry.getKey();
            FieldDefinition fieldDef = entry.getValue();

            // Create the property node for this field
            ObjectNode propertyNode = buildSchemaProperty(fieldDef, userRole, labelMap);
            properties.set(fieldKey, propertyNode);

            // Check for top-level required fields
            if (fieldDef.getValidation() != null && Boolean.TRUE.equals(fieldDef.getValidation().getRequired())) {
                requiredFields.add(fieldKey);
            }
        }
    }

    /**
     * Recursively builds a JSON Schema property for a given FieldDefinition.
     */
    private ObjectNode buildSchemaProperty(FieldDefinition fieldDef, String userRole, Map<String, String> labelMap) {
        ObjectNode propertyNode = objectMapper.createObjectNode();

        propertyNode.put("type", fieldDef.getType());
        propertyNode.put("title", labelMap.getOrDefault(fieldDef.getLabelKey(), fieldDef.getLabelKey()));

        // Add validations (other than 'required')
        if (fieldDef.getValidation() != null) {
            Validation val = fieldDef.getValidation();
            if (val.getMinimum() != null) propertyNode.put("minimum", val.getMinimum());
            if (val.getMaximum() != null) propertyNode.put("maximum", val.getMaximum());
        }

        // Add permissions (readOnly flag)
        if (!hasEditPermission(fieldDef.getPermissions(), userRole)) {
            propertyNode.put("readOnly", true);
        }

        // Handle arrays (recursion)
        if ("array".equals(fieldDef.getType()) && fieldDef.getItems() != null) {
            ArrayItems itemsDef = fieldDef.getItems();
            ObjectNode itemsNode = propertyNode.putObject("items");
            itemsNode.put("type", itemsDef.getType()); // "object"

            ObjectNode itemProperties = itemsNode.putObject("properties");
            ArrayNode itemRequired = itemsNode.putArray("required");

            if (itemsDef.getFields() != null) {
                for (Map.Entry<String, FieldDefinition> itemEntry : itemsDef.getFields().entrySet()) {
                    String itemKey = itemEntry.getKey();
                    FieldDefinition itemFieldDef = itemEntry.getValue();

                    // Recursive call for the item's property
                    ObjectNode itemPropertyNode = buildSchemaProperty(itemFieldDef, userRole, labelMap);
                    itemProperties.set(itemKey, itemPropertyNode);

                    // Handle nested required
                    if (itemFieldDef.getValidation() != null && Boolean.TRUE.equals(itemFieldDef.getValidation().getRequired())) {
                        itemRequired.add(itemKey);
                    }
                }
            }
        }

        return propertyNode;
    }

    /**
     * Checks if the user role has EDIT permission.
     * If permissions list is null or empty, access is granted by default.
     */
    private boolean hasEditPermission(List<String> permissions, String userRole) {
        if (permissions == null || permissions.isEmpty()) {
            return true; // Editable by default
        }
        String editPermission = "EDIT:" + userRole.toUpperCase();
        return permissions.stream().anyMatch(p -> p.equalsIgnoreCase(editPermission));
    }

    // --- UI Schema Generation ---

    private void processUiSchema(ObjectNode uiSchema, List<LayoutElement> layout, Map<String, String> labelMap) {
        // Special case: If root has only one Group, the uiSchema *is* that Group.
        if (layout.size() == 1 && layout.get(0) instanceof Group) {
            buildUiGroup(uiSchema, (Group) layout.get(0), labelMap);
        } else {
            // Otherwise, root is a VerticalLayout containing the elements.
            uiSchema.put("type", "VerticalLayout");
            ArrayNode elements = uiSchema.putArray("elements");
            for (LayoutElement el : layout) {
                buildUiElement(elements.addObject(), el, labelMap);
            }
        }
    }

    /**
     * Generic dispatcher for building any UI element.
     */
    private void buildUiElement(ObjectNode node, LayoutElement el, Map<String, String> labelMap) {
        if (el instanceof Group) {
            buildUiGroup(node, (Group) el, labelMap);
        } else if (el instanceof Row) {
            buildUiRow(node, (Row) el, labelMap);
        }
        // 'Field' elements are handled inside 'buildUiRow'
    }

    /**
     * Builds a UI "Group"
     */
    private void buildUiGroup(ObjectNode groupNode, Group group, Map<String, String> labelMap) {
        groupNode.put("type", "Group");
        groupNode.put("label", labelMap.getOrDefault(group.getLabelKey(), group.getLabelKey()));
        ArrayNode elements = groupNode.putArray("elements");
        for (LayoutElement el : group.getElements()) {
            buildUiElement(elements.addObject(), el, labelMap); // Recursive call
        }
    }

    /**
     * Builds a UI "Row", which becomes either a "Control" or a "HorizontalLayout".
     */
    private void buildUiRow(ObjectNode rowNode, Row row, Map<String, String> labelMap) {
        List<LayoutElement> rowElements = row.getElements();

        // Rule: A Row with one Field becomes a Control.
        if (rowElements.size() == 1 && rowElements.get(0) instanceof Field) {
            Field field = (Field) rowElements.get(0);
            buildUiControl(rowNode, field.getKey());
        }
        // Rule: A Row with multiple elements becomes a HorizontalLayout.
        else {
            rowNode.put("type", "HorizontalLayout");
            ArrayNode elements = rowNode.putArray("elements");
            for (LayoutElement el : rowElements) {
                if (el instanceof Field) {
                    buildUiControl(elements.addObject(), ((Field) el).getKey());
                }
                // You could also handle nested Groups/Rows here if your spec allows it.
            }
        }
    }

    /**
     * Builds a UI "Control"
     */
    private void buildUiControl(ObjectNode controlNode, String fieldKey) {
        controlNode.put("type", "Control");
        controlNode.put("scope", "#/properties/" + fieldKey);
    }
}
