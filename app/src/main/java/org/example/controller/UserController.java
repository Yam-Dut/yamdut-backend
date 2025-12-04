package org.example.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

/**
 * USER CONTROLLER - HANDLES USER AUTHENTICATION AND PROFILE MANAGEMENT
 * 
 * This controller manages:
 * - User registration
 * - User login
 * - Profile updates
 * - Ride history
 * 
 * In a real app, you'd use Spring Security for authentication.
 * For college project, we can keep it simple with basic validation.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    /**
     * USER REGISTRATION
     * POST http://localhost:8080/api/users/register
     * 
     * Expected JSON:
     * {
     *   "name": "John Doe",
     *   "email": "john@example.com",
     *   "phone": "9841000000",
     *   "password": "secret123"
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        // TODO: Validate, check if email/phone exists, hash password, save to database
        return ResponseEntity.ok("User registered successfully!");
    }
    
    /**
     * USER LOGIN
     * POST http://localhost:8080/api/users/login
     * 
     * Expected JSON:
     * {
     *   "phone": "9841000000",
     *   "password": "secret123"
     * }
     * 
     * Returns: User profile + authentication token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        // TODO: Validate credentials, generate session/token
        return ResponseEntity.ok("Login successful!");
    }
    
    /**
     * GET USER PROFILE
     * GET http://localhost:8080/api/users/profile/123
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable String userId) {
        // TODO: Fetch user from database, return profile
        return ResponseEntity.ok("User profile for ID: " + userId);
    }
}
