package com.green.energy.tracker.user_management.unit.keycloak;

import com.fasterxml.jackson.core.JsonParseException;
import com.green.energy.tracker.user_management.kafka.KafkaProducer;
import com.green.energy.tracker.user_management.keycloak.*;
import com.green.energy.tracker.user_management.model.*;
import com.green.energy.tracker.user_management.service.UserService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakEventProcessorTest {
    @Mock
    private UserService userService;
    @Mock
    private KafkaProducer kafkaProducer;
    @Mock
    private KeycloakEvent keycloakEvent;
    @Mock
    private ConsumerRecord<String, KeycloakEvent> consumerRecord;
    @Mock
    User user;
    @InjectMocks
    private KeycloakEventProcessor keycloakEventProcessor;

    @Test
    void handleEventCreateUserAndSendMessageWhenUserEventIsCreate() throws Exception {
        try (var mockedStatic = mockStatic(KeycloakUtil.class)) {
            when(consumerRecord.value()).thenReturn(keycloakEvent);
            mockedStatic.when(() -> KeycloakUtil.getUser(keycloakEvent)).thenReturn(Optional.of(user));
            mockedStatic.when(() -> KeycloakUtil.getUserEvent(keycloakEvent)).thenReturn(Optional.of(UserEvent.CREATE));
            keycloakEventProcessor.handleEvent(consumerRecord);
            verify(userService).create(user);
            verify(kafkaProducer).sendMessage(UserEvent.CREATE, user);
        }
    }

    @Test
    void handleEventCreateUserAndSendMessageWhenUserEventIsDelete() throws Exception {
        try (var mockedStatic = mockStatic(KeycloakUtil.class)) {
            when(consumerRecord.value()).thenReturn(keycloakEvent);
            mockedStatic.when(() -> KeycloakUtil.getUser(keycloakEvent)).thenReturn(Optional.of(user));
            mockedStatic.when(() -> KeycloakUtil.getUserEvent(keycloakEvent)).thenReturn(Optional.of(UserEvent.DELETE));
            keycloakEventProcessor.handleEvent(consumerRecord);
            verify(userService).delete(user);
            verify(kafkaProducer).sendMessage(UserEvent.DELETE, user);
        }
    }

    @Test
    void handleEventCreateUserAndSendMessageWhenUserEventIsOther() throws Exception {
        try (var mockedStatic = mockStatic(KeycloakUtil.class)) {
            when(consumerRecord.value()).thenReturn(keycloakEvent);
            mockedStatic.when(() -> KeycloakUtil.getUser(keycloakEvent)).thenReturn(Optional.of(user));
            mockedStatic.when(() -> KeycloakUtil.getUserEvent(keycloakEvent)).thenReturn(Optional.of(UserEvent.UPDATE));
            keycloakEventProcessor.handleEvent(consumerRecord);
            verify(userService).update(user);
            verify(kafkaProducer).sendMessage(UserEvent.UPDATE, user);
        }
    }

    @Test
    void handleEventThrowExceptionWhenUserIsNotFound() {
        try (var mockedStatic = mockStatic(KeycloakUtil.class)) {
            when(consumerRecord.value()).thenReturn(keycloakEvent);
            mockedStatic.when(() -> KeycloakUtil.getUser(keycloakEvent)).thenReturn(Optional.empty());
            assertThrows(JsonParseException.class, () -> {keycloakEventProcessor.handleEvent(consumerRecord);});
        }
    }

}
