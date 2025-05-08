package com.green.energy.tracker.user_management.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.green.energy.tracker.user_management.keycloak.KeycloakEventProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final KeycloakEventProcessor authServerEventProcessor;

    @KafkaListener(
            topics = "${spring.kafka.topic.auth-server-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(String event) throws JsonProcessingException {
        authServerEventProcessor.handleEvent(event);
    }
}
