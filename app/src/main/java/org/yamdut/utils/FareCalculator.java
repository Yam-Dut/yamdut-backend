package org.yamdut.utils;

/**
 * Utility class for calculating distance and fare for rides
 */
public class FareCalculator {
    
    // Fare calculation constants
    private static final double METERS_PER_UNIT = 5.0;      // 5 meters
    private static final double FARE_PER_UNIT = 10.0;       // 10 NPR per unit
    private static final double MIN_FARE = 50.0;            // Minimum fare in NPR
    
    /**
     * Calculate distance between two coordinates using Haversine formula
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @return Distance in meters
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS = 6371000; // Earth's radius in meters
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
    
    /**
     * Calculate fare based on distance
     * Formula: (distance / 5 meters) * 10 NPR with minimum fare
     * @param distanceMeters Distance in meters
     * @return Fare in NPR
     */
    public static double calculateFare(double distanceMeters) {
        double calculatedFare = Math.ceil((distanceMeters / METERS_PER_UNIT) * FARE_PER_UNIT);
        return Math.max(calculatedFare, MIN_FARE);
    }
    
    /**
     * Estimate duration based on distance (rough estimate)
     * Assumes average speed of 40 km/h in city
     * @param distanceMeters Distance in meters
     * @return Estimated duration in seconds
     */
    public static double estimateDuration(double distanceMeters) {
        final double AVERAGE_SPEED_MPS = 40000.0 / 3600.0; // 40 km/h in meters per second
        return distanceMeters / AVERAGE_SPEED_MPS;
    }
    
    /**
     * Calculate all ride metrics at once
     * @return FareDetails object containing distance, fare, and duration
     */
    public static FareDetails calculateRideDetails(double pickupLat, double pickupLon, 
                                                   double destLat, double destLon) {
        double distance = calculateDistance(pickupLat, pickupLon, destLat, destLon);
        double fare = calculateFare(distance);
        double duration = estimateDuration(distance);
        
        return new FareDetails(distance, fare, duration);
    }
    
    /**
     * Inner class to hold fare calculation results
     */
    public static class FareDetails {
        private final double distanceMeters;
        private final double fareNPR;
        private final double durationSeconds;
        
        public FareDetails(double distanceMeters, double fareNPR, double durationSeconds) {
            this.distanceMeters = distanceMeters;
            this.fareNPR = fareNPR;
            this.durationSeconds = durationSeconds;
        }
        
        public double getDistanceMeters() {
            return distanceMeters;
        }
        
        public double getFareNPR() {
            return fareNPR;
        }
        
        public double getDurationSeconds() {
            return durationSeconds;
        }
        
        public double getDistanceKm() {
            return distanceMeters / 1000.0;
        }
        
        public double getDurationMinutes() {
            return durationSeconds / 60.0;
        }
        
        @Override
        public String toString() {
            return String.format("Distance: %.2f km, Fare: NPR %.0f, Duration: %.0f min",
                    getDistanceKm(), fareNPR, getDurationMinutes());
        }
    }
}
