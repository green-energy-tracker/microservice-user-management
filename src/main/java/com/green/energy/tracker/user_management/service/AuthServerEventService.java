package com.green.energy.tracker.user_management.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.green.energy.tracker.user_management.model.*;
import java.util.Optional;

public interface AuthServerEventService {
    Optional<User> getUser(String event) throws JsonProcessingException;
    Optional<UserEvent> getUserEvent(String event) throws JsonProcessingException;
}
