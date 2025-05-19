package com.green.energy.tracker.user_management.service;

import com.green.energy.tracker.user_management.model.User;
import com.green.energy.tracker.user_management.repository.UserRepository;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("UserServiceV1")
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        user.setUsername(user.getUsername().toUpperCase());
        if(userRepository.findByUsername(user.getUsername()).isPresent())
            throw new EntityExistsException("User already exists with username: " + user.getUsername());
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        user.setUsername(user.getUsername().toUpperCase());
        User persistenceUser = findByUsername(user.getUsername());
        BeanUtils.copyProperties(user, persistenceUser, "id");
        return userRepository.save(persistenceUser);
    }

    @Override
    public void delete(User user) {
        User persistenceUser = findByUsername(user.getUsername());
        userRepository.delete(persistenceUser);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(()-> new EntityNotFoundException("User not found with username: " + username));
    }

}
