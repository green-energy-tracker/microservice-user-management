package com.green.energy.tracker.user_management.service.authserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.green.energy.tracker.user_management.model.User;
import com.green.energy.tracker.user_management.model.UserEvent;

import java.util.Optional;

public interface AuthServerUserService<T> {
    Optional<User> getUser(T t) throws JsonProcessingException;
    Optional<UserEvent> getUserEvent(T t);
    boolean isEventPermitted(T t);
}
