package com.eainde.synapse.client;

import com.eainde.synapse.dto.UserDTO;
import reactor.core.publisher.Mono;

/**
 * This is the ABSTRACTION.
 * Other modules in our app will inject and use this interface.
 * They will have no idea it's making an HTTP call.
 */
public interface UserApiClient {

    /**
     * Fetches a user by their unique ID.
     * @param userId The ID of the user to fetch.
     * @return A Mono containing the UserDTO, or Mono.empty() if not found.
     */
    Mono<UserDTO> findUserById(String userId);

    // You could add other methods like:
    // Mono<UserDTO> findUserByEmail(String email);
    // Mono<UserDTO> createUser(UserDTO newUser);
}