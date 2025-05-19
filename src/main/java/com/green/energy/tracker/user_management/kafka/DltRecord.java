package com.green.energy.tracker.user_management.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("java:S1068")
public class DltRecord {
    private String key;
    private String payload;
    private String error;
    private String causedBy;
}
