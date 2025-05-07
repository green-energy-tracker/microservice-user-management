package com.green.energy.tracker.user_management.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.green.energy.tracker.user_management.model.*;
import com.green.energy.tracker.user_management.service.*;
import com.green.energy.tracker.user_management.util.CustomSerdes;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserKafkaStreams {

    @Value("${spring.kafka.topic.auth-server-events}")
    private String authServerEventsTopic;
    @Value("${spring.kafka.topic.user-events}")
    private String userEventsTopic;

    @Bean
    public KStream<String, String> userKStream(StreamsBuilder streamsBuilder, KafkaStreamsExceptionHandler kafkaStreamsExceptionHandler,
                                               CustomSerdes customSerdes, @Qualifier("UserServiceV1") UserService userService,
                                               @Qualifier("keycloakEventServiceV1") AuthServerEventService authServerEventService) {

        KStream<String, String> userKStream = streamsBuilder.stream(authServerEventsTopic, Consumed.with(Serdes.String(),Serdes.String()));

        userKStream.peek((key,event)-> log.info("Consuming events from topic {} : {} ",authServerEventsTopic, event))
                .mapValues((key, event) -> handleEvent(kafkaStreamsExceptionHandler,authServerEventService,userService, key,event))
                .filter((key,optEvent)-> optEvent.isPresent())
                .mapValues(Optional::get)
                .map((key, user) -> new KeyValue<>(user.getUsername(), user))
                .peek((key,user)-> log.info("Publishing user {} to topic {}",user,userEventsTopic))
                .to(userEventsTopic, Produced.with(Serdes.String(), customSerdes.userSerde()));
        return userKStream;

    }

    private Optional<User> handleEvent(KafkaStreamsExceptionHandler exceptionHandler,
                             AuthServerEventService authServerEventService, UserService userService, String key, String event){
        try {
            Map<UserEvent, User> userEvents = authServerEventService.eventToUser(event);
            if (Objects.nonNull(userEvents) && !userEvents.isEmpty()) {
                Map.Entry<UserEvent, User> entry = userEvents.entrySet().iterator().next();
                return Optional.ofNullable(userService.handleUserEvent(entry.getKey(), entry.getValue()));
            }
        } catch (JsonProcessingException | PersistenceException e) {
            exceptionHandler.sendToDlt(e,authServerEventsTopic,key,event);
        }
        return Optional.empty();
    }
}