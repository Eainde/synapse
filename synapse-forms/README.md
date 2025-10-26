# Synapse Form Renderer Library

A small, reusable Java 17 library for building, validating, and rendering dynamic form definitions into a specific JSON format.

This library is used by microservices that need to generate form definitions for frontends or other consumers. It ensures that all generated JSON is structurally correct and compliant with the target schema.

## Features

-   **Type-Safe Builder:** Use a fluent Java builder pattern to construct complex form definitions.
-   **Multi-Level Validation:**
    1.  **Bean Validation:** Validates the Java objects at build time (e.g., `@NotBlank`, `@NotEmpty`).
    2.  **JSON Schema Validation:** Validates the final JSON output against a master schema.
-   **Extensible:** Easily add new target formats (e.g., `V2`) or mappers using the registry pattern.
-   **Deterministic Output:** JSON output is stable, with sorted keys.
-   **Clear Error Handling:** Throws a `RenderingException` with a list of `ValidationError` objects, including JSON Pointers to failures.

---

## 1. Quick Start

### 1.1 Add as a Maven Dependency

Add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>com.example.rendering</groupId>
    <artifactId>form-renderer</artifactId>
    <version>1.0.0</version>
</dependency>
```
### 1.2 Configure the Renderer
You can create the renderer manually or wire it up using Spring.

Manual Setup:
```java
import com.example.formrenderer.JsonFormatRenderer;
import com.example.formrenderer.config.FormRendererConfig;

// Use the config class to build a default renderer
FormRendererConfig config = new FormRendererConfig();
JsonFormatRenderer renderer = config.jsonFormatRenderer();
```

Spring Boot @Configuration:
```java
import com.example.formrenderer.JsonFormatRenderer;
import com.example.formrenderer.config.FormRendererConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyServiceConfiguration {

    @Bean
    public JsonFormatRenderer jsonFormatRenderer() {
        // The config class acts as a factory
        return new FormRendererConfig().jsonFormatRenderer();
    }
}
```

### 1.3 Example Usage
Inject or create the JsonFormatRenderer and build your canonical message.
```java
import com.example.formrenderer.JsonFormatRenderer;
import com.example.formrenderer.TargetFormat;
import com.example.formrenderer.model.CanonicalFormMessage;
import com.example.formrenderer.model.fields.*;
import com.example.formrenderer.model.layout.*;
import java.util.List;
import java.util.Map;

// ... in your service class
@Autowired
private JsonFormatRenderer renderer;

public String getKycForm() {
    // 1. Define the fields (the "data dictionary")
    SimpleField occupationField = SimpleField.builder()
        .type("string")
        .widget("text")
        .labelKey("kyc.sof.q2.occupation")
        .validation(ValidationRules.builder().required(true).build())
        .build();
        
    SimpleField industryField = SimpleField.builder()
        .type("string")
        .widget("select")
        .labelKey("kyc.sof.q3.industry")
        .optionsKey("industry_types")
        .build();

    // 2. Define the layout
    Group incomeGroup = new Group(
        "kyc.sof.group1.income_title",
        List.of(
            new Row(List.of(
                new FieldRef("occupation"), // Refers to the key in the fields map
                new FieldRef("industry")
            ))
        )
    );

    // 3. Build the root message
    CanonicalFormMessage formMessage = CanonicalFormMessage.builder()
        .schemaVersion("1.0.0")
        .formId("kyc_sof_multi_group")
        .layout(List.of(incomeGroup))
        .fields(Map.of(
            "occupation", occupationField,
            "industry", industryField
        ))
        .build();

    // 4. Render the JSON
    try {
        String jsonOutput = renderer.render(
            formMessage,
            TargetFormat.DYNAMIC_FORM_V1
        );
        // jsonOutput is a validated, schema-compliant JSON string
        return jsonOutput;
        
    } catch (RenderingException e) {
        // Handle validation or mapping errors
        log.error("Failed to render form: {}", e.getMessage());
        e.getDetails().forEach(detail -> 
            log.error("  - {}: {}", detail.jsonPointer(), detail.message())
        );
        throw new MyServiceException("Could not generate form");
    }
}
```

## 2. Versioning Policy
   This library has two types of versions:

1. Library Version (Maven): The pom.xml version (e.g., 1.0.0). Follows SemVer.

- MAJOR: Breaking API changes (e.g., renaming JsonFormatRenderer methods).

- MINOR: Adding new features (e.g., adding a new TargetFormat.DYNAMIC_FORM_V2).

- PATCH: Bug fixes (e.g., fixing a NullPointerException in a mapper).

2. Schema Version (schemaVersion): The version inside the CanonicalFormMessage (e.g., "1.0.0").

- This versions the canonical POJO model itself.

- If you add a new, optional field to ValidationRules, this is a MINOR bump (e.g., 1.1.0).

- If you remove a field or change a type, this is a MAJOR bump (e.g., 2.0.0).

### 3. How to Add a New Target Format (e.g., V2)
   To add support for a new JSON output format (e.g., DYNAMIC_FORM_V2):
   1. Add to TargetFormat.java:
```java
public enum TargetFormat {
    DYNAMIC_FORM_V1("synapse-form-v1.0.0.schema.json"),
    DYNAMIC_FORM_V2("synapse-form-v2.0.0.schema.json"); // New
    // ...
}
```
2. Add JSON Schema: Create the new src/main/resources/dynamic-form-v2.0.0.schema.json file.
3. 3. Create Mapper: Create a new DynamicFormV2Mapper.java that implements CanonicalToTargetMapper. This mapper will contain the logic to transform the CanonicalFormMessage (which is still v1) into the new V2 JSON structure.
4. 4. Register Mapper: In FormRendererConfig.java, add the new mapper to the map.
```java
@Bean
public JsonFormatRenderer jsonFormatRenderer() {
    // ...
    Map<TargetFormat, CanonicalToTargetMapper> mappers = Map.of(
        TargetFormat.DYNAMIC_FORM_V1, dynamicFormV1Mapper(objectMapper),
        TargetFormat.DYNAMIC_FORM_V2, dynamicFormV2Mapper(objectMapper) // New
    );
    return new DefaultJsonFormatRenderer(validator, schemaValidator, mappers);
}
```

The library will now automatically load the V2 schema and use the V2 mapper when a caller requests TargetFormat.DYNAMIC_FORM_V2.