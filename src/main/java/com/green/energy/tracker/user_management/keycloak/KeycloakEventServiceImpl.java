package com.green.energy.tracker.user_management.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.green.energy.tracker.user_management.model.*;
import com.green.energy.tracker.user_management.service.AuthServerEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import static com.green.energy.tracker.user_management.keycloak.KeycloakEventKeys.*;

@RequiredArgsConstructor
@Service("keycloakEventServiceV1")
public class KeycloakEventServiceImpl implements AuthServerEventService {

    private final ObjectMapper objectMapper;

    @Override
    public Map<UserEvent,User> eventToUser(String event) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(event);
        if(!isEventPermitted(root))
            return Collections.emptyMap();
        UserEvent userEvent = UserEvent.valueOf(root.path(OPERATION_TYPE).asText().toUpperCase());
        User user = getUserFromRepresentation(root);
        return Map.of(userEvent,user);
    }

    private boolean isEventPermitted(JsonNode root){
        boolean isResourceTypePermitted = root.path(RESOURCE_TYPE).asText().equalsIgnoreCase(User.class.getSimpleName());
        boolean isOperationTypePermitted = Arrays.stream(UserEvent.values())
                .map(UserEvent::name)
                .anyMatch(userEvent->userEvent.equals(root.path(OPERATION_TYPE).asText().toUpperCase()));
        return isOperationTypePermitted && isResourceTypePermitted;
    }

    private User getUserFromRepresentation(JsonNode root) throws JsonProcessingException {
        String representation = root.path(REPRESENTATION).asText().replaceAll("^\"|\"$", "");
        JsonNode repNode = objectMapper.readTree(representation);
        return User.builder()
                .email(repNode.path(EMAIL).asText())
                .enabled(repNode.path(ENABLED).asBoolean())
                .firstName(repNode.path(FIRST_NAME).asText())
                .lastName(repNode.path(LAST_NAME).asText())
                .realmId(getRealmId(root))
                .username(repNode.path(USERNAME).asText())
                .build();
    }

    private String getRealmId(JsonNode root){
        return root.path(REALM_ID).asText();
    }

}
