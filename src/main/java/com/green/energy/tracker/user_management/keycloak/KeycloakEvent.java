package com.green.energy.tracker.user_management.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KeycloakEvent {
    @JsonProperty("id_event")
    private Long idEvent;
    private String id;
    @JsonProperty("admin_event_time")
    private Long adminEventTime;
    @JsonProperty("realm_id")
    private String realmId;
    @JsonProperty("operation_type")
    private String operationType;
    @JsonProperty("auth_realm_id")
    private String authRealmId;
    @JsonProperty("auth_client_id")
    private String authClientId;
    @JsonProperty("auth_user_id")
    private String authUserId;
    @JsonProperty("ip_address")
    private String ipAddress;
    @JsonProperty("resource_path")
    private String resourcePath;
    private String representation;
    private String error;
    @JsonProperty("resource_type")
    private String resourceType;
    @JsonProperty("details_json")
    private String detailsJson;
}
