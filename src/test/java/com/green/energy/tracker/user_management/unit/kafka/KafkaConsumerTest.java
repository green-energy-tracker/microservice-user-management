package com.green.energy.tracker.user_management.unit.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.green.energy.tracker.user_management.kafka.KafkaConsumer;
import com.green.energy.tracker.user_management.keycloak.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerTest {

    @Mock
    private ConsumerRecord<String, KeycloakEvent> keycloakEvent;
    @Mock
    private KeycloakEventProcessor keycloakEventProcessor;
    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    @Test
    void testConsumeEventWithoutException() throws Exception {
        kafkaConsumer.consumeEvent(keycloakEvent);
        verify(keycloakEventProcessor).handleEvent(keycloakEvent);
    }

    @Test
    void testConsumeEventPropagatesExceptions() throws Exception {
        doThrow(new JsonProcessingException("test") {}).when(keycloakEventProcessor).handleEvent(keycloakEvent);
        assertThrows(JsonProcessingException.class,() -> kafkaConsumer.consumeEvent(keycloakEvent));
    }
}
