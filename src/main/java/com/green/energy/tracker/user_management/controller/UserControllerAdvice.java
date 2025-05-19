package com.green.energy.tracker.user_management.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class UserControllerAdvice {

    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleUserNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        return ErrorResponse.builder(ex, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage())).instance(URI.create(request.getRequestURI())).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {
        return ErrorResponse.builder(ex, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage())).instance(URI.create(request.getRequestURI())).build();
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleInternalError(Exception ex, HttpServletRequest request) {
        return ErrorResponse.builder(ex, ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)).instance(URI.create(request.getRequestURI())).build();
    }

}
