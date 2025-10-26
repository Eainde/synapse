package com.eainde.synapse.forms;

import com.eainde.synapse.forms.config.FormRendererConfig;
import com.eainde.synapse.forms.exception.RenderingException;
import com.eainde.synapse.forms.exception.ValidationError;
import com.eainde.synapse.forms.model.CanonicalFormMessage;
import com.eainde.synapse.forms.model.fields.*;
import com.eainde.synapse.forms.model.layout.FieldRef;
import com.eainde.synapse.forms.model.layout.Group;
import com.eainde.synapse.forms.model.layout.Row;
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
}
