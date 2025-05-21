package com.green.energy.tracker.user_management.kafka;

import com.green.energy.tracker.user_management.keycloak.KeycloakEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.*;
import org.springframework.util.backoff.FixedBackOff;
import java.util.Objects;
import java.util.function.BiFunction;

@Configuration
@Slf4j
public class KafkaErrorHandlerConfig {

    @Value("${spring.kafka.topic.user-events-dlt}")
    private String topicUserEventsDlt;

    @Bean
    public BiFunction<ConsumerRecord<String,KeycloakEvent>, Exception, TopicPartition> dltDestinationResolver(KafkaTemplate<String, DltRecord> dltKafkaTemplate) {
        return (rec, ex) -> sendDltRecord(dltKafkaTemplate,rec,ex);
    }

    private TopicPartition sendDltRecord(KafkaTemplate<String,DltRecord> dltKafkaTemplate, ConsumerRecord<String,KeycloakEvent> rec, Exception ex){
        DltRecord dlt = buildDltRecord(rec,ex);
        dltKafkaTemplate.send(topicUserEventsDlt, rec.partition(), dlt.getKey(), dlt);
        return null;
    }

    private DltRecord buildDltRecord(ConsumerRecord<String,KeycloakEvent> rec, Exception ex){
        return DltRecord.builder()
                .key(Objects.nonNull(rec.key()) ? rec.key() : "")
                .payload(Objects.nonNull(rec.value()) ? rec.value().toString() : "")
                .error(ex.getMessage())
                .causedBy(ex.getCause().getMessage())
                .build();
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterRecover(KafkaTemplate<String, DltRecord> dltKafkaTemplate,
                                                           BiFunction<ConsumerRecord<?,?>, Exception, TopicPartition> dltDestinationResolver) {
        DeadLetterPublishingRecoverer recover = new DeadLetterPublishingRecoverer(dltKafkaTemplate, dltDestinationResolver);
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