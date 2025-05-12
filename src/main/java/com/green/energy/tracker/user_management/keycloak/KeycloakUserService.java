package com.green.energy.tracker.user_management.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.energy.tracker.configuration.domain.event.KeycloakAdminEventDto;
import com.green.energy.tracker.user_management.model.User;
import com.green.energy.tracker.user_management.model.UserEvent;
import com.green.energy.tracker.user_management.service.authserver.AuthServerUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KeycloakUserService implements AuthServerUserService<KeycloakAdminEventDto> {

    private final ObjectMapper objectMapper;

    @Override
    public Optional<User> getUser(KeycloakAdminEventDto keycloakAdminEventDto) throws JsonProcessingException {
        if(isEventPermitted(keycloakAdminEventDto))
            return Optional.of(getUserFromRepresentation(keycloakAdminEventDto));
        return Optional.empty();
    }

    @Override
    public Optional<UserEvent> getUserEvent(KeycloakAdminEventDto keycloakAdminEventDto) {
        if(isEventPermitted(keycloakAdminEventDto))
            return Optional.of(UserEvent.valueOf(keycloakAdminEventDto.getOperationType().toUpperCase()));
        return Optional.empty();
    }

    @Override
    public boolean isEventPermitted(KeycloakAdminEventDto keycloakAdminEventDto) {
        boolean isResourceTypePermitted = keycloakAdminEventDto.getResourceType().equalsIgnoreCase(User.class.getSimpleName());
        boolean isOperationTypePermitted = Arrays.stream(UserEvent.values())
                .map(UserEvent::name)
                .anyMatch(userEvent->userEvent.equals(keycloakAdminEventDto.getOperationType().toUpperCase()));
        return isOperationTypePermitted && isResourceTypePermitted;
    }

    private User getUserFromRepresentation(KeycloakAdminEventDto keycloakAdminEventDto) throws JsonProcessingException {
        String representation = keycloakAdminEventDto.getRepresentation().replaceAll("^\"|\"$", "");
        JsonNode repNode = objectMapper.readTree(representation);
        return User.builder()
                .email(repNode.path(KeycloakRepresentationKey.EMAIL.name()).asText())
                .enabled(repNode.path(KeycloakRepresentationKey.ENABLED.name()).asBoolean())
                .firstName(repNode.path(KeycloakRepresentationKey.FIRST_NAME.name()).asText())
                .lastName(repNode.path(KeycloakRepresentationKey.LAST_NAME.name()).asText())
                .username(repNode.path(KeycloakRepresentationKey.USERNAME.name()).asText())
                .realmId(keycloakAdminEventDto.getRealmId())
                .build();
    }
}
