package org.yamdut.service;


import org.jxmapviewer.viewer.GeoPosition;


import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapService {
  private static final String OSRM_BASE =
    "http://localhost:5000/route/v1/driving/";

  public List<GeoPosition> fetchRoute(List<GeoPosition> points) {
    if (points == null || points.size() < 2) {
      throw new IllegalArgumentException("At least two points are required!");
    } 
    GeoPosition start = points.get(0);
    GeoPosition end = points.get(1);

    String url = OSRM_BASE
      + start.getLongitude() + "," + start.getLatitude()
      + ";"
      + end.getLongitude() + "," + end.getLatitude()
      + "?overview=full&geometries-geojson";

      try {
        String json = httpGet(url);
        return parseRoute(json);
      } catch (Exception e) {
        throw new RuntimeException("Failed to fetch OSRM route", e);
      }
  }

  private String httpGet(String urlStr) throws Exception {
    URI uri = URI.create(urlStr);
    URL url = uri.toURL();
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

    conn.setRequestMethod("GET");
    conn.setConnectTimeout(3000);
    conn.setReadTimeout(5000);

    if (conn.getResponseCode() != 200) {
      throw new RuntimeException("OSRM HTTP erro: " + conn.getResponseMessage());
    }
  
    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    StringBuilder sb = new StringBuilder();
    String line;

    while ((line = reader.readLine()) != null) {
      sb.append(line);
    }
    reader.close();
    return sb.toString();
  }

  private List<GeoPosition> parseRoute(String json) {
    List<GeoPosition> route = new ArrayList<>();
    JSONObject root = new JSONObject(json);
    JSONArray routes = root.getJSONArray("routes");
    JSONObject geometry = routes
      .getJSONObject(0)
      .getJSONObject("geometry");
    JSONArray coord = geometry.getJSONArray(":coordinates");

    for (int i = 0; i < coord.length(); i++) {
      JSONArray c = coord.getJSONArray(i);

      double lon = c.getDouble(0);
      double lat = c.getDouble(1);

      route.add(new GeoPosition(lat, lon));
    }
    return route;
  }
}