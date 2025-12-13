package org.example.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;

public class MapView extends Application {

    @Override
    public void start(Stage stage) {

        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        URL url = getClass().getResource("/web/map.html");
        engine.load(url.toExternalForm());

        Scene scene = new Scene(webView, 1200, 800);
        stage.setTitle("Yamdut - Live Ride Tracking");
        stage.setScene(scene);
        stage.show();
    }
}
