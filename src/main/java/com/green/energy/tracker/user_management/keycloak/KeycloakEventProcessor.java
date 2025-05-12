package com.green.energy.tracker.user_management.keycloak;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.green.energy.tracker.user_management.kafka.KafkaProducer;
import com.green.energy.tracker.user_management.model.*;
import com.green.energy.tracker.user_management.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.KafkaException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakEventProcessor {

    private final UserService userService;
    private final KafkaProducer kafkaProducer;

    public void handleEvent(ConsumerRecord<String,KeycloakEvent> keycloakEventRecord) {
        try {
            var keycloakEvent = keycloakEventRecord.value();
            log.info("Mapping Keycloak event [{}] to User",keycloakEvent);
            var user = getUser(keycloakEvent);
            log.info("Mapped Keycloak event [{}] to User [{}]",keycloakEvent,user);

            log.info("Mapping Keycloak event [{}] to UserEvent",keycloakEvent);
            var userEvent = getUserEvent(keycloakEvent);
            log.info("Mapped Keycloak event [{}] to UserEvent [{}]",keycloakEvent,userEvent);

            log.info("Start DB operations on entity USER");
            switch (userEvent){
                case CREATE -> userService.save(user);
                case UPDATE -> userService.update(user);
                case DELETE -> userService.delete(user);
            }
            log.info("End DB operations on entity USER");
            //kafkaProducer.sendMessage(userEvent,user);
        } catch (JsonProcessingException e) {
            throw new KafkaException(e);
        }
    }

    private User getUser(KeycloakEvent keycloakEvent) throws JsonProcessingException {
        return KeycloakUtil.getUser(keycloakEvent)
                .orElseThrow(() -> new JsonParseException("User not found for event: " + keycloakEvent));
    }

    private UserEvent getUserEvent(KeycloakEvent keycloakEvent) throws JsonProcessingException {
        return KeycloakUtil.getUserEvent(keycloakEvent)
                .orElseThrow(() -> new JsonParseException("UserEvent not found for event: " + keycloakEvent));
    }
}
