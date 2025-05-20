package com.green.energy.tracker.user_management.service;

import com.green.energy.tracker.user_management.model.User;
import com.green.energy.tracker.user_management.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    private User mockUser;

    @BeforeEach
    void setUp(){
        mockUser = User.builder().id(1L).email("test@test.com").username("TEST").firstName("test").lastName("test").realmId("realmId").enabled(true).build();
    }

    @Test
    void whenCreateThenRepositorySaveCalled() {
        when(userRepository.findByUsername("TEST")).thenReturn(Optional.empty());
        userService.create(mockUser);
        verify(userRepository).findByUsername("TEST");
        verify(userRepository).save(mockUser);
    }

    @Test
    void whenCreateThenThrowEntityExistsException() {
        when(userRepository.findByUsername("TEST")).thenReturn(Optional.of(mockUser));
        assertThrows(EntityExistsException.class, () -> userService.create(mockUser));
        verify(userRepository).findByUsername("TEST");
        verify(userRepository, never()).save(any());
    }

    @Test
    void whenUpdateThenRepositorySaveCalled() {
        when(userRepository.findByUsername("TEST")).thenReturn(Optional.of(mockUser));
        userService.update(mockUser);
        verify(userRepository).findByUsername("TEST");
        verify(userRepository).save(mockUser);
    }

    @Test
    void whenUpdateThenThrowEntityNotFoundException() {
        when(userRepository.findByUsername("TEST")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.update(mockUser));
        verify(userRepository).findByUsername("TEST");
        verify(userRepository, never()).save(any());
    }

    @Test
    void whenDeleteThenRepositoryDeleteCalled() {
        when(userRepository.findByUsername("TEST")).thenReturn(Optional.of(mockUser));
        userService.delete(mockUser);
        verify(userRepository).findByUsername("TEST");
        verify(userRepository).delete(mockUser);
    }

    @Test
    void whenDeleteThenThrowEntityNotFoundException() {
        when(userRepository.findByUsername("TEST")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.delete(mockUser));
        verify(userRepository).findByUsername("TEST");
        verify(userRepository, never()).delete(any());
    }

    @Test
    void whenFindByIdThenReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        User user = userService.findById(1L);
        assertNotNull(user);
        verify(userRepository).findById(1L);
    }

    @Test
    void whenFindByIdThenThrowEntityNotFoundException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findById(2L));
        verify(userRepository).findById(2L);
    }

    @Test
    void whenFindByUsernameThenReturnUser() {
        when(userRepository.findByUsername("TEST")).thenReturn(Optional.of(mockUser));
        User user = userService.findByUsername("TEST");
        assertNotNull(user);
        verify(userRepository).findByUsername("TEST");
    }

    @Test
    void whenFindByUsernameThenThrowEntityNotFoundException() {
        when(userRepository.findByUsername("TEST")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findByUsername("TEST"));
        verify(userRepository).findByUsername("TEST");
    }

}
