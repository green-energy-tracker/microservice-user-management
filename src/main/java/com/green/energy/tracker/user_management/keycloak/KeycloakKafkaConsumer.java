package com.green.energy.tracker.user_management.keycloak;

import com.green.energy.tracker.configuration.domain.event.KeycloakAdminEventDto;
import com.green.energy.tracker.user_management.service.authserver.AuthServerConsumerKafka;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakKafkaConsumer implements AuthServerConsumerKafka<KeycloakAdminEventDto> {

    private final KeycloakEventProcessor keycloakEventProcessor;

    @KafkaListener(
            topics = "${spring.kafka.topic.auth-server-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Override
    public void consumeEvent(KeycloakAdminEventDto keycloakAdminEventDto) {
        log.info(keycloakAdminEventDto.toString());
        keycloakEventProcessor.handleEvent(keycloakAdminEventDto);
    }
}
