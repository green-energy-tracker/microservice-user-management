package com.green.energy.tracker.user_management.kafkaStream;

import com.green.energy.tracker.user_management.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserKafkaStreams {

    @Value("${spring.kafka.topic.keycloak-admin-events}")
    private String keycloakAdminEventsTopic;
    @Value("${spring.kafka.topic.user-events}")
    private String userEventsTopic;
    private final UserService userService;

    @Bean
    public KStream<String, String> userKStream(StreamsBuilder streamsBuilder) {
        KStream<String, String> kStream = streamsBuilder.stream(keycloakAdminEventsTopic, Consumed.with(Serdes.String(),Serdes.String()));

        kStream.peek((key,value)-> log.info("Receive message {} ", value));
        return kStream;

    }
}
