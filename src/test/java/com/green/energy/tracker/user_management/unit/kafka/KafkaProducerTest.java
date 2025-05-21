package com.green.energy.tracker.user_management.unit.kafka;

import com.green.energy.tracker.configuration.domain.event.UserEventPayload;
import com.green.energy.tracker.user_management.kafka.KafkaProducer;
import com.green.energy.tracker.user_management.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.concurrent.CompletableFuture;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {
    @Mock
    private KafkaTemplate<String, UserEventPayload> avroKafkaTemplate;
    @Mock
    CompletableFuture<SendResult<String,UserEventPayload>> sendResultCompletableFuture;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private User user;
    @Mock
    UserEventPayload userEventPayload;
    @InjectMocks
    KafkaProducer kafkaProducer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kafkaProducer, "topicUserEvents", "test-topic");
    }

    @Test
    void sendMessageSendEventToKafka() throws Exception {
        when(modelMapper.map(user, UserEventPayload.class)).thenReturn(userEventPayload);
        when(userEventPayload.getId()).thenReturn(1L);
        when(avroKafkaTemplate.send("test-topic","1",userEventPayload)).thenReturn(sendResultCompletableFuture);
        kafkaProducer.sendMessage(UserEvent.CREATE, user);
        verify(modelMapper).map(user, UserEventPayload.class);
        verify(avroKafkaTemplate).send("test-topic","1", userEventPayload);
    }
}
