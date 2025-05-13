package com.green.energy.tracker.user_management.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.green.energy.tracker.user_management.keycloak.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class kafkaConsumer {

    private final KeycloakEventProcessor keycloakEventProcessor;

    @KafkaListener(topics = "${spring.kafka.topic.auth-server-events}")
    public void consumeEvent(ConsumerRecord<String,KeycloakEvent> keycloakEvent) throws JsonProcessingException {
        log.info("Consuming Keycloak event: {}", keycloakEvent);
        keycloakEventProcessor.handleEvent(keycloakEvent);
    }
}
