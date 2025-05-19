package com.green.energy.tracker.user_management.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.energy.tracker.user_management.model.User;
import com.green.energy.tracker.user_management.model.UserEvent;
import java.util.Arrays;
import java.util.Optional;

public final class KeycloakUtil {

    private KeycloakUtil() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    public static Optional<User> getUser(KeycloakEvent keycloakEvent) throws JsonProcessingException {
        if(isEventPermitted(keycloakEvent))
            return Optional.of(getUserFromRepresentation(keycloakEvent));
        return Optional.empty();
    }

    public static Optional<UserEvent> getUserEvent(KeycloakEvent keycloakEvent) {
        if(isEventPermitted(keycloakEvent))
            return Optional.of(UserEvent.valueOf(keycloakEvent.getOperationType().toUpperCase()));
        return Optional.empty();
    }

    public static boolean isEventPermitted(KeycloakEvent keycloakEvent) {
        boolean isResourceTypePermitted = keycloakEvent.getResourceType().equalsIgnoreCase(User.class.getSimpleName());
        boolean isOperationTypePermitted = Arrays.stream(UserEvent.values())
                .map(UserEvent::name)
                .anyMatch(userEvent->userEvent.equals(keycloakEvent.getOperationType().toUpperCase()));
        return isOperationTypePermitted && isResourceTypePermitted;
    }

    private static User getUserFromRepresentation(KeycloakEvent keycloakEvent) throws JsonProcessingException {
        String representation = keycloakEvent.getRepresentation().replaceAll("^\"|\"$", "");
        JsonNode repNode = new ObjectMapper().readTree(representation);
        return User.builder()
                .email(repNode.path(KeycloakRepresentationKey.EMAIL.getField()).asText())
                .enabled(repNode.path(KeycloakRepresentationKey.ENABLED.getField()).asBoolean())
                .firstName(repNode.path(KeycloakRepresentationKey.FIRSTNAME.getField()).asText())
                .lastName(repNode.path(KeycloakRepresentationKey.LASTNAME.getField()).asText())
                .username(repNode.path(KeycloakRepresentationKey.USERNAME.getField()).asText())
                .realmId(keycloakEvent.getRealmId())
                .build();
    }
}
