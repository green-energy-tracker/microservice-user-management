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
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
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
    public Topology build(
            KafkaStreamsConfiguration kafkaStreamsConfiguration,
            KafkaStreamsExceptionHandler kafkaStreamsExceptionHandler,
            StreamsBuilder streamsBuilder,
            CustomSerdes customSerdes,
            @Qualifier("UserServiceV1") UserService userService,
            @Qualifier("keycloakEventServiceV1") AuthServerEventService authServerEventService
    ) {

        streamsBuilder.stream(authServerEventsTopic, Consumed.with(Serdes.String(),Serdes.String()))
                .peek((key,event)-> log.info("Consuming events from topic {} : {} ",authServerEventsTopic, event))
                .mapValues((key, event) -> handleAuthServerEvent(kafkaStreamsExceptionHandler,authServerEventService,key,event))
                .filter((key, userEvent) -> userEvent.isPresent())
                .mapValues(Optional::get)
                .peek((key,userEvent)-> log.info("Converting event to userEvent {}",userEvent))
                .flatMapValues((key, userEvent) -> handleUserEvent(kafkaStreamsExceptionHandler,userService,key,userEvent))
                .peek((key,user)-> log.info("Converting userEvent to user {} ",user))
                .map((key, user) -> new KeyValue<>(user.getUsername(), user))
                .peek((key,user)-> log.info("Publishing user {} to topic {}",user,userEventsTopic))
                .to(userEventsTopic, Produced.with(Serdes.String(), customSerdes.userSerde()));

        var topology = streamsBuilder.build();
        var kafkaStreams = new KafkaStreams(topology, kafkaStreamsConfiguration.asProperties());
        kafkaStreams.setUncaughtExceptionHandler(error->{
            log.error("Uncaught Exception user event stream", error);
            return StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.REPLACE_THREAD;
        });
        kafkaStreams.start();
        return topology;
    }

    private Optional<Map<UserEvent, User>> handleAuthServerEvent(KafkaStreamsExceptionHandler kafkaStreamsExceptionHandler,
                                                                 AuthServerEventService authServerEventService,
                                                                 String key, String event){
        try {
            return Optional.ofNullable(authServerEventService.eventToUser(event));
        } catch (JsonProcessingException e) {
            kafkaStreamsExceptionHandler.sendToDlq(e,authServerEventsTopic,key,event);
            return Optional.empty();
        }
    }

    private List<User> handleUserEvent(KafkaStreamsExceptionHandler kafkaStreamsExceptionHandler, UserService userService,
                                       String key, Map<UserEvent,User> userEvent){
        return userEvent.entrySet()
                .stream()
                .map(entry -> {
                    try {
                        return userService.handleUserEvent(entry.getKey(), entry.getValue());
                    } catch (PersistenceException e) {
                        kafkaStreamsExceptionHandler.sendToDlq(e, authServerEventsTopic, key, userEvent);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}