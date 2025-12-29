package org.yamdut.view.map;

import java.awt.BorderLayout;
import java.net.URL;

import javax.swing.JPanel;

import org.yamdut.model.RideRequest;
import org.yamdut.model.Role;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;



public class MapPanel extends JPanel {
    private final JFXPanel fxPanel;
    private WebEngine engine;
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
            engine = webView.getEngine();

            engine.getLoadWorker().stateProperty().addListener((obs, old, newState) -> {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    System.out.println("Map loaded — initializing JS…");

                    Platform.runLater(() -> {
                        engine.executeScript("YamdutMap.init('" + role.name().toLowerCase() + "')");
                        engine.executeScript("YamdutMap.resize()");
                    });
                }
            });

            //URL url = getClass().getResource("/map/index.html");

            //if (url == null) throw new IllegalStateException("Map HTML not found on classpath");

            //engine.load(url.toExternalForm());
            engine.load("http://localhost:8080/index.html");
            fxPanel.setScene(new Scene(webView));
        });
    }


    // private String toJsRole(Role role) {
    //     return switch (role) {
    //         case DRIVER -> "driver";
    //         case PASSENGER -> "passenger";
    //         case ADMIN -> "admin";
    //     };
    // }

    public void setCenter(double lat, double lng, int zoom) {
        Platform.runLater(() -> 
            engine.executeScript(
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
            engine.executeScript("YamdutMap.showEntities(" + jsonData + ");");
        });
    }
    
    public void updateEntityPosition(String id, double lat, double lon) {
        Platform.runLater(() ->
            engine.executeScript(
                "YamdutMap.updateEntityPosition('" +
                id + "'," + lat + "," + lon + ");"
            )
        );
    }    

    public void setRoute(double startLat, double startLon, double endLat, double endLon) {
        Platform.runLater(() -> {
            engine.executeScript("YamdutMap.setRoute([" + startLat + "," + startLon + "], [" 
                                + endLat + "," + endLon + "]);");
            });
        }

    public void clearRoute() {
        Platform.runLater(() ->
            engine.executeScript("YamdutMap.clearRoute();")
        );
    }

    public void clearMap() {
        Platform.runLater(() ->
            engine.executeScript("YamdutMap.clearMap();")
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
