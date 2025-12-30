package org.yamdut.view.map;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.yamdut.model.RideRequest;
import org.yamdut.model.Role;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class MapPanel extends JPanel {

    private final JFXPanel fxPanel;
    private WebEngine engine;
    private WebView webView;
    private final Role role;
    private boolean mapInitialized = false;

    public MapPanel(Role role) {
        this.role = role;
        setLayout(new BorderLayout());

        fxPanel = new JFXPanel();
        // Don't set preferred size - let it fill the panel
        add(fxPanel, BorderLayout.CENTER);

        initFX();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Update scene size when panel resizes
                Platform.runLater(() -> {
                    if (fxPanel != null && fxPanel.getScene() != null) {
                        int w = Math.max(400, getWidth());
                        int h = Math.max(300, getHeight());
                        Scene scene = fxPanel.getScene();
                        if (scene != null) {
                            scene.getWindow().setWidth(w);
                            scene.getWindow().setHeight(h);
                        }
                    }
                });
                // Also resize the map
                scheduleResize(100);
            }
        });
    }

    private void initFX() {
        Platform.runLater(() -> {
            try {
                webView = new WebView();
                engine = webView.getEngine();

                // Enable JavaScript
                engine.setJavaScriptEnabled(true);
                
                // Optimize WebView settings
                webView.setCache(true);
                webView.setContextMenuEnabled(false);
                
                // Set user agent for better tile server compatibility
                engine.setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
                
                // Enable local storage for tile caching
                try {
                    engine.executeScript("if (typeof(Storage) !== 'undefined') { localStorage.setItem('test', 'ok'); }");
                } catch (Exception e) {
                    System.out.println("[MapPanel] LocalStorage test: " + e.getMessage());
                }

                // JS alert -> console
                engine.setOnAlert(e -> System.out.println("[JS Alert] " + e.getData()));

                // Handle console messages
                engine.setOnError((e) -> {
                    System.err.println("[WebView Error] " + e.getMessage());
                });

                // On HTML load, initialize map
                engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        System.out.println("[MapPanel] HTML loaded successfully");
                        
                        // Register Java connector for JS -> Java communication
                        try {
                            JSObject window = (JSObject) engine.executeScript("window");
                            JavaConnector connector = new JavaConnector();
                            window.setMember("java", connector);
                            System.out.println("[MapPanel] Java connector registered");
                        } catch (Exception e) {
                            System.err.println("[MapPanel] Error registering Java connector: " + e.getMessage());
                        }
                        
                        // Use a timer instead of blocking sleep for better performance
                        Timer initTimer = new Timer(300, e -> {
                            Platform.runLater(() -> {
                                String roleStr = role.name().toLowerCase();
                                System.out.println("[MapPanel] Initializing map with role: " + roleStr);
                                
                                // Initialize the map with proper error handling
                                String initScript = 
                                    "(function() {" +
                                    "  if (typeof YamdutMap !== 'undefined') {" +
                                    "    try {" +
                                    "      YamdutMap.init('" + roleStr + "');" +
                                    "      setTimeout(function() { " +
                                    "        if (YamdutMap && YamdutMap.resize) YamdutMap.resize();" +
                                    "      }, 100);" +
                                    "      return true;" +
                                    "    } catch(err) {" +
                                    "      console.error('Map init error:', err);" +
                                    "      return false;" +
                                    "    }" +
                                    "  } else {" +
                                    "    console.error('YamdutMap not found');" +
                                    "    return false;" +
                                    "  }" +
                                    "})()";
                                
                                try {
                                    Object result = engine.executeScript(initScript);
                                    if (result != null && result.equals(true)) {
                                        mapInitialized = true;
                                        System.out.println("[MapPanel] Map initialization successful");
                                        
                                        // Force resize after initialization with multiple attempts
                                        scheduleResize(200);
                                        scheduleResize(500);
                                        scheduleResize(1000);
                                    } else {
                                        System.err.println("[MapPanel] Map initialization returned false");
                                        // Retry once
                                        Timer retryTimer = new Timer(500, retry -> {
                                            Platform.runLater(() -> {
                                                try {
                                                    engine.executeScript(initScript);
                                                    mapInitialized = true;
                                                    scheduleResize(300);
                                                } catch (Exception ex) {
                                                    System.err.println("[MapPanel] Retry failed: " + ex.getMessage());
                                                }
                                            });
                                        });
                                        retryTimer.setRepeats(false);
                                        retryTimer.start();
                                    }
                                } catch (Exception ex) {
                                    System.err.println("[MapPanel] Error executing init script: " + ex.getMessage());
                                    ex.printStackTrace();
                                }
                            });
                        });
                        initTimer.setRepeats(false);
                        initTimer.start();
                    } else if (newState == Worker.State.FAILED) {
                        System.err.println("[MapPanel] Failed to load HTML: " + 
                            engine.getLoadWorker().getException());
                    }
                });

                // Load the HTML
                URL url = getClass().getResource("/map/index.html");
                if (url == null) {
                    throw new IllegalStateException("Map HTML not found at /map/index.html");
                }
                
                String htmlUrl = url.toExternalForm();
                System.out.println("[MapPanel] Loading HTML from: " + htmlUrl);
                engine.load(htmlUrl);

                // Set scene with proper sizing - get actual panel size
                int initialWidth = Math.max(600, getWidth() > 0 ? getWidth() : 800);
                int initialHeight = Math.max(400, getHeight() > 0 ? getHeight() : 600);
                
                Scene scene = new Scene(webView, initialWidth, initialHeight);
                fxPanel.setScene(scene);
                
                // Make WebView fill the scene
                webView.prefWidthProperty().bind(scene.widthProperty());
                webView.prefHeightProperty().bind(scene.heightProperty());
                
                // Update scene size when panel is resized - use existing listener
                // The componentResized in constructor will handle this
                
            } catch (Exception e) {
                System.err.println("[MapPanel] Error initializing WebView: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void scheduleResize(int delayMs) {
        Timer timer = new Timer(delayMs, e -> {
            Platform.runLater(() -> {
                if (engine != null && mapInitialized) {
                    try {
                        String resizeScript = 
                            "if (typeof YamdutMap !== 'undefined' && YamdutMap.resize) {" +
                            "  YamdutMap.resize();" +
                            "} else if (window.map && window.map.invalidateSize) {" +
                            "  window.map.invalidateSize(true);" +
                            "}";
                        engine.executeScript(resizeScript);
                    } catch (Exception ex) {
                        System.err.println("[MapPanel] Error resizing map: " + ex.getMessage());
                    }
                }
            });
        });
        timer.setRepeats(false);
        timer.start();
    }

    // Map API
    public void setCenter(double lat, double lon, int zoom) {
        Platform.runLater(() -> {
            if (engine != null && mapInitialized) {
                engine.executeScript("if (window.YamdutMap) YamdutMap.setCenter(" + lat + "," + lon + "," + zoom + ");");
            }
        });
    }

    public void showEntities(String json) {
        Platform.runLater(() -> {
            if (engine != null && mapInitialized) {
                engine.executeScript("if (window.YamdutMap) YamdutMap.showEntities(" + json + ");");
            }
        });
    }

    public void updateEntityPosition(String id, double lat, double lon) {
        Platform.runLater(() -> {
            if (engine != null && mapInitialized) {
                engine.executeScript(
                    "if (window.YamdutMap) YamdutMap.updateEntityPosition('" + id + "'," + lat + "," + lon + ");"
                );
            }
        });
    }

    public void setRoute(double slat, double slon, double elat, double elon) {
        Platform.runLater(() -> {
            if (engine != null && mapInitialized) {
                engine.executeScript(
                    "if (window.YamdutMap) YamdutMap.setRoute([" + slat + "," + slon + "],[" + elat + "," + elon + "]);"
                );
            }
        });
    }

    public void clearRoute() {
        Platform.runLater(() -> {
            if (engine != null && mapInitialized) {
                engine.executeScript("if (window.YamdutMap) YamdutMap.clearRoute();");
            }
        });
    }

    public void clearMap() {
        Platform.runLater(() -> {
            if (engine != null && mapInitialized) {
                engine.executeScript("if (window.YamdutMap) YamdutMap.clearMap();");
            }
        });
    }

    public void showRide(RideRequest request) {
        System.out.println("[MAP] Showing ride: " + request);
    }
    
    /**
     * Force map to refresh and resize - useful when panel becomes visible
     */
    public void refresh() {
        Platform.runLater(() -> {
            if (engine != null && mapInitialized) {
                scheduleResize(100);
                scheduleResize(300);
            } else if (engine != null) {
                // Try to reinitialize if not initialized
                String roleStr = role.name().toLowerCase();
                String initScript = 
                    "if (typeof YamdutMap !== 'undefined') {" +
                    "  YamdutMap.init('" + roleStr + "');" +
                    "  setTimeout(function() { if (YamdutMap.resize) YamdutMap.resize(); }, 200);" +
                    "}";
                try {
                    engine.executeScript(initScript);
                    mapInitialized = true;
                } catch (Exception e) {
                    System.err.println("[MapPanel] Error refreshing map: " + e.getMessage());
                }
            }
        });
    }

    // JS -> Java connector
    public class JavaConnector {
        public void recieveMapClick(double lat, double lon) {
            System.out.println("[MAP CLICK] " + lat + ", " + lon);
            // Notify controller if available
            if (mapClickListener != null) {
                mapClickListener.onMapClick(lat, lon);
            }
        }

        public void logDebug(String msg) {
            System.out.println("[MAP DEBUG] " + msg);
        }
    }
    
    // Map click listener interface
    public interface MapClickListener {
        void onMapClick(double lat, double lon);
    }
    
    private MapClickListener mapClickListener;
    
    public void setMapClickListener(MapClickListener listener) {
        this.mapClickListener = listener;
    }
}
