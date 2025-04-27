package com.green.energy.tracker.user_management.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaStreamsExceptionHandler {

    @Value("${spring.kafka.topic.user-events-dlq}")
    private String userEventsTopicDLQ;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse handleUncaught(Throwable throwable) {
        log.error("Uncaught Exception user event stream", throwable);
        kafkaTemplate.send(userEventsTopicDLQ,throwable.getMessage());
        return StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.REPLACE_THREAD;
    }

}
