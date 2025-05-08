package com.green.energy.tracker.user_management.service.authserver;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface AuthServerEventProcessor {
    void handleEvent(String event) throws JsonProcessingException;
}
