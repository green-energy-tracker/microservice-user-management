package com.green.energy.tracker.user_management.kafka;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DltRecord {
    private String key;
    private String payload;
    private String error;
    private String causedBy;
}
