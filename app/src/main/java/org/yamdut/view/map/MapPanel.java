package org.yamdut.view.map;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.yamdut.model.RideRequest;
import org.yamdut.model.Role;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class MapPanel extends JPanel {
    private final JFXPanel fxPanel;
    private WebEngine engine;
    private final Role role;
    private static boolean tileServerStarted = false;
    private WebView webView;

    public interface MapClickListener {
        void onMapClick(double lat, double lon);
    }

    public MapPanel(Role role) {
        this.role = role;
        setLayout(new BorderLayout());
        
        fxPanel = new JFXPanel();
        fxPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        add(fxPanel, BorderLayout.CENTER);

        synchronized (MapPanel.class) {
            if (!tileServerStarted) {
                try {
                    TileServer.getInstance().start();
                    tileServerStarted = true;
                } catch (IOException e) {
                    System.out.println("[MapPanel] Tile server already running or port in use - continuing...");
                }
            }
        }

        initFX();
        
        // Add component listener to handle resizing
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                resize();
            }
        });
    }

    private void initFX() {
        Platform.runLater(() -> {
            webView = new WebView();
            webView.setPrefSize(800, 600);
            engine = webView.getEngine();
            
            engine.setOnAlert(event -> System.out.println("[JS Alert] " + event.getData()));
            
            engine.getLoadWorker().stateProperty().addListener((obs, old, newState) -> {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    System.out.println("Map loaded — initializing JS…");
                    
                    JSObject window = (JSObject) engine.executeScript("window");
                    window.setMember("javaConnector", new JavaConnector());
                    
                    engine.executeScript("YamdutMap.init('" + role.name().toLowerCase() + "')");
                    
                    // Multiple resize attempts to fix rendering
                    scheduleResize(100);
                    scheduleResize(300);
                    scheduleResize(500);
                    scheduleResize(1000);
                    scheduleResize(2000);
                    
                } else if (newState == javafx.concurrent.Worker.State.FAILED) {
                    System.err.println("Map loading failed!");
                }
            });

            URL url = getClass().getResource("/map/index.html");
            if (url == null) {
                throw new IllegalStateException("Map HTML not found at /map/index.html");
            }
            
            System.out.println("Loading map from: " + url.toExternalForm());
            engine.load(url.toExternalForm());
            
            Scene scene = new Scene(webView);
            fxPanel.setScene(scene);
        });
    }
    
    private void scheduleResize(int delayMs) {
        Timer timer = new Timer(delayMs, e -> {
            Platform.runLater(() -> {
                try {
                    engine.executeScript("if(typeof YamdutMap !== 'undefined') { YamdutMap.resize(); YamdutMap.setCenter(27.7172, 85.3240, 14); }");
                } catch (Exception ex) {
                    // Ignore if map not ready yet
                }
            });
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    public void resize() {
        if (engine != null) {
            Platform.runLater(() -> {
                try {
                    int width = getWidth();
                    int height = getHeight();
                    webView.setPrefSize(width, height);
                    webView.setMinSize(width, height);
                    webView.setMaxSize(width, height);
                    engine.executeScript("if(typeof YamdutMap !== 'undefined') YamdutMap.resize();");
                } catch (Exception e) {
                    // Ignore
                }
            });
        }
    }
    
    public void setCenter(double lat, double lng, int zoom) {
        Platform.runLater(() -> {
            try {
                engine.executeScript("YamdutMap.setCenter(" + lat + "," + lng + "," + zoom + ");");
            } catch (Exception e) {
                System.err.println("Error setting center: " + e.getMessage());
            }
        });
    }

    public void showEntities(String jsonData) {
        Platform.runLater(() -> {
            try {
                engine.executeScript("YamdutMap.showEntities(" + jsonData + ");");
            } catch (Exception e) {
                System.err.println("Error showing entities: " + e.getMessage());
            }
        });
    }

    public void updateEntityPosition(String id, double lat, double lon) {
        Platform.runLater(() -> {
            try {
                engine.executeScript("YamdutMap.updateEntityPosition('" + id + "'," + lat + "," + lon + ");");
            } catch (Exception e) {
                System.err.println("Error updating position: " + e.getMessage());
            }
        });
    }

    public void setRoute(double startLat, double startLon, double endLat, double endLon) {
        Platform.runLater(() -> {
            try {
                engine.executeScript("YamdutMap.setRoute([" + startLat + "," + startLon + "], [" 
                                    + endLat + "," + endLon + "]);");
            } catch (Exception e) {
                System.err.println("Error setting route: " + e.getMessage());
            }
        });
    }

    public void clearRoute() {
        Platform.runLater(() -> {
            try {
                engine.executeScript("YamdutMap.clearRoute();");
            } catch (Exception e) {
                System.err.println("Error clearing route: " + e.getMessage());
            }
        });
    }

    public void clearMap() {
        Platform.runLater(() -> {
            try {
                engine.executeScript("YamdutMap.clearMap();");
            } catch (Exception e) {
                System.err.println("Error clearing map: " + e.getMessage());
            }
        });
    }

    public void showPickupAndDestination(String pickup, String destination) {
        System.out.println("[MAP] Showing pickup: " + pickup + " | destination: " + destination);
    }

    public void showRide(RideRequest request) {
        System.out.println("[MAP] Showing accepted ride: " + request);
    }

    public class JavaConnector {
        public void recieveMapClick(double lat, double lon) {
            System.out.println("[MAP CLICK] Lat: " + lat + ", Lon: " + lon);
        }
        
        public void logDebug(String message) {
            System.out.println("[MAP DEBUG] " + message);
        }
    }
}