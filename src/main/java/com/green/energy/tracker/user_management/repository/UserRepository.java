package com.green.energy.tracker.user_management.repository;

import com.green.energy.tracker.user_management.model.User;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User,Long> {
    Optional<User> findByUsername(String username);
}
