package com.green.energy.tracker.user_management.kafka;

import com.green.energy.tracker.configuration.domain.event.UserEventPayload;
import com.green.energy.tracker.user_management.model.User;
import com.green.energy.tracker.user_management.model.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
    @Value("${spring.kafka.topic.user-events}")
    private String topicUserEvents;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, UserEventPayload> kafkaTemplate;

    public void sendMessage(UserEvent userEvent, User user){
        UserEventPayload userEventPayload = modelMapper.map(user,UserEventPayload.class);
        userEventPayload.setEventType(userEvent.name());
        kafkaTemplate.send(topicUserEvents,userEventPayload);
    }
}
