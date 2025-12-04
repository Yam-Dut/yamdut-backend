package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * DRIVER ENTITY MODEL - REPRESENTS A DRIVER IN THE DATABASE
 * 
 * Drivers have:
 * - Personal information
 * - Vehicle details
 * - Current location (for real-time tracking)
 * - Availability status
 * - Rating based on user feedback
 * 
 * In a real app, you'd need:
 * - License verification
 * - Vehicle documents
 * - Background checks
 * 
 * For college project, we keep it simple.
 */
@Entity
@Table(name = "drivers")
public class Driver {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String phone;
    
    private String email;
    
    @Column(nullable = false)
    private String licenseNumber;
    
    @Column(nullable = false)
    private String vehicleModel;
    
    @Column(nullable = false)
    private String vehicleNumber;  // e.g., "BA 1 PA 1234"
    
    @Column(nullable = false)
    private String vehicleColor;
    
    // Current location (for real-time tracking)
    private Double currentLatitude;
    private Double currentLongitude;
    
    // Availability status: AVAILABLE, BUSY, OFFLINE
    @Column(nullable = false)
    private String status = "OFFLINE";
    
    // Driver rating (1-5 stars)
    private Double rating = 5.0;
    
    @Column(name = "total_rides")
    private Integer totalRides = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public Driver() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Driver(String name, String phone, String licenseNumber, 
                  String vehicleModel, String vehicleNumber, String vehicleColor) {
        this.name = name;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.vehicleModel = vehicleModel;
        this.vehicleNumber = vehicleNumber;
        this.vehicleColor = vehicleColor;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    
    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
    
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    
    public String getVehicleColor() { return vehicleColor; }
    public void setVehicleColor(String vehicleColor) { this.vehicleColor = vehicleColor; }
    
    public Double getCurrentLatitude() { return currentLatitude; }
    public void setCurrentLatitude(Double currentLatitude) { this.currentLatitude = currentLatitude; }
    
    public Double getCurrentLongitude() { return currentLongitude; }
    public void setCurrentLongitude(Double currentLongitude) { this.currentLongitude = currentLongitude; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    
    public Integer getTotalRides() { return totalRides; }
    public void setTotalRides(Integer totalRides) { this.totalRides = totalRides; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
