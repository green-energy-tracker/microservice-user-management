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
    @Value("${spring.kafka.topic.user-events-dlt}")
    private String topicUserEventsDlt;
    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaRegistryUrl;
    @Value("${spring.kafka.properties.schema.registry.cache-capacity}")
    private Integer schemaRegistryCacheCapacity;
    private final KafkaTemplate<String, UserEventPayload> kafkaTemplate;
    private final ModelMapper modelMapper;

    public void sendMessage(UserEvent userEvent, User user){
        UserEventPayload userEventPayload = generatePayload(userEvent,user);
        kafkaTemplate.send(topicUserEvents,userEventPayload.getUser().getUsername(),userEventPayload);
    }

    public void sendMessageDlt(){

    }

    private UserEventPayload generatePayload(UserEvent userEvent, User user){
        return UserEventPayload.newBuilder()
                .setUser(com.green.energy.tracker.configuration.domain.event.User.newBuilder()
                        .setId(user.getId())
                        .setEmail(user.getEmail())
                        .setEnabled(user.isEnabled())
                        .setFirstName(user.getFirstName())
                        .setLastName(user.getLastName())
                        .setRealmId(user.getRealmId())
                        .build())
                .setEventType(com.green.energy.tracker.configuration.domain.event.UserEvent.valueOf(userEvent.name()))
                .build();
    }
}
