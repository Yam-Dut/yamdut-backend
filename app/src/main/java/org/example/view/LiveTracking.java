package org.example.view;

import javafx.scene.web.WebEngine;

import java.util.Timer;
import java.util.TimerTask;

public class LiveTracking {

    private final WebEngine engine;
    private double lat = 27.7172;
    private double lng = 85.3240;

    public LiveTracking(WebEngine engine) {
        this.engine = engine;
    }

    public void startTracking() {
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                lat += 0.0002;
                lng += 0.0002;

                engine.executeScript(
                    "updateDriverLocation(" + lat + "," + lng + ");"
                );
            }
        }, 3000, 3000);
    }
}
