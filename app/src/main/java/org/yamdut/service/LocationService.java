package org.yamdut.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * Best-effort user location from simple IP-based geolocation.
 */
public class LocationService {

    private static final String IP_API_URL = "http://ip-api.com/json/";

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public GeoPosition getCurrentLocation() {
        try {
            Request request = new Request.Builder()
                    .url(IP_API_URL)
                    .build();

            try (Response resp = client.newCall(request).execute()) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    throw new IllegalStateException("IP-API error: " + resp.code());
                }

                String body = resp.body().string();
                JsonObject json = gson.fromJson(body, JsonObject.class);

                double lat = json.get("lat").getAsDouble();
                double lon = json.get("lon").getAsDouble();
                return new GeoPosition(lat, lon);
            }
        } catch (Exception e) {
            // Fallback: Kathmandu
            return new GeoPosition(27.7172, 85.3240);
        }
    }
}



