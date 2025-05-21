package com.green.energy.tracker.user_management.unit.kafka;

import com.green.energy.tracker.user_management.kafka.KafkaProducerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class KafkaProducerConfigTest {
    @InjectMocks
    KafkaProducerConfig kafkaProducerConfig;

    @BeforeEach
    void setUp(){
        ReflectionTestUtils.setField(kafkaProducerConfig, "bootstrapServers","broker:9092");
        ReflectionTestUtils.setField(kafkaProducerConfig, "schemaRegistryUrl","http://schema-registry:8081");
        ReflectionTestUtils.setField(kafkaProducerConfig, "producerAvroKeySerializer","org.apache.kafka.common.serialization.StringSerializer");
        ReflectionTestUtils.setField(kafkaProducerConfig, "producerAvroValueSerializer","io.confluent.kafka.serializers.KafkaAvroSerializer");
        ReflectionTestUtils.setField(kafkaProducerConfig, "producerDltKeySerializer","org.apache.kafka.common.serialization.StringSerializer");
        ReflectionTestUtils.setField(kafkaProducerConfig, "producerDltValueSerializer","org.apache.kafka.common.serialization.ByteArraySerializer");
    }

    @Test
    void testAvroProducerFactory(){
        assertNotNull(kafkaProducerConfig.avroProducerFactory());
    }

    @Test
    void testAvroKafkaTemplate(){
        assertNotNull(kafkaProducerConfig.avroKafkaTemplate());
    }

    @Test
    void testDltProducerFactory(){
        assertNotNull(kafkaProducerConfig.dltProducerFactory());
    }

    @Test
    void testDltKafkaTemplate(){
        assertNotNull(kafkaProducerConfig.dltKafkaTemplate());
    }
}
