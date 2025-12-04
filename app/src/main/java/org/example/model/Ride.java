package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * RIDE ENTITY MODEL - REPRESENTS A RIDE/BOOKING IN THE DATABASE
 * 
 * This is the most important entity in your Uber clone.
 * It connects Users, Drivers, and contains all ride details.
 * 
 * Ride status lifecycle:
 * 1. REQUESTED - User books a ride
 * 2. ACCEPTED - Driver accepts the ride
 * 3. ARRIVING - Driver is arriving at pickup
 * 4. IN_PROGRESS - User is in the vehicle, ride ongoing
 * 5. COMPLETED - Ride finished, payment done
 * 6. CANCELLED - Ride cancelled by user or driver
 * 
 * Each ride has:
 * - Pickup and destination coordinates
 * - Timestamps for each status change
 * - Fare calculation
 * - Rating and feedback
 */
@Entity
@Table(name = "rides")
public class Ride {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;
    
    // Pickup location
    @Column(nullable = false)
    private Double pickupLatitude;
    
    @Column(nullable = false)
    private Double pickupLongitude;
    
    private String pickupAddress;
    
    // Destination location
    @Column(nullable = false)
    private Double destinationLatitude;
    
    @Column(nullable = false)
    private Double destinationLongitude;
    
    private String destinationAddress;
    
    // Ride details
    private Double distance;  // in kilometers
    private Integer estimatedTime;  // in minutes
    private Double fare;  // in NPR
    
    // Ride status: REQUESTED, ACCEPTED, ARRIVING, IN_PROGRESS, COMPLETED, CANCELLED
    @Column(nullable = false)
    private String status = "REQUESTED";
    
    // Ride type: STANDARD, PREMIUM, POOL, BIKE
    private String rideType = "STANDARD";
    
    // Timestamps
    @Column(name = "requested_at")
    private LocalDateTime requestedAt;
    
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    // Rating and feedback
    private Integer userRating;  // 1-5 stars
    private String userFeedback;
    
    private Integer driverRating;
    private String driverFeedback;
    
    // Payment status
    private Boolean isPaid = false;
    private String paymentMethod;  // CASH, KHALTI, ESEWA, CARD
    
    // Constructors
    public Ride() {
        this.requestedAt = LocalDateTime.now();
    }
    
    public Ride(User user, Double pickupLatitude, Double pickupLongitude,
                Double destinationLatitude, Double destinationLongitude) {
        this.user = user;
        this.pickupLatitude = pickupLatitude;
        this.pickupLongitude = pickupLongitude;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
        this.requestedAt = LocalDateTime.now();
    }
    
    // Getters and Setters (abbreviated for space)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }
    
    public Double getPickupLatitude() { return pickupLatitude; }
    public void setPickupLatitude(Double pickupLatitude) { this.pickupLatitude = pickupLatitude; }
    
    public Double getPickupLongitude() { return pickupLongitude; }
    public void setPickupLongitude(Double pickupLongitude) { this.pickupLongitude = pickupLongitude; }
    
    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    
    public Double getDestinationLatitude() { return destinationLatitude; }
    public void setDestinationLatitude(Double destinationLatitude) { this.destinationLatitude = destinationLatitude; }
    
    public Double getDestinationLongitude() { return destinationLongitude; }
    public void setDestinationLongitude(Double destinationLongitude) { this.destinationLongitude = destinationLongitude; }
    
    public String getDestinationAddress() { return destinationAddress; }
    public void setDestinationAddress(String destinationAddress) { this.destinationAddress = destinationAddress; }
    
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
    
    public Integer getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(Integer estimatedTime) { this.estimatedTime = estimatedTime; }
    
    public Double getFare() { return fare; }
    public void setFare(Double fare) { this.fare = fare; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getRideType() { return rideType; }
    public void setRideType(String rideType) { this.rideType = rideType; }
    
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    
    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public Integer getUserRating() { return userRating; }
    public void setUserRating(Integer userRating) { this.userRating = userRating; }
    
    public String getUserFeedback() { return userFeedback; }
    public void setUserFeedback(String userFeedback) { this.userFeedback = userFeedback; }
    
    public Integer getDriverRating() { return driverRating; }
    public void setDriverRating(Integer driverRating) { this.driverRating = driverRating; }
    
    public String getDriverFeedback() { return driverFeedback; }
    public void setDriverFeedback(String driverFeedback) { this.driverFeedback = driverFeedback; }
    
    public Boolean getIsPaid() { return isPaid; }
    public void setIsPaid(Boolean isPaid) { this.isPaid = isPaid; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
