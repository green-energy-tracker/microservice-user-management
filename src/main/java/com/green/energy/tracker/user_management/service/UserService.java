package com.green.energy.tracker.user_management.service;

import com.green.energy.tracker.user_management.model.User;

public interface UserService {
    User create (User user);
    User update (User user);
    void delete (User user);
    User findById (Long id);
    User findByUsername(String username);
    Long findIdByUsername(String username);
}
