package org.yamdut.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

public class GeocodingService {
    private static GeocodingService instance;
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

    private GeocodingService() {
    }

    public static GeocodingService getInstance() {
        if (instance == null) {
            instance = new GeocodingService();
        }
        return instance;
    }

    public GeocodeResult geocode(String locationName) {
        try {
            String encodedLocation = URLEncoder.encode(locationName, StandardCharsets.UTF_8);
            String urlString = NOMINATIM_URL + "?q=" + encodedLocation +
                    "&format=json&limit=1&addressdetails=1&accept-language=ne,en";

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "YamdutApp/1.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) {
                return new GeocodeResult(false, "Geocoding service unavailable", 0, 0);
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONArray results = new JSONArray(response.toString());
            if (results.length() == 0) {
                return new GeocodeResult(false, "Location not found", 0, 0);
            }

            JSONObject firstResult = results.getJSONObject(0);
            double lat = firstResult.getDouble("lat");
            double lon = firstResult.getDouble("lon");
            String fullAddress = firstResult.getString("display_name");
            String displayName = fullAddress.split(",")[0].trim();

            return new GeocodeResult(true, displayName, lat, lon);

        } catch (Exception e) {
            System.err.println("[Geocoding] Error: " + e.getMessage());
            return new GeocodeResult(false, "Error: " + e.getMessage(), 0, 0);
        }
    }

    public GeocodeResult reverseGeocode(double lat, double lon) {
        try {
            String urlString = "https://nominatim.openstreetmap.org/reverse?lat=" + lat +
                    "&lon=" + lon +
                    "&format=json&accept-language=ne,en";

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "YamdutApp/1.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) {
                return new GeocodeResult(false, String.format("%.5f, %.5f", lat, lon), lat, lon);
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject result = new JSONObject(response.toString());
            String fullAddress = result.optString("display_name", String.format("%.5f, %.5f", lat, lon));
            String displayName = fullAddress.split(",")[0].trim();

            return new GeocodeResult(true, displayName, lat, lon);

        } catch (Exception e) {
            System.err.println("[Geocoding] Reverse error: " + e.getMessage());
            return new GeocodeResult(false, String.format("%.5f, %.5f", lat, lon), lat, lon);
        }
    }

    public GeocodeResult getDeviceLocation() {
        // 1. Try Windows Location Service first (accurate)
        GeocodeResult winLoc = getWindowsLocation();
        if (winLoc.isSuccess()) {
            return winLoc;
        }

        // 2. Fallback to IP-based Geolocation (less accurate)
        try {
            URL url = new URL("http://ip-api.com/json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) {
                return new GeocodeResult(false, "IP Geolocation failed", 27.7270647, 85.3179259);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            JSONObject json = new JSONObject(sb.toString());
            if ("fail".equals(json.optString("status"))) {
                return new GeocodeResult(false, "Geolocation failed", 27.7270647, 85.3179259);
            }

            double lat = json.getDouble("lat");
            double lon = json.getDouble("lon");
            String city = json.optString("city", "Kathmandu");

            return new GeocodeResult(true, city, lat, lon);

        } catch (Exception e) {
            System.err.println("[Geocoding] Device location error: " + e.getMessage());
            return new GeocodeResult(false, "Kathmandu", 27.7270647, 85.3179259);
        }
    }

    private GeocodeResult getWindowsLocation() {
        try {
            // PowerShell command to get live location from Windows Location Service
            String psCommand = "Add-Type -AssemblyName System.Device; " +
                    "$watcher = New-Object System.Device.Location.GeoCoordinateWatcher; " +
                    "$watcher.Start(); " +
                    "while (($watcher.Status -ne 'Ready') -and ($watcher.Permission -ne 'Denied')) { Start-Sleep -Milliseconds 100 }; "
                    +
                    "if ($watcher.Status -eq 'Ready') { " +
                    "  $pos = $watcher.Position.Location; " +
                    "  if (!$pos.IsUnknown) { Write-Output \"$($pos.Latitude),$($pos.Longitude)\" } " +
                    "}";

            ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-Command", psCommand);
            Process p = pb.start();

            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line = r.readLine();
                if (line != null && line.contains(",")) {
                    String[] parts = line.split(",");
                    double lat = Double.parseDouble(parts[0]);
                    double lon = Double.parseDouble(parts[1]);
                    return new GeocodeResult(true, "Live Location", lat, lon);
                }
            }
        } catch (Exception e) {
            System.err.println("[Geocoding] Windows location failed: " + e.getMessage());
        }
        return new GeocodeResult(false, "OS Location failed", 0, 0);
    }

    public static class GeocodeResult {
        private final boolean success;
        private final String address;
        private final double lat;
        private final double lon;

        public GeocodeResult(boolean success, String address, double lat, double lon) {
            this.success = success;
            this.address = address;
            this.lat = lat;
            this.lon = lon;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getAddress() {
            return address;
        }

        public double getLat() {
            return lat;
        }

        public double getLon() {
            return lon;
        }
    }
}
