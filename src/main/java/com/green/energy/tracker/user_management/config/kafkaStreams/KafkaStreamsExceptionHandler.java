package com.green.energy.tracker.user_management.config.kafkaStreams;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.errors.DeserializationExceptionHandler;
import org.apache.kafka.streams.errors.ProductionExceptionHandler;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaStreamsExceptionHandler implements DeserializationExceptionHandler, ProductionExceptionHandler {

    @Value("${spring.kafka.topic.user-events-dlq}")
    private String userEventsTopicDLQ;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public DeserializationHandlerResponse handle(ProcessorContext processorContext, ConsumerRecord<byte[], byte[]> consumerRecord, Exception e) {
        log.error("Deserialization Exception: {} for record: {}", e.getMessage(), consumerRecord, e);
        publishError("Deserialization", e.getMessage(), consumerRecord.toString());
        return DeserializationHandlerResponse.CONTINUE;
    }

    @Override
    public ProductionExceptionHandlerResponse handle(ProducerRecord<byte[], byte[]> producerRecord, Exception e) {
        log.error("Production Exception: {} for record: {}", e.getMessage(), producerRecord, e);
        publishError("Production", e.getMessage(), producerRecord.toString());
        return ProductionExceptionHandlerResponse.CONTINUE;
    }

    public StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse handleUncaught(Throwable e) {
        log.error("Uncaught Exception in Stream Thread: {}", e.getMessage(), e);
        publishError("Uncaught", e.getMessage(), "No record context");
        return StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.REPLACE_THREAD;
    }

    @Override
    public void configure(Map<String, ?> map) {
        log.info("KafkaStreamsExceptionHandler configured with properties: {}", map);
    }

    private void publishError(String type, String errorMessage, String recordInfo) {
        KafkaErrorEvent errorEvent = new KafkaErrorEvent(type, errorMessage, recordInfo);
        kafkaTemplate.send(userEventsTopicDLQ,recordInfo);
    }



}
