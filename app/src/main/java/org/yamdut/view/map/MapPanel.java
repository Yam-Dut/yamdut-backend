package org.yamdut.view.map;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Reusable map component backed by JavaFX WebView + Leaflet (web-map.html).
 * - Loads OSM tiles over HTTPS
 * - Supports pan/zoom
 * - Exposes setRoute(...) for Java -> JS
 */
public class MapPanel extends JPanel {

    private final AtomicBoolean fxSceneInitialized = new AtomicBoolean(false);

    private final JFXPanel fxPanel = new JFXPanel();
    private WebEngine webEngine;
    private MapClickListener mapClickListener;

    public MapPanel() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(900, 600));
        add(fxPanel, BorderLayout.CENTER);

        // JFXPanel ensures toolkit is started; just schedule scene setup once
        Platform.runLater(() -> {
            if (fxSceneInitialized.compareAndSet(false, true)) {
                Platform.setImplicitExit(false);
                initFxScene();
            }
        });
    }

    private void initFxScene() {
        WebView webView = new WebView();
        webEngine = webView.getEngine();

        URL url = getClass().getResource("/map/web-map.html");
        if (url != null) {
            System.out.println("MapPanel: loading " + url);
            webEngine.load(url.toExternalForm());
        } else {
            System.out.println("MapPanel: web-map.html not found on classpath");
            webEngine.loadContent("<html><body><h3>web-map.html not found</h3></body></html>");
        }

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            System.out.println("MapPanel load state: " + newState);
        });

        // Optional bridge: JS can call window.javaBridge.*
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember("javaBridge", new JavaBridge());

        fxPanel.setScene(new Scene(webView));
    }

    /** Center the map from Java (used e.g. for Kathmandu default). */
    public void setCenter(double lat, double lng, int zoom) {
        if (webEngine == null) return;
        Platform.runLater(() -> {
            String script = String.format("setCenter(%f,%f,%d);", lat, lng, zoom);
            webEngine.executeScript(script);
        });
    }

    /** Draw/update a route polyline between origin & destination. */
    public void setRoute(double fromLat, double fromLng,
                         double toLat, double toLng) {
        if (webEngine == null) return;
        Platform.runLater(() -> {
            String script = String.format(
                    "setRoute(%f,%f,%f,%f);",
                    fromLat, fromLng, toLat, toLng
            );
            webEngine.executeScript(script);
        });
    }

    /** Clear markers/route from the web map. */
    public void clearRoute() {
        if (webEngine == null) return;
        Platform.runLater(() -> webEngine.executeScript("clearRoute();"));
    }

    /** Listen for map clicks coming from JS (Leaflet). */
    public void setMapClickListener(MapClickListener listener) {
        this.mapClickListener = listener;
    }

    public interface MapClickListener {
        void onMapClick(double lat, double lng);
    }

    /** Example Java <-> JS bridge (extend as needed). */
    public class JavaBridge {
        // Called from JS: window.javaBridge.log('msg')
        public void log(String msg) {
            System.out.println("JS: " + msg);
        }

        public void onMapClick(double lat, double lng) {
            if (mapClickListener != null) {
                SwingUtilities.invokeLater(() -> mapClickListener.onMapClick(lat, lng));
            }
        }
    }
}