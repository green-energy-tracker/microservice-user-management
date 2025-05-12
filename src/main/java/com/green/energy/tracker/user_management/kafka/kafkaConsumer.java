package com.green.energy.tracker.user_management.kafka;

import com.green.energy.tracker.user_management.keycloak.KeycloakEvent;
import com.green.energy.tracker.user_management.keycloak.KeycloakEventProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class kafkaConsumer {

    private final KeycloakEventProcessor keycloakEventProcessor;

    public kafkaConsumer(KeycloakEventProcessor keycloakEventProcessor) {
        this.keycloakEventProcessor = keycloakEventProcessor;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.auth-server-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeEvent(ConsumerRecord<String, KeycloakEvent> authServerEvent){
        keycloakEventProcessor.handleEvent(authServerEvent);
    }
}
