package com.green.energy.tracker.user_management.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.energy.tracker.configuration.domain.event.UserEventPayload;
import com.green.energy.tracker.user_management.model.User;
import com.green.energy.tracker.user_management.model.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
    @Value("${spring.kafka.topic.user-events}")
    private String topicUserEvents;
    private final KafkaTemplate<String, UserEventPayload> avroKafkaTemplate;
    private final ModelMapper modelMapper;

    public void sendMessage(UserEvent userEvent, User user) throws ExecutionException, InterruptedException {
        UserEventPayload userEventPayload = modelMapper.map(user, UserEventPayload.class);
        userEventPayload.setEventType(userEvent.name());
        avroKafkaTemplate.send(topicUserEvents, String.valueOf(userEventPayload.getId()), userEventPayload).get();
    }
}
