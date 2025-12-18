package org.yamdut.view.components;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class MapPanel extends JPanel {

    private WebEngine webEngine;
    private double lat = 27.7172;
    private double lng = 85.3240;
    private Timer trackingTimer;

    public MapPanel() {
        super(new BorderLayout());
        setPreferredSize(new Dimension(900, 600));
        
        // Initialize JavaFX toolkit
        JFXPanel jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);
        
        // Create and configure JavaFX WebView on JavaFX thread
        Platform.runLater(() -> {
            WebView webView = new WebView();
            webEngine = webView.getEngine();
            
            // Enable JavaScript
            webEngine.setJavaScriptEnabled(true);
            
            // Load the map HTML file
            String mapUrl = getClass().getResource("/map/map.html").toExternalForm();
            webEngine.load(mapUrl);
            
            // Wait for document to load before setting token and starting tracking
            webEngine.documentProperty().addListener((obs, oldDoc, newDoc) -> {
                if (newDoc != null) {
                    // Optional: Update Mapbox token from properties if needed
                    String token = loadMapboxToken();
                    if (token != null && !token.equals("Token not found")) {
                        webEngine.executeScript("setMapboxToken('" + token + "');");
                    }
                    
                    // Start live tracking simulation
                    startLiveTracking();
                }
            });
            
            // Create scene and set it to the JFXPanel
            StackPane root = new StackPane();
            root.getChildren().add(webView);
            Scene scene = new Scene(root);
            jfxPanel.setScene(scene);
        });
    }

    private String loadMapboxToken() {
        try (InputStream is = getClass().getResourceAsStream("/config/application.properties")) {
            if (is == null) {
                return "Token not found";
            }
            Properties props = new Properties();
            props.load(is);
            return props.getProperty("mapbox.token", "Token not found");
        } catch (Exception e) {
            return "Token not found";
        }
    }

    private void startLiveTracking() {
        // Simulate driver movement
        trackingTimer = new Timer();
        trackingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Move driver position slightly
                lat += 0.00015;
                lng += 0.00015;
                
                // Update map on JavaFX thread
                Platform.runLater(() -> {
                    if (webEngine != null) {
                        webEngine.executeScript(
                            "updateDriverLocation(" + lat + ", " + lng + ");"
                        );
                    }
                });
            }
        }, 3000, 3000); // Update every 3 seconds
    }
    
    public void stopTracking() {
        if (trackingTimer != null) {
            trackingTimer.cancel();
            trackingTimer = null;
        }
    }
}
