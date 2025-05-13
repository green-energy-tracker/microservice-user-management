package com.green.energy.tracker.user_management.kafka;

import com.green.energy.tracker.user_management.keycloak.KeycloakEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Objects;

@Configuration
@Slf4j
public class KafkaErrorHandlerConfig {

    @Bean
    public DeadLetterPublishingRecoverer deadLetterRecoverer(KafkaTemplate<Object, Object> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(kafkaTemplate,
                (ConsumerRecord<?, ?> record, Exception ex) ->
                        new TopicPartition(record.topic() + ".DLT", record.partition())
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

    @Bean
    public KafkaTemplate<Object, Object> producerListenerErrorHandler(ProducerFactory<Object, Object> pf) {
        KafkaTemplate<Object, Object> kafkaTemplate = new KafkaTemplate<>(pf);
        kafkaTemplate.setProducerListener(new ProducerListener<>() {
            @Override
            public void onError(ProducerRecord<Object, Object> producerRecord, RecordMetadata recordMetadata, Exception exception) {
                String dltTopic = producerRecord.topic() + ".DLT";
                ProducerRecord<Object, Object> dltRecord = new ProducerRecord<>(dltTopic, producerRecord.key(), producerRecord.value());
                kafkaTemplate.send(dltRecord)
                        .whenCompleteAsync((result,ex)->{
                            if(Objects.nonNull(ex))
                                log.error("Failed to send record to DLT topic '{}': {}", dltTopic, ex.getMessage(), ex);
                        });
            }
        });
        return kafkaTemplate;
    }
}
