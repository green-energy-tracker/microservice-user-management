package com.green.energy.tracker.user_management.unit.service;

import com.green.energy.tracker.user_management.model.User;
import com.green.energy.tracker.user_management.repository.UserRepository;
import com.green.energy.tracker.user_management.service.UserServiceImpl;
import jakarta.persistence.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
    void testCreate() {
        when(userRepository.findByUsername("TEST")).thenReturn(Optional.empty());
        userService.create(mockUser);
        verify(userRepository).findByUsername("TEST");
        verify(userRepository).save(mockUser);
    }

    @Test
    void testCreateEntityExistsException() {
        when(userRepository.findByUsername("TEST")).thenReturn(Optional.of(mockUser));
        assertThrows(EntityExistsException.class, () -> userService.create(mockUser));
        verify(userRepository).findByUsername("TEST");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdate() {
        when(userRepository.findByUsername("TEST")).thenReturn(Optional.of(mockUser));
        userService.update(mockUser);
        verify(userRepository).findByUsername("TEST");
        verify(userRepository).save(mockUser);
    }

    @Test
    void testUpdateEntityNotFoundException() {
        when(userRepository.findByUsername("TEST")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.update(mockUser));
        verify(userRepository).findByUsername("TEST");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDelete() {
        when(userRepository.findByUsername("TEST")).thenReturn(Optional.of(mockUser));
        userService.delete(mockUser);
        verify(userRepository).findByUsername("TEST");
        verify(userRepository).delete(mockUser);
    }

    @Test
    void testDeleteEntityNotFoundException() {
        when(userRepository.findByUsername("TEST")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.delete(mockUser));
        verify(userRepository).findByUsername("TEST");
        verify(userRepository, never()).delete(any());
    }

    @Test
    void testFindById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        User user = userService.findById(1L);
        assertNotNull(user);
        verify(userRepository).findById(1L);
    }

    @Test
    void testFindByIdEntityNotFoundException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findById(2L));
        verify(userRepository).findById(2L);
    }

    @Test
    void testFindByUsername() {
        when(userRepository.findByUsername("TEST")).thenReturn(Optional.of(mockUser));
        User user = userService.findByUsername("TEST");
        assertNotNull(user);
        verify(userRepository).findByUsername("TEST");
    }

    @Test
    void testFindByUsernameEntityNotFoundException() {
        when(userRepository.findByUsername("TEST")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findByUsername("TEST"));
        verify(userRepository).findByUsername("TEST");
    }

    @Test
    void testFindIdByUsername() {
        when(userRepository.findByUsername("TEST")).thenReturn(Optional.of(mockUser));
        Long id = userService.findIdByUsername("TEST");
        assertEquals(1L,id);
        verify(userRepository).findByUsername("TEST");
    }

    @Test
    void testFindIdByUsernameEntityNotFoundException() {
        when(userRepository.findByUsername("TEST")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findIdByUsername("TEST"));
        verify(userRepository).findByUsername("TEST");
    }

}
