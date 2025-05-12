package com.green.energy.tracker.user_management.service.authserver;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.kafka.annotation.KafkaListener;

public interface AuthServerConsumerKafka<E extends SpecificRecord> {
    @KafkaListener(
            topics = "${spring.kafka.topic.auth-server-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    void consumeEvent(E event);
}
