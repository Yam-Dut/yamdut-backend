package org.yamdut.view.map;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import org.yamdut.model.*;



public class MapPanel extends JPanel {
    private final JFXPanel fxPanel;
    private WebEngine webEngine;
    private final Role role;

    public interface MapClickListener {
        void onMapClick(double lat, double lon);
    }

    public MapPanel(Role role) {
        this.role = role;
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
                    
                    String jsRole = toJsRole(role);
                    webEngine.executeScript(
                        "YamdutMap.init('" + jsRole + "');"
                    );
                }
            });

            fxPanel.setScene(new Scene(webView));
        });
    }

    private String toJsRole(Role role) {
        return switch (role) {
            case DRIVER -> "driver";
            case PASSENGER -> "passenger";
            case ADMIN -> "admin";
        };
    }

    public void setCenter(double lat, double lng, int zoom) {
        Platform.runLater(() -> 
            webEngine.executeScript(
                "YamdutMap.setCenter(" + lat + "," + lng + "," + zoom + ");"
                )
        );
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
    
    public void updateEntityPosition(String id, double lat, double lon) {
        Platform.runLater(() ->
            webEngine.executeScript(
                "YamdutMap.updateEntityPosition('" +
                id + "'," + lat + "," + lon + ");"
            )
        );
    }    

    public void setRoute(double startLat, double startLon, double endLat, double endLon) {
        Platform.runLater(() -> {
            webEngine.executeScript("YamdutMap.setRoute([" + startLat + "," + startLon + "], [" 
                                + endLat + "," + endLon + "]);");
            });
        }

    public void clearRoute() {
        Platform.runLater(() ->
            webEngine.executeScript("YamdutMap.clearRoute();")
        );
    }

    public void clearMap() {
        Platform.runLater(() ->
            webEngine.executeScript("YamdutMap.clearMap();")
        );
    }

    public void showPickupAndDestination(String pickup, String destination) {
        System.out.println(
            "[MAP] Showing pickup: " + pickup +
            " | destination: " + destination
        );

        // later:
        // - geocode pickup
        // - geocode destination
        // - draw markers + route
    }
    
    public void showRide(RideRequest request) {
        System.out.println(
            "[MAP] Showing accepted ride: " + request
        );

        // later:
        // - show passenger pickup
        // - show route
        // - follow driver location
    }

    public class JavaConnector {
        public void recieveMessage(String msg) {
            System.out.println("JS says" + msg);
        }
    }
}