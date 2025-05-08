package com.green.energy.tracker.user_management.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.errors.DeserializationExceptionHandler;
import org.apache.kafka.streams.errors.ProductionExceptionHandler;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@Slf4j
public class KafkaStreamsExceptionHandler implements DeserializationExceptionHandler, ProductionExceptionHandler {

    @Autowired
    private static KafkaDLTService kafkaDLTService;

    @Autowired
    public void setKafkaDLTService(KafkaDLTService kafkaDLTService) {
        KafkaStreamsExceptionHandler.kafkaDLTService = kafkaDLTService;
    }

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
            kafkaDLTService.sendToDlt(exception,producerRecord.topic(),producerRecord.key(),producerRecord.value());
        else if(record instanceof ConsumerRecord<?, ?> consumerRecord)
            kafkaDLTService.sendToDlt(exception,consumerRecord.topic(),consumerRecord.key(),consumerRecord.value());
    }

    public <K, V> void sendToDltBusinessException(Exception exception,String topic, K key, V payload){
        kafkaDLTService.sendToDlt(exception,topic,key,payload);
    }

}
