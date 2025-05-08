package com.green.energy.tracker.user_management.util;

import com.green.energy.tracker.configuration.domain.event.UserEventPayload;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.common.serialization.Serde;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomSerdes {
    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaRegistryUrl;

    private Map<String, String> serdeConfig;

    @PostConstruct
    public void init() {
        serdeConfig = new HashMap<>();
        serdeConfig.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        serdeConfig.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, String.valueOf(false));
        serdeConfig.put(KafkaAvroSerializerConfig.AVRO_REMOVE_JAVA_PROPS_CONFIG, "true");
        serdeConfig.put("value.subject.name.strategy", "io.confluent.kafka.serializers.subject.RecordNameStrategy");
    }

    public Serde<UserEventPayload> userEventPayloadSerde() {
        final Serde<UserEventPayload> serde = new SpecificAvroSerde<>();
        serde.configure(serdeConfig, false);
        return serde;
    }

}
