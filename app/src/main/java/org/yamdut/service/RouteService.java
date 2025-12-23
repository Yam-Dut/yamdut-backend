package org.yamdut.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Calls OSRM public routing API to fetch driving routes between two points.
 */
public class RouteService {

    private static final String OSRM_API = "http://router.project-osrm.org/route/v1/driving/";

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public RouteResult getRoute(GeoPosition from, GeoPosition to) throws IOException {
        String url = String.format(
                "%s%f,%f;%f,%f?overview=full&geometries=geojson",
                OSRM_API,
                from.getLongitude(), from.getLatitude(),
                to.getLongitude(), to.getLatitude()
        );

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response resp = client.newCall(request).execute()) {
            if (!resp.isSuccessful() || resp.body() == null) {
                throw new IOException("Unexpected response from OSRM: " + resp.code());
            }

            String body = resp.body().string();
            JsonObject json = gson.fromJson(body, JsonObject.class);

            JsonArray routes = json.getAsJsonArray("routes");
            if (routes == null || routes.size() == 0) {
                throw new IOException("No routes returned from OSRM");
            }

            JsonObject routeObj = routes.get(0).getAsJsonObject();

            double distanceMeters = routeObj.get("distance").getAsDouble();
            double durationSeconds = routeObj.get("duration").getAsDouble();

            JsonObject geometry = routeObj.getAsJsonObject("geometry");
            JsonArray coords = geometry.getAsJsonArray("coordinates");

            List<GeoPosition> points = new ArrayList<>();
            for (int i = 0; i < coords.size(); i++) {
                JsonArray c = coords.get(i).getAsJsonArray();
                double lon = c.get(0).getAsDouble();
                double lat = c.get(1).getAsDouble();
                points.add(new GeoPosition(lat, lon));
            }

            return new RouteResult(
                    points,
                    distanceMeters / 1000.0,
                    durationSeconds / 60.0
            );
        }
    }

    /**
     * Simple value object representing a route polyline and summary stats.
     */
    public static class RouteResult {
        private final List<GeoPosition> points;
        private final double distanceKm;
        private final double durationMinutes;

        public RouteResult(List<GeoPosition> points, double distanceKm, double durationMinutes) {
            this.points = points;
            this.distanceKm = distanceKm;
            this.durationMinutes = durationMinutes;
        }

        public List<GeoPosition> getPoints() {
            return points;
        }

        public double getDistanceKm() {
            return distanceKm;
        }

        public double getDurationMinutes() {
            return durationMinutes;
        }
    }
}



