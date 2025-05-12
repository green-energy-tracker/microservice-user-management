package com.green.energy.tracker.user_management.keycloak;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.green.energy.tracker.configuration.domain.event.KeycloakAdminEventDto;
import com.green.energy.tracker.user_management.kafka.KafkaProducer;
import com.green.energy.tracker.user_management.model.User;
import com.green.energy.tracker.user_management.model.UserEvent;
import com.green.energy.tracker.user_management.service.authserver.AuthServerEventProcessor;
import com.green.energy.tracker.user_management.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakEventProcessor implements AuthServerEventProcessor<KeycloakAdminEventDto> {

    private final UserService userService;
    private final KeycloakUserService keycloakUserService;
    private final KafkaProducer kafkaProducer;

    @Override
    public void handleEvent(KeycloakAdminEventDto keycloakAdminEventDto) {
        try {
            User user = getUser(keycloakAdminEventDto);
            UserEvent userEvent = getUserEvent(keycloakAdminEventDto);
            userService.handleUserEvent(userEvent,user);
            //kafkaProducer.sendMessage(userEvent,user);
        } catch (JsonProcessingException e) {
            throw new KafkaException(e);
        }
    }

    private User getUser(KeycloakAdminEventDto keycloakAdminEventDto) throws JsonProcessingException {
        return keycloakUserService.getUser(keycloakAdminEventDto)
                .orElseThrow(() -> new JsonParseException("User not found for event: " + keycloakAdminEventDto));
    }

    private UserEvent getUserEvent(KeycloakAdminEventDto keycloakAdminEventDto) throws JsonProcessingException {
        return keycloakUserService.getUserEvent(keycloakAdminEventDto)
                .orElseThrow(() -> new JsonParseException("UserEvent not found for event: " + keycloakAdminEventDto));
    }
}
