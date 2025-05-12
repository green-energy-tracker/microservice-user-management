package com.green.energy.tracker.user_management.service.authserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.green.energy.tracker.user_management.model.User;
import com.green.energy.tracker.user_management.model.UserEvent;
import org.apache.avro.specific.SpecificRecord;
import java.util.Optional;

public interface AuthServerUserService<E extends SpecificRecord> {
    Optional<User> getUser(E event) throws JsonProcessingException;
    Optional<UserEvent> getUserEvent(E event);
    boolean isEventPermitted(E event);
}
