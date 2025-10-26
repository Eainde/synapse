package com.eainde.synapse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Map;

public class Application {

    public static void main(String[] args) throws JsonProcessingException {
        FormConverter converter = new FormConverter();
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        // --- This Label Map is CRUCIAL ---
        // In a real app, you would load this from a database or properties file.
        Map<String, String> dummyLabelMap = new HashMap<>();

        // Labels for Example 1 & 2
        dummyLabelMap.put("kyc.sof.title", "Source of Funds Questionnaire");
        dummyLabelMap.put("kyc.sof.q1.source", "What is your primary source of funds?");
        dummyLabelMap.put("kyc.sof.q2.occupation", "Please specify your occupation.");
        dummyLabelMap.put("kyc.sof.q3.income", "What is your estimated annual income?");
        dummyLabelMap.put("kyc.sof.q4.purpose", "What is the primary purpose of this account?");
        dummyLabelMap.put("kyc.sof.q5.other", "Please provide details if 'Other' was selected.");

        // Labels for Example 2
        dummyLabelMap.put("kyc.sof.group1.income_title", "Income & Employment");
        dummyLabelMap.put("kyc.sof.group2.account_title", "Account Details");
        dummyLabelMap.put("kyc.sof.q3.industry", "Industry");
        dummyLabelMap.put("kyc.sof.q4.income", "What is your estimated annual income?"); // Duplicate, OK
        dummyLabelMap.put("kyc.sof.q5.purpose", "What is the primary purpose of this account?"); // Duplicate, OK
        dummyLabelMap.put("kyc.sof.q6.deposit", "Estimated initial deposit amount");
        dummyLabelMap.put("kyc.sof.q2.occupation", "Occupation"); // Overwriting for Example 2

        // Labels for Example 3
        dummyLabelMap.put("kyc.sof.grid.title", "Select a country which contributes towards the party's SoF");
        dummyLabelMap.put("kyc.sof.col.country", "Select a country which contributes towards the part...");
        dummyLabelMap.put("kyc.sof.col.is_hrtc", "Is this a HRTC?");
        dummyLabelMap.put("kyc.sof.col.exposure", "% of Funds with HRTC exposure");
        dummyLabelMap.put("kyc.sof.title", "What is the Source of Funds (SoF) provided for the relationship?"); // Overwriting for Example 3

        // Labels for Example 4
        dummyLabelMap.put("kyc.sof.group1.primary_title", "Primary Source of Funds");
        dummyLabelMap.put("kyc.sof.group2.assets_title", "Other Assets");
        // Re-uses other labels from previous examples


        // --- Example 4: Permissions and Validations ---
        String inputJson4 = """
        {
          "formId": "kyc_sof_grid_perms",
          "layout": [
            { "type": "Group", "labelKey": "kyc.sof.group1.primary_title",
              "elements": [ { "type": "Row", "elements": [{ "type": "Field", "key": "primarySource" }] } ]
            },
            { "type": "Group", "labelKey": "kyc.sof.group2.assets_title",
              "elements": [ { "type": "Row", "elements": [{ "type": "Field", "key": "sofCountries" }] } ]
            }
          ],
          "fields": {
            "primarySource": {
              "type": "string", "widget": "select", "labelKey": "kyc.sof.q1.source", "optionsKey": "fund_sources",
              "validation": { "required": true },
              "permissions": ["VIEW:USER", "EDIT:ANALYST", "EDIT:ADMIN"]
            },
            "sofCountries": {
              "type": "array", "widget": "table", "labelKey": "kyc.sof.grid.title",
              "permissions": ["VIEW:USER", "EDIT:ANALYST", "EDIT:ADMIN"],
              "items": {
                "type": "object",
                "fields": {
                  "country": {
                    "type": "string", "widget": "select", "labelKey": "kyc.sof.col.country", "optionsKey": "country_list",
                    "validation": { "required": true }
                  },
                  "isHrtc": {
                    "type": "boolean", "widget": "switch", "labelKey": "kyc.sof.col.is_hrtc",
                    "permissions": ["VIEW:USER", "EDIT:ANALYST", "EDIT:ADMIN"]
                  },
                  "hrtcExposure": {
                    "type": "number", "widget": "percentage", "labelKey": "kyc.sof.col.exposure",
                    "validation": { "required": true, "minimum": 0, "maximum": 100 },
                    "permissions": ["VIEW:USER", "VIEW:ANALYST", "EDIT:ADMIN"]
                  }
                }
              }
            }
          }
        }
        """;

        System.out.println("--- Processing Example 4 (Role: ANALYST) ---");

        // Define the user context
        String userRole = "ANALYST";

        ObjectNode output4 = converter.convert(inputJson4, userRole, dummyLabelMap);
        System.out.println(mapper.writeValueAsString(output4));


        // --- You can also run the other examples ---

        // System.out.println("\n--- Processing Example 1 (Role: USER) ---");
        // String inputJson1 = ... (paste example 1 JSON here) ...
        // ObjectNode output1 = converter.convert(inputJson1, "USER", dummyLabelMap);
        // System.out.println(mapper.writeValueAsString(output1));

        // System.out.println("\n--- Processing Example 2 (Role: ADMIN) ---");
        // String inputJson2 = ... (paste example 2 JSON here) ...
        // ObjectNode output2 = converter.convert(inputJson2, "ADMIN", dummyLabelMap);
        // System.out.println(mapper.writeValueAsString(output2));

        // System.out.println("\n--- Processing Example 3 (Role: USER) ---");
        // Note: I fixed a typo in your input 3 ("type: "Group" -> "type": "Group")
        // String inputJson3 = ... (paste example 3 JSON here) ...
        // ObjectNode output3 = converter.convert(inputJson3, "USER", dummyLabelMap);
        // System.out.println(mapper.writeValueAsString(output3));
    }
}
