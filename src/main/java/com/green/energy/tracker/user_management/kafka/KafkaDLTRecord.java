package com.green.energy.tracker.user_management.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaDLTRecord {
    private String topic;
    private String key;
    private String value;
    private String errorMessage;
    private Long timestamp;
}