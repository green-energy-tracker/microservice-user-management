package com.green.energy.tracker.user_management.kafka;

import com.green.energy.tracker.user_management.keycloak.KeycloakEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.*;
import org.springframework.util.backoff.FixedBackOff;
import java.util.Objects;

@Configuration
@Slf4j
public class KafkaErrorHandlerConfig {

    @Value("${spring.kafka.topic.user-events-dlt}")
    private String topicUserEventsDlt;

    @Bean
    public DeadLetterPublishingRecoverer deadLetterRecover(KafkaTemplate<String, DltRecord> dltKafkaTemplate) {
        DeadLetterPublishingRecoverer recover = new DeadLetterPublishingRecoverer(dltKafkaTemplate, (ConsumerRecord<?, ?> dltRecord, Exception ex) -> {
             DltRecord dlt = DltRecord.builder()
                            .key(Objects.nonNull(dltRecord.key()) ? dltRecord.key().toString() : "")
                            .payload(Objects.nonNull(dltRecord.value()) ? dltRecord.value().toString() : "")
                            .error(ex.getMessage())
                            .causedBy(ex.getCause().getMessage())
                            .build();
                    dltKafkaTemplate.send(topicUserEventsDlt, dltRecord.partition(),dlt.getKey(),dlt);
            return null;
        });
        recover.setThrowIfNoDestinationReturned(false);
        return recover;
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler(DeadLetterPublishingRecoverer recover) {
        FixedBackOff backOff = new FixedBackOff(1000L, 3L);
        return new DefaultErrorHandler(recover, backOff);
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