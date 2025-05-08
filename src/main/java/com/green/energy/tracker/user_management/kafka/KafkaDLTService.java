package com.green.energy.tracker.user_management.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaDLTService {

    @Value("${spring.kafka.topic.user-events-dlt}")
    private String userEventsTopicDLT;
    private final KafkaTemplate<String, KafkaDLTRecord> kafkaTemplate;

    public <K, V> void sendToDlt(Exception exception, String topic, K key, V payload){
        log.error("Sending record to DLQ due to exception");
        KafkaDLTRecord dlqRecord = KafkaDLTRecord.builder()
                .topic(topic)
                .key(key != null ? key.toString() : null)
                .value(payload != null ? payload.toString() : "")
                .errorMessage(exception.getMessage())
                .timestamp(System.currentTimeMillis())
                .build();
        kafkaTemplate.send(userEventsTopicDLT, dlqRecord.getKey(), dlqRecord);
    }
}
