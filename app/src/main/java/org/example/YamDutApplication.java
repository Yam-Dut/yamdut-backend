package org.example;

import org.example.desktop.MapDesktopApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import javax.swing.*;

/**
 * MAIN APPLICATION - STARTS BOTH SPRING BOOT BACKEND AND DESKTOP MAP APP
 * 
 * This application runs in two modes:
 * 
 * 1. BACKEND MODE (Spring Boot):
 *    - REST API server on http://localhost:8080
 *    - Handles user registration, ride booking, etc.
 *    - Database access for storing ride information
 *    
 * 2. DESKTOP MODE (Swing Application):
 *    - Interactive map viewer
 *    - Zoom and pan capabilities
 *    - Pin placement for destinations
 *    - Useful for testing and demonstration
 *    
 * How to run:
 * - Run this class as Java Application
 * - Backend starts on port 8080
 * - Desktop map window opens automatically
 * 
 * API Endpoints:
 * - GET http://localhost:8080/api/health - Check if backend is running
 * - POST http://localhost:8080/api/rides/book - Book a ride
 * - GET http://localhost:8080/api/map/locations - Get Kathmandu locations
 */
@SpringBootApplication
public class YamDutApplication {
    
    public static void main(String[] args) {
        // Start Spring Boot backend
        ConfigurableApplicationContext context = SpringApplication.run(YamDutApplication.class, args);
        
        System.out.println("=========================================");
        System.out.println("🚗 YAMDUT - Ride Sharing for Kathmandu");
        System.out.println("=========================================");
        System.out.println("Backend API: http://localhost:8080");
        System.out.println("Database Console: http://localhost:8080/h2-console");
        System.out.println("Use credentials from application.properties");
        System.out.println("=========================================");
        
        // Start desktop map application
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                MapDesktopApp desktopApp = new MapDesktopApp();
                desktopApp.setVisible(true);
                System.out.println("✅ Desktop map application started");
            } catch (Exception e) {
                System.err.println("❌ Failed to start desktop application: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
