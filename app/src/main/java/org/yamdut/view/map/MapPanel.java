package org.yamdut.view.map;



import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import javafx.scene.web.WebEngine;

import javax.swing.*;
import java.awt.*;


/**
 * Reusable map component backed by JavaFX WebView + Leaflet (web-map.html).
 * - Loads OSM tiles over HTTPS
 * - Supports pan/zoom
 * - Exposes setRoute(...) for Java -> JS
 */
public class MapPanel extends JPanel {
    private final JFXPanel fxPanel;
    private WebEngine webEngine;

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
    /** 
     show entities (driver passenger on the map)
     @param jsonData JSON string [{id:"1", name:"Driver1", lat:"78.32", lon:"2380.9", type:"driver"}....]
     * **/
    public void showEntities(String jsonData) {
        Platform.runLater(() -> {
            webEngine.executeScript("YamdutMap.showEntites(" + jsonData + ");");
        });
    }
    
    /** 
     @param routeJson JSON array string [{lat:"12.4", lon:"23.53"}]
     **/
    public void drawRoute(String routeJSon) {
        Platform.runLater(() -> webEngine.executeScript("YamdutMap.startSimulation();"));
    }

    public void stopSimulation() {
        Platform.runLater(() -> webEngine.executeScript("YamdutMap.stopSimulation();"));
    }



    public static class JavaConnector {
        public void recieveMessage(String msg) {
            System.out.println("JS says" + msg);
        }
    }
}