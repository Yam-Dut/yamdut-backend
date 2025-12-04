package org.example.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

/**
 * DRIVER CONTROLLER - MANAGES DRIVERS AND THEIR LOCATIONS
 * 
 * This controller handles:
 * - Driver registration
 * - Driver location updates (real-time)
 * - Driver availability status
 * - Driver rating and feedback
 * 
 * For real-time location updates, consider WebSocket or Server-Sent Events (SSE).
 * For college project, simple HTTP endpoints are fine.
 */
@RestController
@RequestMapping("/api/drivers")
public class DriverController {
    
    /**
     * GET AVAILABLE DRIVERS NEAR A LOCATION
     * GET http://localhost:8080/api/drivers/nearby?lat=27.7172&lon=85.3240&radius=5
     * 
     * Parameters:
     * - lat: Latitude of user's location
     * - lon: Longitude of user's location
     * - radius: Search radius in kilometers
     * 
     * Returns: List of available drivers with their locations and vehicle info
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<?>> getNearbyDrivers(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "5") double radius) {
        // TODO: Query database for drivers within radius, calculate distances
        return ResponseEntity.ok(List.of());
    }
    
    /**
     * UPDATE DRIVER LOCATION (FOR REAL-TIME TRACKING)
     * PUT http://localhost:8080/api/drivers/{driverId}/location
     * 
     * Expected JSON:
     * {
     *   "latitude": 27.7172,
     *   "longitude": 85.3240
     * }
     * 
     * This would be called frequently by driver's app to update their position.
     */
    @PutMapping("/{driverId}/location")
    public ResponseEntity<?> updateDriverLocation(
            @PathVariable String driverId,
            @RequestBody LocationUpdateDTO locationUpdate) {
        // TODO: Update driver's location in database
        return ResponseEntity.ok("Location updated!");
    }
    
    /**
     * UPDATE DRIVER AVAILABILITY STATUS
     * PUT http://localhost:8080/api/drivers/{driverId}/status
     * 
     * Expected JSON:
     * {
     *   "status": "AVAILABLE"  // or "BUSY", "OFFLINE"
     * }
     */
    @PutMapping("/{driverId}/status")
    public ResponseEntity<?> updateDriverStatus(
            @PathVariable String driverId,
            @RequestBody StatusUpdateDTO statusUpdate) {
        // TODO: Update driver's status in database
        return ResponseEntity.ok("Status updated!");
    }
}
