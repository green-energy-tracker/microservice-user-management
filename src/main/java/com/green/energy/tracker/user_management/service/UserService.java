package com.green.energy.tracker.user_management.service;

import com.green.energy.tracker.user_management.model.User;
import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;

public interface UserService {
    User save (User user);
    User update (User user);
    void delete (User user);
    User findById (Long id);
    User findByUsername(String username);

}
