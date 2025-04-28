package com.green.energy.tracker.user_management.kafka;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.errors.DeserializationExceptionHandler;
import org.apache.kafka.streams.errors.ProductionExceptionHandler;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
@NoArgsConstructor
public class KafkaStreamsExceptionHandler {

    @Value("${spring.kafka.topic.user-events-dlq}")
    private String userEventsTopicDLQ;
    private KafkaTemplate<String, KafkaDlqRecord> kafkaTemplate;

    public <K, V> void sendToDlq(Throwable throwable,String topic, K key, V value) {
        log.error("Sending record to DLQ due to exception", throwable);
        KafkaDlqRecord dlqRecord = KafkaDlqRecord.builder()
                .topic(topic)
                .key(Objects.nonNull(key) ? key.toString() : null)
                .value(Objects.nonNull(value) ? value.toString() : "")
                .errorMessage(throwable.getMessage())
                .timestamp(new Date().getTime())
                .build();
        kafkaTemplate.send(userEventsTopicDLQ, dlqRecord.getKey(), dlqRecord);
    }

    @Component
    public class CustomDeserializationProductionHandler implements DeserializationExceptionHandler, ProductionExceptionHandler {

        @Override
        public DeserializationHandlerResponse handle(ProcessorContext processorContext, ConsumerRecord<byte[], byte[]> consumerRecord, Exception exception) {
            log.error("Deserialization error for message with key: {}", new String(consumerRecord.key()), exception);
            sendToDlq(exception, consumerRecord.topic(), consumerRecord.key(), consumerRecord.value());
            return DeserializationHandlerResponse.CONTINUE;
        }

        @Override
        public void configure(Map<String, ?> configs) {

        }

        @Override
        public ProductionExceptionHandlerResponse handle(ProducerRecord<byte[], byte[]> producerRecord, Exception e) {
            log.error("Production error for message with key: {}", new String(producerRecord.key(), StandardCharsets.UTF_8), e);
            sendToDlq(e, producerRecord.topic(), producerRecord.key(), producerRecord.value());
            return ProductionExceptionHandlerResponse.CONTINUE;
        }

        @Override
        public ProductionExceptionHandlerResponse handleSerializationException(ProducerRecord record, Exception exception) {
            log.error("Serialization error for message with key: {}", record.key(), exception);
            sendToDlq(exception, record.topic(), record.key(), record.value());
            return ProductionExceptionHandlerResponse.CONTINUE;
        }
    }

}
