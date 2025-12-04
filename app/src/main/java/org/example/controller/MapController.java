package org.example.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;

/**
 * MAP CONTROLLER - HANDLES MAP AND LOCATION-RELATED OPERATIONS
 * 
 * This controller provides:
 * - Kathmandu-specific location data
 * - Distance calculations
 * - Route suggestions
 * - Popular pickup/drop points in Kathmandu
 * 
 * For a real app, you'd integrate with Google Maps API or OSRM.
 * For college project, we can use pre-defined Kathmandu locations.
 */
@RestController
@RequestMapping("/api/map")
public class MapController {
    
    /**
     * GET POPULAR LOCATIONS IN KATHMANDU
     * GET http://localhost:8080/api/map/locations/kathmandu
     * 
     * Returns: JSON array of popular locations with names and coordinates.
     * Example: Thamel, Airport, Boudha, Pashupati, etc.
     */
    @GetMapping("/locations/kathmandu")
    public ResponseEntity<Map<String, Object>> getKathmanduLocations() {
        Map<String, Object> response = new HashMap<>();
        
        // Pre-defined Kathmandu locations
        Map<String, double[]> locations = new HashMap<>();
        locations.put("Thamel", new double[]{27.7172, 85.3240});
        locations.put("Tribhuvan Airport", new double[]{27.6961, 85.3544});
        locations.put("Boudhanath Stupa", new double[]{27.7218, 85.3621});
        locations.put("Pashupatinath Temple", new double[]{27.7105, 85.3486});
        locations.put("Swayambhunath", new double[]{27.7149, 85.2906});
        locations.put("Durbar Square", new double[]{27.7045, 85.3072});
        
        response.put("locations", locations);
        return ResponseEntity.ok(response);
    }
    
    /**
     * CALCULATE DISTANCE BETWEEN TWO POINTS
     * GET http://localhost:8080/api/map/distance?lat1=27.7172&lon1=85.3240&lat2=27.6961&lon2=85.3544
     * 
     * Returns: Distance in kilometers and estimated travel time
     */
    @GetMapping("/distance")
    public ResponseEntity<Map<String, Object>> calculateDistance(
            @RequestParam double lat1,
            @RequestParam double lon1,
            @RequestParam double lat2,
            @RequestParam double lon2) {
        
        Map<String, Object> response = new HashMap<>();
        
        // TODO: Calculate distance using Haversine formula
        double distance = 5.5; // Placeholder
        int estimatedTime = 15; // Placeholder in minutes
        
        response.put("distanceKm", distance);
        response.put("estimatedTimeMinutes", estimatedTime);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * CALCULATE FARE FOR A RIDE
     * GET http://localhost:8080/api/map/fare?distance=5.5&time=15&rideType=STANDARD
     * 
     * Uses Kathmandu-specific pricing:
     * - Base fare: NPR 50
     * - Per km: NPR 25
     * - Per minute: NPR 1
     */
    @GetMapping("/fare")
    public ResponseEntity<Map<String, Object>> calculateFare(
            @RequestParam double distance,
            @RequestParam int time,
            @RequestParam(defaultValue = "STANDARD") String rideType) {
        
        Map<String, Object> response = new HashMap<>();
        
        double baseFare = 50.0;
        double perKmRate = 25.0;
        double perMinuteRate = 1.0;
        
        double fare = baseFare + (distance * perKmRate) + (time * perMinuteRate);
        
        response.put("fare", fare);
        response.put("currency", "NPR");
        response.put("breakdown", Map.of(
            "baseFare", baseFare,
            "distanceCharge", distance * perKmRate,
            "timeCharge", time * perMinuteRate
        ));
        
        return ResponseEntity.ok(response);
    }
}
