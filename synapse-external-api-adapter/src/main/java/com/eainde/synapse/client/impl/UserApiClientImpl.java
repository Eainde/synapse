package com.eainde.synapse.client.impl;

import com.eainde.synapse.client.UserApiClient;
import com.eainde.synapse.config.ExternalServiceProperties;
import com.eainde.synapse.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * This is the IMPLEMENTATION.
 * It implements the UserApiClient interface using WebClient.
 * This class is what actually performs the external API call.
 */
@Service // This registers it as a Spring bean
public class UserApiClientImpl implements UserApiClient {

    private static final Logger log = LoggerFactory.getLogger(UserApiClientImpl.class);
    private final WebClient webClient;

    @Autowired
    public UserApiClientImpl(
            WebClient.Builder webClientBuilder, // We get the pre-configured builder
            ExternalServiceProperties properties // We get our properties object
    ) {
        // Create a service-specific WebClient
        ExternalServiceProperties.ServiceConfig userApiConfig = properties.getUserApi();
        String baseUrl = userApiConfig.getBaseUrl();

        // Create a service-specific HttpClient for custom timeouts
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(userApiConfig.getTimeoutSeconds()));

        this.webClient = webClientBuilder
                .clone() // Clone the builder to not modify the original bean
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        log.info("Initialized UserApiClient with base URL: {}", baseUrl);
    }

    @Override
    public Mono<UserDTO> findUserById(String userId) {
        return this.webClient.get()
                .uri("/users/{id}", userId) // Appends to the base URL
                .retrieve()
                // Handle 404 (Not Found) gracefully
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND),
                        clientResponse -> Mono.empty())
                .bodyToMono(UserDTO.class)
                // Handle any other errors
                .doOnError(e -> log.error("Failed to fetch user with id {}: {}", userId, e.getMessage()))
                .onErrorResume(e -> Mono.empty()); // Return empty if an error occurs
    }
}

