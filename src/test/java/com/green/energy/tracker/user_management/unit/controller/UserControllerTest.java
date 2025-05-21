package com.green.energy.tracker.user_management.unit.controller;

import com.green.energy.tracker.user_management.controller.UserController;
import com.green.energy.tracker.user_management.model.User;
import com.green.energy.tracker.user_management.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    User mockUser;
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    @Test
    void testGetUserByUsernameResponseOk(){
        when(userService.findByUsername("test")).thenReturn(mockUser);
        assertEquals(userController.getUserByUsername("test"), ResponseEntity.ok(mockUser));
    }
    @Test
    void testGetUserByIdResponseOk(){
        when(userService.findById(1L)).thenReturn(mockUser);
        assertEquals(userController.getUserById(1L), ResponseEntity.ok(mockUser));
    }

}
