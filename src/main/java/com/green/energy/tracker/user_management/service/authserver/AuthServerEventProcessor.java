package com.green.energy.tracker.user_management.service.authserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.avro.generic.GenericRecord;

public interface AuthServerEventProcessor {
    void handleEvent(GenericRecord authServerEvent) throws JsonProcessingException;
}
