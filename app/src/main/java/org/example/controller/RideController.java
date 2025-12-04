package org.example.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

/**
 * RIDE CONTROLLER - HANDLES ALL RIDE-RELATED HTTP REQUESTS
 * 
 * This is a REST Controller that exposes endpoints for:
 * - Booking new rides
 * - Getting ride details
 * - Cancelling rides
 * - Tracking ride status
 * 
 * @RestController = @Controller + @ResponseBody (automatically converts to JSON)
 * @RequestMapping("/api/rides") means all endpoints start with /api/rides
 * 
 * Frontend will call these endpoints to:
 * 1. POST /api/rides/book - Book a new ride
 * 2. GET /api/rides/{id} - Get ride details by ID
 * 3. GET /api/rides/user/{userId} - Get all rides for a user
 * 4. PUT /api/rides/{id}/cancel - Cancel a ride
 */
@RestController
@RequestMapping("/api/rides")
public class RideController {
    
    /**
     * BOOK A NEW RIDE
     * POST http://localhost:8080/api/rides/book
     * 
     * Expected JSON from frontend:
     * {
     *   "userId": "123",
     *   "pickupLat": 27.7172,
     *   "pickupLon": 85.3240,
     *   "destinationLat": 27.6961,
     *   "destinationLon": 85.3544,
     *   "rideType": "STANDARD"
     * }
     * 
     * Returns: Ride details with estimated fare and assigned driver
     */
    @PostMapping("/book")
    public ResponseEntity<?> bookRide(@RequestBody RideRequestDTO rideRequest) {
        // TODO: Validate request, calculate fare, find nearest driver, save to database
        return ResponseEntity.ok("Ride booked successfully!");
    }
    
    /**
     * GET RIDE DETAILS BY ID
     * GET http://localhost:8080/api/rides/123
     */
    @GetMapping("/{rideId}")
    public ResponseEntity<?> getRideDetails(@PathVariable String rideId) {
        // TODO: Fetch ride from database and return details
        return ResponseEntity.ok("Ride details for ID: " + rideId);
    }
    
    /**
     * GET ALL RIDES FOR A USER
     * GET http://localhost:8080/api/rides/user/456
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<?>> getUserRides(@PathVariable String userId) {
        // TODO: Fetch all rides for this user from database
        return ResponseEntity.ok(List.of());
    }
}
