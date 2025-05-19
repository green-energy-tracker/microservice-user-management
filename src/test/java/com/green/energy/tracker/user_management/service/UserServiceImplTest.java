package com.green.energy.tracker.user_management.service;

import com.green.energy.tracker.user_management.model.User;
import com.green.energy.tracker.user_management.repository.UserRepository;
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
        mockUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .username("test")
                .firstName("test")
                .lastName("test")
                .realmId("realmId")
                .enabled(true)
                .build();
    }
    @Test
    void givenExistingUsername_whenDelete_thenRepositoryDeleteCalled() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(mockUser));
        userService.delete(mockUser);
        verify(userRepository).findByUsername("test");
        verify(userRepository).delete(mockUser);
    }

    @Test
    void givenNonExistingUsername_whenDelete_thenThrowEntityNotFoundException() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> userService.delete(mockUser));
        assertTrue(ex.getMessage().contains("User not found with username: test"));
        verify(userRepository).findByUsername("test");
        verify(userRepository, never()).delete(any());
    }

    @Test
    void givenExistingUser_whenFindById_thenReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        User user = userService.findById(1L);
        assertNotNull(user);
        assertEquals(1L,user.getId());
        assertEquals("test@test.com",user.getEmail());
        assertEquals("test",user.getUsername());
        assertEquals("test",user.getFirstName());
        assertEquals("test",user.getLastName());
        assertEquals("realmId",user.getRealmId());
        assertTrue(user.isEnabled());
        verify(userRepository).findById(1L);
    }

    @Test
    void givenNotExistingUser_whenFindById_thenThrowEntityNotFoundException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> userService.findById(2L));
        assertTrue(ex.getMessage().contains("User not found with id: 2"));
        verify(userRepository).findById(2L);
    }

    @Test
    void givenExistingUser_whenFindByUsername_thenReturnUser() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(mockUser));
        User user = userService.findByUsername("test");
        assertNotNull(user);
        assertEquals(1L,user.getId());
        assertEquals("test@test.com",user.getEmail());
        assertEquals("test",user.getUsername());
        assertEquals("test",user.getFirstName());
        assertEquals("test",user.getLastName());
        assertEquals("realmId",user.getRealmId());
        assertTrue(user.isEnabled());
        verify(userRepository).findByUsername("test");
    }

    @Test
    void givenNotExistingUser_whenFindByUsername_thenThrowEntityNotFoundException() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> userService.findByUsername("test"));
        assertTrue(ex.getMessage().contains("User not found with username: test"));
        verify(userRepository).findByUsername("test");
    }

}
