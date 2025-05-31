package com.green.energy.tracker.user_management.controller;

import com.green.energy.tracker.user_management.model.User;
import com.green.energy.tracker.user_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user-management")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get User Info by Username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "400", description = "Invalid username parameter", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @GetMapping("/findByUsername")
    public ResponseEntity<User> getUserByUsername(@RequestParam String username) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @Operation(summary = "Get User Info by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "400", description = "Invalid id parameter", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @GetMapping("/findById")
    public ResponseEntity<User> getUserById(@RequestParam Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(summary = "Get Id by Username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "400", description = "Invalid id parameter", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @GetMapping("/findIdByUsername")
    public ResponseEntity<Long> getUserById(@RequestParam String username) {
        return ResponseEntity.ok(userService.findIdByUsername(username));
    }
}
