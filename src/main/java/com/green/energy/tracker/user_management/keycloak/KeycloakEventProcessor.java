package com.green.energy.tracker.user_management.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.energy.tracker.user_management.kafka.KafkaProducer;
import com.green.energy.tracker.user_management.model.User;
import com.green.energy.tracker.user_management.model.UserEvent;
import com.green.energy.tracker.user_management.service.authserver.AuthServerEventProcessor;
import com.green.energy.tracker.user_management.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class KeycloakEventProcessor implements AuthServerEventProcessor {

    private final UserService userService;
    private final KeycloakUserService keycloakUserService;
    private final ObjectMapper mapper;
    private final KafkaProducer kafkaProducer;

    @Override
    public void handleEvent(String event) throws JsonProcessingException {
        KeycloakEvent keycloakEvent = mapper.readValue(event, KeycloakEvent.class);
        User user = getUser(keycloakEvent);
        UserEvent userEvent = getUserEvent(keycloakEvent);
        userService.handleUserEvent(userEvent,user);
        kafkaProducer.sendMessage(userEvent,user);
    }

    private User getUser(KeycloakEvent keycloakEvent) throws JsonProcessingException {
        return keycloakUserService.getUser(keycloakEvent)
                .orElseThrow(() -> new RuntimeException("User not found for event: " + keycloakEvent));
    }

    private UserEvent getUserEvent(KeycloakEvent keycloakEvent) {
        return keycloakUserService.getUserEvent(keycloakEvent)
                .orElseThrow(() -> new RuntimeException("UserEvent not found for event: " + keycloakEvent));
    }

}
