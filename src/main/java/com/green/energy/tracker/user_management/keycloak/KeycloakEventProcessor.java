package com.green.energy.tracker.user_management.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.green.energy.tracker.user_management.kafka.KafkaProducer;
import com.green.energy.tracker.user_management.model.User;
import com.green.energy.tracker.user_management.model.UserEvent;
import com.green.energy.tracker.user_management.service.authserver.AuthServerEventProcessor;
import com.green.energy.tracker.user_management.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakEventProcessor implements AuthServerEventProcessor {

    private final UserService userService;
    private final KeycloakUserService keycloakUserService;
    private final KafkaProducer kafkaProducer;
    private final ModelMapper modelMapper;

    @Override
    public void handleEvent(GenericRecord authServerEvent) throws JsonProcessingException {
        KeycloakEvent keycloakEvent = deserializeGenericRecord(authServerEvent);
        log.info("keycloakEvent: {}", keycloakEvent);
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

    private KeycloakEvent deserializeGenericRecord(GenericRecord authServerEvent){
        log.info("JSON EVENT processing");
        authServerEvent.getSchema().getFields().forEach(field -> log.info(field.name()));
        return modelMapper.map(authServerEvent.getSchema().getFields().stream()
                .map(Schema.Field::name)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(key-> key, authServerEvent::get)),KeycloakEvent.class);
    }
}
