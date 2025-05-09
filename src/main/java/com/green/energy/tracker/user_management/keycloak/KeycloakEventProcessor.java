package com.green.energy.tracker.user_management.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.avro.AvroMapper;
import com.green.energy.tracker.user_management.kafka.KafkaProducer;
import com.green.energy.tracker.user_management.model.User;
import com.green.energy.tracker.user_management.model.UserEvent;
import com.green.energy.tracker.user_management.service.authserver.AuthServerEventProcessor;
import com.green.energy.tracker.user_management.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakEventProcessor implements AuthServerEventProcessor {

    private final UserService userService;
    private final KeycloakUserService keycloakUserService;
    private final ObjectMapper mapper;
    private final KafkaProducer kafkaProducer;
    private final AvroMapper avroMapper;

    @Override
    public void handleEvent(GenericRecord authServerEvent) throws JsonProcessingException {
        KeycloakEvent keycloakEvent = deserializeSpecificRecord(authServerEvent);
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

    private KeycloakEvent deserializeSpecificRecord(GenericRecord authServerEvent) throws JsonProcessingException {
        log.info("JSON EVENT processing");
        String jsonEvent = avroMapper.writerFor(GenericRecord.class).writeValueAsString(authServerEvent);
        log.info("JSON EVENT: {}",jsonEvent);
        return mapper.readValue(jsonEvent, KeycloakEvent.class);
    }

}
