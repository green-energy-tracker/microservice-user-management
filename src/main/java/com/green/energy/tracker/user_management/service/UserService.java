package com.green.energy.tracker.user_management.service;

import com.green.energy.tracker.user_management.model.User;
import java.util.Optional;

public interface UserService {
    User save (User user);
    Optional<User> findById (Long id);
}
