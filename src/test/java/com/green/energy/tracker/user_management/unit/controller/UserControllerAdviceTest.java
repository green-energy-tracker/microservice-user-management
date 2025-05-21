package com.green.energy.tracker.user_management.unit.controller;

import com.green.energy.tracker.user_management.controller.UserControllerAdvice;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerAdviceTest {
    @Mock
    private HttpServletRequest httpServletRequest;
    @InjectMocks
    private UserControllerAdvice userControllerAdvice;

    @BeforeEach
    void setUp(){
        when(httpServletRequest.getRequestURI()).thenReturn("test");
    }

    @Test
    void testHandleUserNotFound(){
        var ex = new EntityNotFoundException();
        var response = userControllerAdvice.handleUserNotFound(ex,httpServletRequest);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        var problemDetail = response.getBody();
        assertEquals(ex.getMessage(),problemDetail.getDetail());
    }

    @Test
    void testHandleBadRequest(){
        var ex = new IllegalArgumentException();
        var response = userControllerAdvice.handleBadRequest(ex,httpServletRequest);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        var problemDetail = response.getBody();
        assertEquals(ex.getMessage(),problemDetail.getDetail());
    }

    @Test
    void testHandleInternalError(){
        var ex = new Exception();
        var response = userControllerAdvice.handleInternalError(ex,httpServletRequest);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        var problemDetail = response.getBody();
        assertEquals(ex.getMessage(),problemDetail.getDetail());
    }
}
