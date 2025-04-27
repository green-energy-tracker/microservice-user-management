package com.green.energy.tracker.user_management.util;

import com.green.energy.tracker.user_management.model.User;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Service;

@Service
public class CustomSerdes {
    public Serde<User> userSerde() {
        JsonSerializer<User> serializer = new JsonSerializer<>();
        JsonDeserializer<User> deserializer = new JsonDeserializer<>(User.class, false);
        return Serdes.serdeFrom(serializer, deserializer);
    }
}
