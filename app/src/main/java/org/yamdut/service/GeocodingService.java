package org.yamdut.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.IOException;

/**
 * Reverse geocoding using OpenStreetMap Nominatim public API.
 */
public class GeocodingService {

    private static final String NOMINATIM =
            "https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f";

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public String getAddress(GeoPosition pos) throws IOException {
        String url = String.format(NOMINATIM, pos.getLatitude(), pos.getLongitude());

        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "YamdutRideshare/1.0")
                .build();

        try (Response resp = client.newCall(request).execute()) {
            if (!resp.isSuccessful() || resp.body() == null) {
                throw new IOException("Nominatim error: " + resp.code());
            }

            String body = resp.body().string();
            JsonObject json = gson.fromJson(body, JsonObject.class);

            if (json == null || !json.has("display_name")) {
                throw new IOException("Invalid response from Nominatim");
            }

            return json.get("display_name").getAsString();
        }
    }
}



