package com.eainde.synapse.forms;

import com.eainde.synapse.forms.config.FormRendererConfig;
import com.eainde.synapse.forms.exception.RenderingException;
import com.eainde.synapse.forms.exception.ValidationError;
import com.eainde.synapse.forms.domain.CanonicalFormMessage;
import com.eainde.synapse.forms.domain.fields.*;
import com.eainde.synapse.forms.domain.layout.FieldRef;
import com.eainde.synapse.forms.domain.layout.Group;
import com.eainde.synapse.forms.domain.layout.Row;
import com.eainde.synapse.forms.domain.rules.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * End-to-end tests that build a canonical model and render it.
 */
class FormRendererIntegrationTest {

    private static JsonFormatRenderer renderer;
    private static ObjectMapper objectMapper = new ObjectMapper(); // For test assertions

    @BeforeAll
    static void setUp() {
        // Manually build the renderer using its config class
        FormRendererConfig config = new FormRendererConfig();
        renderer = config.jsonFormatRenderer();
    }

    // Helper to load expected JSON from a file
    private JsonNode getExpectedJson(String name) throws Exception {
        try (var in = getClass().getClassLoader().getResourceAsStream(name)) {
            assertThat(in).isNotNull();
            return objectMapper.readTree(in);
        }
    }

    @Test
    void testRenderSimpleForm_Example1() throws Exception {
        CanonicalFormMessage message = CanonicalFormMessage.builder()
                .formId("kyc_source_of_funds")
                .layout(List.of(
                        new Group("kyc.sof.title", List.of(
                                new Row(List.of(new FieldRef("primarySource"))),
                                new Row(List.of(new FieldRef("occupation"))),
                                new Row(List.of(new FieldRef("annualIncome"))),
                                new Row(List.of(new FieldRef("accountPurpose"))),
                                new Row(List.of(new FieldRef("otherDetails")))
                        ))
                ))
                .fields(Map.of(
                        "primarySource", SimpleField.builder().type("string").widget("select").labelKey("kyc.sof.q1.source")
                                .optionsKey("fund_sources").validation(ValidationRules.builder().required(true).build()).build(),
                        "occupation", SimpleField.builder().type("string").widget("text").labelKey("kyc.sof.q2.occupation")
                                .validation(ValidationRules.builder().required(true).build()).build(),
                        "annualIncome", SimpleField.builder().type("string").widget("select").labelKey("kyc.sof.q3.income")
                                .optionsKey("income_brackets").validation(ValidationRules.builder().required(true).build()).build(),
                        "accountPurpose", SimpleField.builder().type("string").widget("select").labelKey("kyc.sof.q4.purpose")
                                .optionsKey("purpose_options").validation(ValidationRules.builder().required(true).build()).build(),
                        "otherDetails", SimpleField.builder().type("string").widget("textarea").labelKey("kyc.sof.q5.other").build()
                ))
                .build();

        JsonNode actualNode = renderer.renderToNode(message, TargetFormat.SYNAPSE_FORM_V1);
        JsonNode expectedNode = getExpectedJson("test-fixtures/example1_simple.json");

        assertThat(actualNode).isEqualTo(expectedNode);
    }

    @Test
    void testRenderMultiGroupLayout_Example2() throws Exception {
        CanonicalFormMessage message = CanonicalFormMessage.builder()
                .formId("kyc_sof_multi_group")
                .layout(List.of(
                        new Group("kyc.sof.group1.income_title", List.of(
                                new Row(List.of(new FieldRef("primarySource"))),
                                new Row(List.of(new FieldRef("occupation"), new FieldRef("industry"))),
                                new Row(List.of(new FieldRef("annualIncome")))
                        )),
                        new Group("kyc.sof.group2.account_title", List.of(
                                new Row(List.of(new FieldRef("accountPurpose"))),
                                new Row(List.of(new FieldRef("initialDepositAmount")))
                        ))
                ))
                .fields(Map.of(
                        "primarySource", SimpleField.builder().type("string").widget("select").labelKey("kyc.sof.q1.source")
                                .optionsKey("fund_sources").validation(ValidationRules.builder().required(true).build()).build(),
                        "occupation", SimpleField.builder().type("string").widget("text").labelKey("kyc.sof.q2.occupation")
                                .validation(ValidationRules.builder().required(true).build()).build(),
                        "industry", SimpleField.builder().type("string").widget("select").labelKey("kyc.sof.q3.industry")
                                .optionsKey("industry_types").build(),
                        "annualIncome", SimpleField.builder().type("string").widget("select").labelKey("kyc.sof.q4.income")
                                .optionsKey("income_brackets").validation(ValidationRules.builder().required(true).build()).build(),
                        "accountPurpose", SimpleField.builder().type("string").widget("select").labelKey("kyc.sof.q5.purpose")
                                .optionsKey("purpose_options").validation(ValidationRules.builder().required(true).build()).build(),
                        "initialDepositAmount", SimpleField.builder().type("number").widget("currency").labelKey("kyc.sof.q6.deposit").build()
                ))
                .build();

        JsonNode actualNode = renderer.renderToNode(message, TargetFormat.SYNAPSE_FORM_V1);
        JsonNode expectedNode = getExpectedJson("test-fixtures/example2_multi_group.json");

        assertThat(actualNode).isEqualTo(expectedNode);
    }

    @Test
    void testRenderGridForm_Example3() throws Exception {
        // Define the columns for the grid
        Map<String, FieldDefinition> gridFields = Map.of(
                "country", SimpleField.builder().type("string").widget("select")
                        .labelKey("kyc.sof.col.country").optionsKey("country_list").build(),
                "isHrtc", SimpleField.builder().type("boolean").widget("switch")
                        .labelKey("kyc.sof.col.is_hrtc").build(),
                "hrtcExposure", SimpleField.builder().type("number").widget("percentage")
                        .labelKey("kyc.sof.col.exposure").build()
        );

        ArrayField sofCountriesField = ArrayField.builder()
                .widget("table")
                .labelKey("kyc.sof.grid.title")
                .items(new ObjectItem(gridFields))
                .build();

        CanonicalFormMessage message = CanonicalFormMessage.builder()
                .formId("kyc_sof_country_grid")
                .layout(List.of(
                        new Group("kyc.sof.title", List.of(
                                new Row(List.of(new FieldRef("sofCountries")))
                        ))
                ))
                .fields(Map.of("sofCountries", sofCountriesField))
                .build();

        JsonNode actualNode = renderer.renderToNode(message, TargetFormat.SYNAPSE_FORM_V1);
        JsonNode expectedNode = getExpectedJson("test-fixtures/example3_grid.json");

        // Note: The example3_grid.json in the prompt has a typo: "type: "Group"".
        // The test fixture must have this corrected to "type": "Group".
        assertThat(actualNode).isEqualTo(expectedNode);
    }

    @Test
    void testRenderPermissionsAndValidations_Example4() throws Exception {
        // Define grid columns with their own permissions/validations
        Map<String, FieldDefinition> gridFields = Map.of(
                "country", SimpleField.builder().type("string").widget("select")
                        .labelKey("kyc.sof.col.country").optionsKey("country_list")
                        .validation(ValidationRules.builder().required(true).build()).build(),
                "isHrtc", SimpleField.builder().type("boolean").widget("switch")
                        .labelKey("kyc.sof.col.is_hrtc")
                        .permissions(List.of("VIEW:USER", "EDIT:ANALYST", "EDIT:ADMIN")).build(),
                "hrtcExposure", SimpleField.builder().type("number").widget("percentage")
                        .labelKey("kyc.sof.col.exposure")
                        .validation(ValidationRules.builder().required(true).minimum(0).maximum(100).build())
                        .permissions(List.of("VIEW:USER", "VIEW:ANALYST", "EDIT:ADMIN")).build()
        );

        ArrayField sofCountriesField = ArrayField.builder()
                .widget("table")
                .labelKey("kyc.sof.grid.title")
                .permissions(List.of("VIEW:USER", "EDIT:ANALYST", "EDIT:ADMIN"))
                .items(new ObjectItem(gridFields))
                .build();

        SimpleField primarySourceField = SimpleField.builder().type("string").widget("select")
                .labelKey("kyc.sof.q1.source").optionsKey("fund_sources")
                .validation(ValidationRules.builder().required(true).build())
                .permissions(List.of("VIEW:USER", "EDIT:ANALYST", "EDIT:ADMIN"))
                .build();

        CanonicalFormMessage message = CanonicalFormMessage.builder()
                .formId("kyc_sof_grid_perms")
                .layout(List.of(
                        new Group("kyc.sof.group1.primary_title", List.of(
                                new Row(List.of(new FieldRef("primarySource")))
                        )),
                        new Group("kyc.sof.group2.assets_title", List.of(
                                new Row(List.of(new FieldRef("sofCountries")))
                        ))
                ))
                .fields(Map.of(
                        "primarySource", primarySourceField,
                        "sofCountries", sofCountriesField
                ))
                .build();

        JsonNode actualNode = renderer.renderToNode(message, TargetFormat.SYNAPSE_FORM_V1);
        JsonNode expectedNode = getExpectedJson("test-fixtures/example4_permissions.json");

        assertThat(actualNode).isEqualTo(expectedNode);
    }

    @Test
    void testBeanValidationFailure() {
        // Build an invalid message (missing formId)
        CanonicalFormMessage message = CanonicalFormMessage.builder()
                .formId(null) // This is invalid
                .layout(List.of(new Group("a", List.of(new FieldRef("b")))))
                .fields(Map.of("b", SimpleField.builder().type("string").widget("text").labelKey("c").build()))
                .build();

        assertThatThrownBy(() -> renderer.render(message, TargetFormat.SYNAPSE_FORM_V1))
                .isInstanceOf(RenderingException.class)
                .hasMessageContaining("Input CanonicalFormMessage failed validation")
                .extracting(ex -> ((RenderingException) ex).getErrorCode())
                .isEqualTo("ERR_BEAN_VALIDATION");
    }

    @Test
    void testSchemaValidationFailure() {
        // Build a message that is valid per POJOs but not schema
        // e.g., schema says `layout` minItems is 1, but we pass an empty list
        CanonicalFormMessage message = new CanonicalFormMessage(
                "1.0.0",
                "test-form",
                List.of(), // Invalid, schema requires minItems: 1
                Map.of("b", SimpleField.builder().type("string").widget("text").labelKey("c").build())
        );

        assertThatThrownBy(() -> renderer.render(message, TargetFormat.SYNAPSE_FORM_V1))
                .isInstanceOf(RenderingException.class)
                .hasMessageContaining("Output JSON failed schema validation")
                .extracting(ex -> ((RenderingException) ex).getErrorCode())
                .isEqualTo("ERR_SCHEMA_VALIDATION");

        // Check for the specific error
        assertThatThrownBy(() -> renderer.render(message, TargetFormat.SYNAPSE_FORM_V1))
                .isInstanceOfSatisfying(RenderingException.class, ex -> {
                    assertThat(ex.getDetails()).hasSize(1);
                    ValidationError error = ex.getDetails().get(0);
                    assertThat(error.code()).isEqualTo("1016"); // networknt code for minItems
                    assertThat(error.jsonPointer()).isEqualTo("$.layout");
                });
    }

    @Test
    void testRenderConditionalFormV2_Example4() throws Exception {
        // 1. Define the conditional rule for 'hrtcExposure'
        Condition hrtcCondition = new Condition("isHrtc", Operator.EQUALS, true);
        ValidationRule hrtcRule = new ValidationRule(
                ConditionBlock.allOf(hrtcCondition),
                new ValidationAction(true, null, null) // then: { required: true }
        );

        // 2. Define the 'sofCountries' grid columns
        FieldDefinition countryField = SimpleField.builder()
                .type("string").widget("select").labelKey("kyc.sof.col.country")
                .optionsKey("country_list").validation(ValidationRules.builder().required(true).build()).build();

        FieldDefinition isHrtcField = SimpleField.builder()
                .type("boolean").widget("switch").labelKey("kyc.sof.col.is_hrtc")
                .permissions(List.of("VIEW:USER", "EDIT:ANALYST", "EDIT:ADMIN")).build();

        FieldDefinition hrtcExposureField = SimpleField.builder()
                .type("number").widget("percentage").labelKey("kyc.sof.col.exposure")
                .validation(ValidationRules.builder()
                        .minimum(0)
                        .maximum(100)
                        .rules(List.of(hrtcRule)) // <-- Add the conditional rule
                        .build())
                .permissions(List.of("VIEW:USER", "VIEW:ANALYST", "EDIT:ADMIN")).build();

        // 3. Define the 'sofCountries' ArrayField
        ArrayField sofCountriesField = ArrayField.builder()
                .widget("table").labelKey("kyc.sof.grid.title")
                .permissions(List.of("VIEW:USER", "EDIT:ANALYST", "EDIT:ADMIN"))
                .items(new ObjectItem(Map.of(
                        "country", countryField,
                        "isHrtc", isHrtcField,
                        "hrtcExposure", hrtcExposureField
                )))
                .build();

        // 4. Define the conditional rule for 'otherSourceDetails'
        Condition otherSourceCondition = new Condition("primarySource", Operator.EQUALS, "OTHER_SOURCE");

        ValidationRule otherSourceValidationRule = new ValidationRule(
                ConditionBlock.allOf(otherSourceCondition),
                new ValidationAction(true, null, null) // then: { required: true }
        );

        VisibilityRule otherSourceVisibilityRule = new VisibilityRule(
                ConditionBlock.allOf(otherSourceCondition),
                true // then: { visible: true }
        );

        // 5. Define the 'primarySource' and 'otherSourceDetails' fields
        SimpleField primarySourceField = SimpleField.builder()
                .type("string").widget("select").labelKey("kyc.sof.q1.source")
                .optionsKey("fund_sources").validation(ValidationRules.builder().required(true).build())
                .permissions(List.of("VIEW:USER", "EDIT:ANALYST", "EDIT:ADMIN")).build();

        SimpleField otherSourceDetailsField = SimpleField.builder()
                .type("string").widget("textarea").labelKey("kyc.sof.q1.other_details")
                .validation(ValidationRules.builder()
                        .rules(List.of(otherSourceValidationRule)) // <-- Add the conditional validation
                        .build())
                .build();

        // 6. Define the Layout
        Group group1 = new Group(
                "kyc.sof.group1.primary_title",
                List.of(
                        new Row(List.of(new FieldRef("primarySource"))), // Regular row
                        new Row( // Conditional row
                                List.of(new FieldRef("otherSourceDetails")),
                                List.of(otherSourceVisibilityRule) // <-- Add the conditional visibility
                        )
                )
        );

        Group group2 = new Group(
                "kyc.sof.group2.assets_title",
                List.of(new Row(List.of(new FieldRef("sofCountries"))))
        );

        // 7. Assemble the final message
        CanonicalFormMessage message = CanonicalFormMessage.builder()
                .formId("kyc_sof_grid_perms_conditional")
                .schemaVersion("2.0.0") // <-- Use V2 schema version
                .layout(List.of(group1, group2))
                .fields(Map.of(
                        "primarySource", primarySourceField,
                        "otherSourceDetails", otherSourceDetailsField,
                        "sofCountries", sofCountriesField
                ))
                .build();

        // 8. Render and assert
        JsonNode actualNode = renderer.renderToNode(message, TargetFormat.SYNAPSE_FORM_V2);
        JsonNode expectedNode = getExpectedJson("test-fixtures/example4_v2_conditional.json");

        assertThat(actualNode).isEqualTo(expectedNode);
    }
}
