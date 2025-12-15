package org.example.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class MapView extends Application {

    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        var url = getClass().getResource("/web/map.html");
        if (url == null) {
            engine.loadContent("<h1>map.html not found</h1>");
        } else {
            engine.load(url.toExternalForm());
            engine.getLoadWorker().stateProperty().addListener((obs, o, n) -> {
                if (n == javafx.concurrent.Worker.State.SUCCEEDED) {
                    engine.executeScript("window.MAPBOX_TOKEN = '" + System.getenv("MAPBOX_TOKEN") + "'");
                }
            });
        }

        stage.setScene(new Scene(webView, 1200, 800));
        stage.setTitle("Yamdut - Live Tracking");
        stage.show();
    }
}
