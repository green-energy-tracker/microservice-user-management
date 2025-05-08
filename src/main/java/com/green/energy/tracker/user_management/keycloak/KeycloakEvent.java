package com.green.energy.tracker.user_management.keycloak;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeycloakEvent {
    private Long idEvent;
    private String id;
    private Long adminEventTime;
    private String realmId;
    private String operationType;
    private String authRealmId;
    private String authClientId;
    private String authUserId;
    private String ipAddress;
    private String resourcePath;
    private String representation;
    private String error;
    private String resourceType;
    private String detailsJson;
}
