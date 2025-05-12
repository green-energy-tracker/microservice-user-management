package com.green.energy.tracker.user_management.service.authserver;

import org.apache.avro.specific.SpecificRecord;

public interface AuthServerEventProcessor<E extends SpecificRecord> {
     void handleEvent(E event);
}
