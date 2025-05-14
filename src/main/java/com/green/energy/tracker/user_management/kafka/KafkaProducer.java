package com.green.energy.tracker.user_management.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.energy.tracker.configuration.domain.event.UserEventPayload;
import com.green.energy.tracker.user_management.model.User;
import com.green.energy.tracker.user_management.model.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
    @Value("${spring.kafka.topic.user-events}")
    private String topicUserEvents;
    @Value("${spring.kafka.topic.user-events-dlt}")
    private String topicUserEventsDlt;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, UserEventPayload> avroKafkaTemplate;
    private final KafkaTemplate<String, String> dltKafkaTemplate;

    public void sendMessage(UserEvent userEvent, User user){
        UserEventPayload userEventPayload = modelMapper.map(user, UserEventPayload.class);
        try {
            userEventPayload.setEventType(userEvent.name());
            avroKafkaTemplate.send(topicUserEvents, String.valueOf(userEventPayload.getId()), userEventPayload);
        } catch (KafkaException e){
            handleSendFailure(String.valueOf(userEventPayload.getId()),userEventPayload,e);
        }
    }

    private void handleSendFailure(String key, Object payload, Throwable ex) {
        log.error("Error during serialization/sending to Kafka: {}", ex.getMessage());
        try {
            String fallbackPayload = objectMapper.writeValueAsString(payload);
            dltKafkaTemplate.send(topicUserEventsDlt, key, fallbackPayload);
            log.warn("Message sent to dlt topic: {}", topicUserEventsDlt);
        } catch (JsonProcessingException e) {
            log.error("Failed to send message on DLT topic : {}", e.getMessage());
        }
    }
}
