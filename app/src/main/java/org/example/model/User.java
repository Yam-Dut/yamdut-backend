package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * USER ENTITY MODEL - REPRESENTS A USER/CUSTOMER IN THE DATABASE
 * 
 * This class maps to a database table called "users".
 * Each user can have multiple rides (One-to-Many relationship).
 * 
 * @Entity: Marks this as a JPA entity (database table)
 * @Table: Specifies the table name
 * @Id: Marks the primary key
 * @GeneratedValue: Auto-generates IDs
 * 
 * Fields represent columns in the database:
 * - id: Primary key
 * - name: User's full name
 * - email: For communication
 * - phone: Must be unique (Nepali phone numbers)
 * - password: Should be encrypted (hashed) before storing
 * - createdAt: When the account was created
 * - rides: List of rides this user has taken
 */
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(unique = true, nullable = false)
    private String phone;
    
    @Column(nullable = false)
    private String password;  // Should be hashed
    
    private String profilePicture;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Ride> rides = new ArrayList<>();
    
    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
    }
    
    public User(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public List<Ride> getRides() { return rides; }
    public void setRides(List<Ride> rides) { this.rides = rides; }
}
