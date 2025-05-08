package com.green.energy.tracker.user_management.kafka;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.errors.DeserializationExceptionHandler;
import org.apache.kafka.streams.errors.ProductionExceptionHandler;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.*;

@Slf4j
@Component
@NoArgsConstructor
public class KafkaStreamsExceptionHandler implements DeserializationExceptionHandler, ProductionExceptionHandler {

    @Value("${spring.kafka.topic.user-events-dlt}")
    private String userEventsTopicDLT;

    @Autowired
    private KafkaTemplate<String, KafkaDLTRecord> kafkaTemplate;

    @Override
    public DeserializationHandlerResponse handle(ProcessorContext processorContext, ConsumerRecord<byte[], byte[]> consumerRecord, Exception exception) {
        log.error("Deserialization error for message : {}", consumerRecord,exception);
        sendToDltKafkaExceptions(exception, consumerRecord);
        return DeserializationHandlerResponse.CONTINUE;
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }

    @Override
    public ProductionExceptionHandlerResponse handle(ProducerRecord<byte[], byte[]> producerRecord, Exception exception) {
        log.error("Production error for message : {}", producerRecord, exception);
        sendToDltKafkaExceptions(exception, producerRecord);
        return ProductionExceptionHandlerResponse.CONTINUE;
    }

    @Override
    public ProductionExceptionHandlerResponse handleSerializationException(ProducerRecord producerRecord, Exception exception) {
        log.error("Serialization error for message : {}", producerRecord,exception);
        sendToDltKafkaExceptions(exception, producerRecord);
        return ProductionExceptionHandlerResponse.CONTINUE;
    }

    public void sendToDltKafkaExceptions(Exception exception, Object record) {
        if(Objects.isNull(record)) {
            log.error("Record is null, aborted send to DLQ");
            return;
        }
        if(record instanceof ProducerRecord<?, ?> producerRecord)
            sendToDlt(exception,producerRecord.topic(),producerRecord.key(),producerRecord.value());
        else if(record instanceof ConsumerRecord<?, ?> consumerRecord)
            sendToDlt(exception,consumerRecord.topic(),consumerRecord.key(),consumerRecord.value());
    }

    public <K, V> void sendToDltBusinessException(Exception exception,String topic, K key, V payload){
        sendToDlt(exception,topic,key,payload);
    }

    private <K, V> void sendToDlt(Exception exception,String topic, K key, V payload){
        log.error("Sending record to DLQ due to exception");
        KafkaDLTRecord dlqRecord = KafkaDLTRecord.builder()
                .topic(topic)
                .key(Objects.nonNull(key) ? key.toString() : null)
                .value(Objects.nonNull(payload) ? payload.toString() : "")
                .errorMessage(exception.getMessage())
                .timestamp(new Date().getTime())
                .build();
        kafkaTemplate.send(userEventsTopicDLT, dlqRecord.getKey(), dlqRecord);
    }
}
