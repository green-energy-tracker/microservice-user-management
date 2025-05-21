package com.green.energy.tracker.user_management.unit.kafka;

import com.green.energy.tracker.user_management.kafka.*;
import com.green.energy.tracker.user_management.keycloak.KeycloakEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.*;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.function.BiFunction;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class KafkaErrorHandlerConfigTest {
    @Mock
    private BiFunction<ConsumerRecord<?,?>, Exception, TopicPartition> dltDestinationResolver;
    @Mock
    private KafkaTemplate<String, DltRecord> dltKafkaTemplate;
    @Mock
    private DeadLetterPublishingRecoverer deadLetterPublishingRecoverer;
    @Mock
    private ConsumerFactory<String, KeycloakEvent> consumerFactory;
    @Mock
    private DefaultErrorHandler defaultErrorHandler;
    @Mock
    private ConsumerRecord<String,KeycloakEvent> consumerRecord;
    @InjectMocks
    private KafkaErrorHandlerConfig kafkaErrorHandlerConfig;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kafkaErrorHandlerConfig, "topicUserEventsDlt", "test-topic-dlt");
    }

    @Test
    void testDefaultErrorHandler(){
        var defaultErrorHandler = kafkaErrorHandlerConfig.defaultErrorHandler(deadLetterPublishingRecoverer);
        assertNotNull(defaultErrorHandler);
    }

    @Test
    void testKafkaListenerContainerFactory(){
        var factory = kafkaErrorHandlerConfig.kafkaListenerContainerFactory(consumerFactory,defaultErrorHandler);
        assertNotNull(factory);
        assertEquals(factory.getConsumerFactory(),consumerFactory);
    }

    @Test
    void testDeadLetterRecover(){
        var deadLetterRecover = kafkaErrorHandlerConfig.deadLetterRecover(dltKafkaTemplate,dltDestinationResolver);
        assertNotNull(deadLetterRecover);
    }

    @Test
    void testDltDestinationResolver(){
        var dltDestinationResolver = kafkaErrorHandlerConfig.dltDestinationResolver(dltKafkaTemplate);
        assertNotNull(dltDestinationResolver);
        var topicPartition = dltDestinationResolver.apply(consumerRecord,new RuntimeException(new Throwable()));
        assertNull(topicPartition);
    }
}
