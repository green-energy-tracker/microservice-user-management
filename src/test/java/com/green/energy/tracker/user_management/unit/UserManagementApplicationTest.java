package com.green.energy.tracker.user_management.unit;

import com.green.energy.tracker.user_management.UserManagementApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;

@ExtendWith(MockitoExtension.class)
class UserManagementApplicationTest {
    @Test
    void testMain() {
        String[] args = new String[] { "test"};
        try (MockedStatic<SpringApplication> springApp = Mockito.mockStatic(SpringApplication.class)) {
            springApp.when(() -> SpringApplication.run(UserManagementApplication.class, args)).thenReturn(null);
            UserManagementApplication.main(args);
            springApp.verify(() -> SpringApplication.run(UserManagementApplication.class, args), Mockito.times(1));
        }
    }
}
