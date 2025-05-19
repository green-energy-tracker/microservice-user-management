package com.green.energy.tracker.user_management.keycloak;

import lombok.*;

@RequiredArgsConstructor
@Getter
public enum KeycloakRepresentationKey {
    EMAIL("email"),
    ENABLED("enabled"),
    FIRSTNAME("firstName"),
    LASTNAME("lastName"),
    USERNAME("username");

    final String field;
}
