package com.green.energy.tracker.user_management.stream;

import com.green.energy.tracker.user_management.config.KafkaStreamsExceptionHandler;
import com.green.energy.tracker.user_management.service.AuthServerEventService;
import com.green.energy.tracker.user_management.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserKafkaStreams {

    @Value("${spring.kafka.topic.auth-server-events}")
    private String authServerEventsTopic;
    @Value("${spring.kafka.topic.user-events}")
    private String userEventsTopic;

    @Bean
    public Topology build(KafkaStreamsConfiguration kafkaStreamsConfiguration, StreamsBuilder streamsBuilder, KafkaStreamsExceptionHandler handler,
                          UserService userService, @Qualifier("keycloakEventServiceV1") AuthServerEventService authServerEventService) {
        streamsBuilder.stream(authServerEventsTopic, Consumed.with(Serdes.String(),Serdes.String()))
                .peek((key,event)-> log.info("Consuming events from topic {} : {} ",authServerEventsTopic, event))
                .flatMap((key, event) -> authServerEventService.eventToUser(event).entrySet().stream()
                        .map(entry -> new KeyValue<>(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList()))
                .peek((userEvent,user)->{
                    switch (userEvent){
                        case CREATE -> userService.save(user);
                        case DELETE -> userService.delete(user);
                        case UPDATE -> userService.update(user);
                    }
                })
                .to(userEventsTopic);
        var topology = streamsBuilder.build();
        var kafkaStreams = new KafkaStreams(topology, kafkaStreamsConfiguration.asProperties());
        kafkaStreams.setUncaughtExceptionHandler(handler::handleUncaught);
        kafkaStreams.start();
        return topology;
    }
}
