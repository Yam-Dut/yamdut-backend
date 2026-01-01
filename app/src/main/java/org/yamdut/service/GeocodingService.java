package org.yamdut.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

public class GeocodingService {
    private static GeocodingService instance;
    
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
    
    private GeocodingService() {}
    
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
                              "&format=json&limit=1&addressdetails=1";
            
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "YamdutApp/1.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return new GeocodeResult(false, "Geocoding service unavailable", 0, 0);
            }
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
            );
            
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
            String displayName = firstResult.getString("display_name");
            
            return new GeocodeResult(true, displayName, lat, lon);
            
        } catch (Exception e) {
            System.err.println("[Geocoding] Error: " + e.getMessage());
            return new GeocodeResult(false, "Error: " + e.getMessage(), 0, 0);
        }
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
