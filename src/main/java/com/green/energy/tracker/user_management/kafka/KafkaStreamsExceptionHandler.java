package com.green.energy.tracker.user_management.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaStreamsExceptionHandler {

    @Value("${spring.kafka.topic.user-events-dlq}")
    private String userEventsTopicDLQ;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public <K, V> void sendToDlq(Throwable throwable,String topic, K key, V value) {
        log.error("Sending record to DLQ due to exception", throwable);
        KafkaDlqRecord dlqRecord = KafkaDlqRecord.builder()
                .topic(topic)
                .key(Objects.nonNull(key) ? key.toString() : null)
                .value(Objects.nonNull(value) ? value.toString() : "")
                .errorMessage(throwable.getMessage())
                .timestamp(new Date().getTime())
                .build();
        try {
            String dlqPayload = objectMapper.writeValueAsString(dlqRecord);
            kafkaTemplate.send(userEventsTopicDLQ, dlqRecord.getKey(), dlqPayload);
        } catch (Exception e) {
            log.error("Failed to send record to DLQ", e);
        }
    }

}
