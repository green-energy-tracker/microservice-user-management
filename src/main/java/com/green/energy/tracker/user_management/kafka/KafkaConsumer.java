package com.green.energy.tracker.user_management.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.green.energy.tracker.user_management.keycloak.KeycloakEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final KeycloakEventProcessor authServerEventProcessor;

    @KafkaListener(
            topics = "${spring.kafka.topic.auth-server-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(GenericRecord authServerEvent) throws JsonProcessingException {
        log.info(authServerEvent.toString());
        //authServerEventProcessor.handleEvent(authServerEvent);
    }
}
