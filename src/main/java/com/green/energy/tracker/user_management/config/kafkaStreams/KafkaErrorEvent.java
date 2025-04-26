package com.green.energy.tracker.user_management.config.kafkaStreams;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaErrorEvent {
    private String type;
    private String error;
    private String record;
}