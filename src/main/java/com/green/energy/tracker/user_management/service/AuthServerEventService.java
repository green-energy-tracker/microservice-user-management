package com.green.energy.tracker.user_management.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.green.energy.tracker.user_management.model.*;
import java.util.Map;

public interface AuthServerEventService {
    Map<UserEvent,User> eventToUser(String event) throws JsonProcessingException;
}
