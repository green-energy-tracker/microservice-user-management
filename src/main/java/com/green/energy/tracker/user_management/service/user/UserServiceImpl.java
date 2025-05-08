package com.green.energy.tracker.user_management.service.user;

import com.green.energy.tracker.user_management.model.User;
import com.green.energy.tracker.user_management.model.UserEvent;
import com.green.energy.tracker.user_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service("UserServiceV1")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User handleUserEvent(UserEvent userEvent, User user) {
        switch (userEvent){
            case CREATE -> {
                return save(user);
            }
            case UPDATE -> {
                return update(user);
            }
            case DELETE -> {
                delete(user);
                return user;
            }
        }
        return user;
    }
}
