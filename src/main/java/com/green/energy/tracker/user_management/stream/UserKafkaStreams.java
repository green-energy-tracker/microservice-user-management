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
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.stereotype.Component;

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
                .mapValues((key, event) -> authServerEventService.eventToUser(event))
                .flatMapValues((key, mapEvent) ->
                        mapEvent.entrySet().stream()
                                .map(entry -> userService.handleUserEvent(entry.getKey(), entry.getValue()))
                                .toList()
                )
                .map((key, user) -> new KeyValue<>(user.getId(), user.toString()))
                .to(userEventsTopic, Produced.with(Serdes.Long(), Serdes.String()));

        var topology = streamsBuilder.build();
        var kafkaStreams = new KafkaStreams(topology, kafkaStreamsConfiguration.asProperties());
        kafkaStreams.setUncaughtExceptionHandler(handler::handleUncaught);
        kafkaStreams.start();
        return topology;
    }
}
