package com.green.energy.tracker.user_management.keycloak;

import com.green.energy.tracker.configuration.domain.event.KeycloakAdminEventDto;
import com.green.energy.tracker.user_management.service.authserver.AuthServerConsumerKafka;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakKafkaConsumer{ //implements AuthServerConsumerKafka<KeycloakAdminEventDto> {

    private final KeycloakEventProcessor keycloakEventProcessor;

    @KafkaListener(
            topics = "${spring.kafka.topic.auth-server-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeEvent(ConsumerRecord<Object, Object> record) {
        Object value = record.value();

        System.out.println("==== Tipo messaggio: " + value.getClass().getName());

        if (value instanceof GenericRecord) {
            System.out.println("Messaggio è un GenericRecord.");
            GenericRecord genericRecord = (GenericRecord) value;
        } else {
            System.out.println("Messaggio è un record Avro specifico.");
            System.out.println("Contenuto: " + value);
        }
        //log.info(keycloakAdminEventDto.toString());
        //keycloakEventProcessor.handleEvent(keycloakAdminEventDto);
    }
}
