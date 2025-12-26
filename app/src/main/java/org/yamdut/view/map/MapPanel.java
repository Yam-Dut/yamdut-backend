package org.yamdut.view.map;


import java.awt.BorderLayout;

import javax.swing.JPanel;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;



public class MapPanel extends JPanel {
    private final JFXPanel fxPanel;
    private WebEngine webEngine;

    public interface MapClickListener {
        void onMapClick(double lat, double lon);
    }
    private MapClickListener clickListener;

    public MapPanel() {
        setLayout(new BorderLayout());
        fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);
        
        initFX();
    }

    private void initFX() {
        Platform.runLater(() -> {
            WebView webView = new WebView();
            webEngine = webView.getEngine();

            //Loading the map.html
            String url = getClass().getResource("/map/map.html").toExternalForm();
            webEngine.load(url);
            
            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("javaConnector", new JavaConnector());

                }
            });

            fxPanel.setScene(new Scene(webView));
        });
    }

    public void setMapClickListener(MapClickListener listener) {
        this.clickListener = listener;
    }

    public void fireMapClick(double lat, double lon) {
        if (clickListener != null) {
            clickListener.onMapClick(lat, lon);
        }
    }
    public void setCenter(double lat, double lng, int zoom) {
        Platform.runLater(() -> {
            webEngine.executeScript(
                "YamdutMap.setCenter(" + lat + ", " + lng + ", " + zoom + ");"
            );
        });
    }

    public void addPickupMarker(double lat, double lon, String id) {
        Platform.runLater(() -> {
            webEngine.executeScript(
                "YamdutMap.addPickupMarker('" + id + "', " + lat + ", " + lon + ");"
            );
        });
    }

    public void addDestinationMarker(double lat, double lon, String id) {
        Platform.runLater(() -> {
            webEngine.executeScript(
                "YamdutMap.addDestinationMarker('" + id + "', " + lat + ", " + lon + ");"
            );
        });
    }

    public void addDriverMarker(double lat, double lon, String id, String name) {
        Platform.runLater(() -> {
            webEngine.executeScript(
                "YamdutMap.addDriverMarker('" + id + "', " + lat + ", " + lon + ", '" + name + "');"
            );
        });
    }
    public void showRoute(double startLat, double startLng, double endLat, double endLng) {
        Platform.runLater(() -> {
            webEngine.executeScript("YamdutMap.setRoute([" + startLat + "," + startLng + "], [" 
                                + endLat + "," + endLng + "]);");
        });
    }
    

    public void clearRoute() {
        Platform.runLater(() -> {
            webEngine.executeScript("YamdutMap.clearRoute();");
        });
    }
    
    /** 
     show entities (driver passenger on the map)
     @param jsonData JSON string [{id:"1", name:"Driver1", lat:"78.32", lon:"2380.9", type:"driver"}....]
     * **/
    public void showEntities(String jsonData) {
        Platform.runLater(() -> {
            webEngine.executeScript("YamdutMap.showEntities(" + jsonData + ");");
        });
    }
    
    /** 
     @param routeJson JSON array string [{lat:"12.4", lon:"23.53"}]
     **/


     //make the javasript function too for this 

    public void drawRoute(String routeJson) {
        Platform.runLater(() -> webEngine.executeScript("YamdutMap.drawRoute(" + routeJson + "); YamdutMap.startSimulation();"));
    }

    public void stopSimulation() {
        Platform.runLater(() -> webEngine.executeScript("YamdutMap.stopSimulation();"));
    }
    public void updateEntityPosition(String entityID, double lat, double lon) {
        Platform.runLater(() -> webEngine.executeScript(
            "YamdutMap.updateEntityPosition('" + entityID + "', {lat:" + lat + ", lon:" + lon + "});"
        ));
    }
    public void clearMap() {
        Platform.runLater(() -> webEngine.executeScript("YamditMap.clearMap();"));
    }

    public void setRoute(double startLat, double startLng, double endLat, double endLng) {
        Platform.runLater(() -> {
            webEngine.executeScript("YamdutMap.setRoute([" + startLat + "," + startLng + "], [" 
                                + endLat + "," + endLng + "]);");
            });
        }

    public void addPassengerMarker(double lat, double lon, String id) {
        Platform.runLater(() -> {
            webEngine.executeScript("YamdutMap.addMarker('" + id + "', " + lat + ", " + lon + ", 'passenger');");
        });
    }

    public void addDriverMarker(double lat, double lon, String id) {
        Platform.runLater(() -> {
            webEngine.executeScript("YamdutMap.addMarker('" + id + "', " + lat + ", " + lon + ", 'driver');");
        });
    }


    public class JavaConnector {
        public void recieveMapClick(double lat, double lon) {
            fireMapClick(lat, lon);
        }
        public void recieveMessage(String msg) {
            System.out.println("JS says" + msg);
        }
    }
}