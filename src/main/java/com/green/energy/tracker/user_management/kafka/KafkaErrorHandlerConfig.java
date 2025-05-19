package com.green.energy.tracker.user_management.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.energy.tracker.user_management.keycloak.KeycloakEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import java.util.Objects;


@Configuration
@Slf4j
public class KafkaErrorHandlerConfig {

    @Value("${spring.kafka.topic.user-events-dlt}")
    private String topicUserEventsDlt;

    @Bean
    public DeadLetterPublishingRecoverer deadLetterRecoverer(
            KafkaTemplate<String, DltRecord> dltKafkaTemplate,
            ObjectMapper objectMapper) {
        return new DeadLetterPublishingRecoverer(dltKafkaTemplate, (ConsumerRecord<?, ?> record, Exception ex) -> {
             DltRecord dlt = DltRecord.builder()
                            .key(Objects.nonNull(record.key()) ? record.key().toString() : "")
                            .payload(Objects.nonNull(record.value()) ? record.value().toString() : "")
                            .error(ex.getMessage())
                            .build();
                    dltKafkaTemplate.send(topicUserEventsDlt, record.partition(),dlt.getKey(),dlt);
                    return new TopicPartition(topicUserEventsDlt, record.partition());
                }
        );
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler(DeadLetterPublishingRecoverer recoverer) {
        FixedBackOff backOff = new FixedBackOff(1000L, 3L);
        return new DefaultErrorHandler(recoverer, backOff);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KeycloakEvent> kafkaListenerContainerFactory (
            ConsumerFactory<String, KeycloakEvent> consumerFactory, DefaultErrorHandler defaultErrorHandler) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, KeycloakEvent>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(defaultErrorHandler);
        return factory;
    }
}