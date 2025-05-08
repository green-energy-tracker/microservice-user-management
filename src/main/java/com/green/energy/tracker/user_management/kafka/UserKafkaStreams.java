package com.green.energy.tracker.user_management.kafka;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.green.energy.tracker.configuration.domain.event.UserEventPayload;
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
import org.springframework.context.annotation.Configuration;
import java.util.*;

@Configuration
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
                .filter((key,userEventPayload)-> Objects.nonNull(userEventPayload.getUser()) && Objects.nonNull(userEventPayload.getEventType()))
                .peek((key,user)-> log.info("Publishing user {} to topic {}",user,userEventsTopic))
                .to(userEventsTopic, Produced.with(Serdes.String(), customSerdes.userEventPayloadSerde()));
        return userKStream;

    }

    private UserEventPayload handleEvent(KafkaStreamsExceptionHandler exceptionHandler,
                                         AuthServerEventService authServerEventService, UserService userService, String key, String event){
        try {
            Optional<User> optUser = authServerEventService.getUser(event);
            Optional<UserEvent> optUserEvent = authServerEventService.getUserEvent(event);
            if(optUser.isEmpty() || optUserEvent.isEmpty())
                throw new JsonParseException("Not valid payload");
            userService.handleUserEvent(optUserEvent.get(),optUser.get());
            return UserEventPayload.newBuilder()
                    .setEventType(com.green.energy.tracker.configuration.domain.event.UserEvent.valueOf(optUserEvent.get().name()))
                    .setUser(com.green.energy.tracker.configuration.domain.event.User.newBuilder()
                            .setId(optUser.get().getId())
                            .setEmail(optUser.get().getEmail())
                            .setEnabled(optUser.get().isEnabled())
                            .setFirstName(optUser.get().getFirstName())
                            .setLastName(optUser.get().getLastName())
                            .setRealmId(optUser.get().getRealmId())
                            .setUsername(optUser.get().getUsername())
                            .build()
                    )
                    .build();
        } catch (JsonProcessingException | PersistenceException e) {
            exceptionHandler.sendToDlt(e,authServerEventsTopic,key,event);
            return UserEventPayload.newBuilder().build();
        }
    }
}