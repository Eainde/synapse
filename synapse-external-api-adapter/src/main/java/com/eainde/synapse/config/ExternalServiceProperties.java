package com.eainde.synapse.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Maps the `external.services` properties from application.properties
 * into a type-safe object.
 */
@Configuration
@ConfigurationProperties(prefix = "external.services")
@Validated
@Data // Lombok annotation for getters/setters/toString
public class ExternalServiceProperties {

    @NotNull
    private ServiceConfig userApi;

    @NotNull
    private ServiceConfig weatherApi;

    @Data
    public static class ServiceConfig {
        @NotBlank
        private String baseUrl;
        private int timeoutSeconds = 10; // Default timeout
    }
}
