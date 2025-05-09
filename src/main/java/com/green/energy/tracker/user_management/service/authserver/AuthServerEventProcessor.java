package com.green.energy.tracker.user_management.service.authserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.avro.specific.SpecificRecord;

public interface AuthServerEventProcessor {
    void handleEvent(SpecificRecord authServerEvent) throws JsonProcessingException;
}
