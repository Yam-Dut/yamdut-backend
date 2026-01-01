package org.yamdut.view.map;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;
import org.yamdut.model.RideRequest;
import org.yamdut.model.Role;

public class MapPanel extends JPanel {

    private JMapViewer mapViewer;
    private final Role role;
    private final Map<String, MapMarkerDot> entityMarkers = new HashMap<>(); // ID -> Marker
    private MapPolygon currentRoute;

    public MapPanel(Role role) {
        this.role = role;
        setLayout(new BorderLayout());

        // CRITICAL: Set User-Agent for OSM
        System.setProperty("http.agent", "YamDut/1.0");

        mapViewer = new JMapViewer();

        // Default position (Kathmandu)
        mapViewer.setDisplayPosition(new Coordinate(27.7172, 85.3240), 13);

        add(mapViewer, BorderLayout.CENTER);

        // Add click listener
        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Double click handling for setting destination
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    ICoordinate coord = mapViewer.getPosition(e.getPoint());
                    if (mapClickListener != null) {
                        mapClickListener.onDestinationSelected(coord.getLat(), coord.getLon());
                    }
                }
                // Single click
                else if (e.getButton() == MouseEvent.BUTTON1 && mapClickListener != null) {
                    ICoordinate coord = mapViewer.getPosition(e.getPoint());
                    mapClickListener.onMapClick(coord.getLat(), coord.getLon());
                }
            }
        });
    }

    // Map API
    public void setCenter(double lat, double lon, int zoom) {
        SwingUtilities.invokeLater(() -> {
            if (mapViewer != null) {
                mapViewer.setDisplayPosition(new Coordinate(lat, lon), zoom);
            }
        });
    }

    public void showEntities(String json) {
        // Ignored in favor of direct updateEntityPosition calls from controller
        // System.out.println("[MapPanel] showEntities called (JSON parsing skipped in
        // Swing impl)");
    }

    /**
     * Updates an entity position on the map with specific color coding.
     * 
     * @param id   Unique ID of the entity
     * @param lat  Latitude
     * @param lon  Longitude
     * @param type "PASSENGER" (Blue) or "DRIVER" (Red)
     */
    public void updateEntityPosition(String id, double lat, double lon, String type) {
        SwingUtilities.invokeLater(() -> {
            if (mapViewer != null) {
                MapMarkerDot marker = entityMarkers.get(id);
                Coordinate newCoord = new Coordinate(lat, lon);
                Color color = "PASSENGER".equals(type) ? Color.BLUE : Color.RED;

                if (marker != null) {
                    mapViewer.removeMapMarker(marker);
                }

                marker = new MapMarkerDot(color, newCoord.getLat(), newCoord.getLon());
                marker.setBackColor(color);
                entityMarkers.put(id, marker);
                mapViewer.addMapMarker(marker);
            }
        });
    }

    // Overload for backward compatibility (defaults to RED/Driver if unknown, but
    // useful for generic updates)
    public void updateEntityPosition(String id, double lat, double lon) {
        updateEntityPosition(id, lat, lon, "DRIVER");
    }

    public void setRoute(double slat, double slon, double elat, double elon) {
        SwingUtilities.invokeLater(() -> {
            if (mapViewer != null) {
                if (currentRoute != null) {
                    mapViewer.removeMapPolygon(currentRoute);
                }

                List<Coordinate> routePoints = new ArrayList<>();
                routePoints.add(new Coordinate(slat, slon));
                routePoints.add(new Coordinate(elat, elon)); // Direct line for now

                // Blue line, thick
                currentRoute = new MapPolygonImpl(routePoints);
                // Note: JMapViewer defaults for polygon styles might vary, customized logic
                // usually needed for lines
                // But MapPolygonImpl is often used for lines if points are < 3 or style set
                // correctly.
                // Creating a custom style for the route line:
                // Since MapPolygonImpl customization is limited without extending, we rely on
                // default or simple impl.
                // Ideally, we'd use a MapOverlay or similar, but polygon is standard for paths
                // in simple usage.

                mapViewer.addMapPolygon(currentRoute);

                // Hack: MapPolygonImpl default might be filled. We want a line.
                // JMapViewer 2.x often treats polygons as filled areas.
                // We might need a custom MapPolygon or just rely on it being a "thin" polygon
                // if JMapViewer supports paths.

                // NOTE: Standard JMapViewer MapPolygonImpl draws a polygon.
                // Functionally for a route (A to B), it might look like a filled shape if not
                // handled carefully.
                // However, with 2 points, it SHOULD render as a line in many Graphics
                // implementations.
                // If this fails to render a line, we might need a custom MapPolygon.
            }
        });
    }

    public void clearRoute() {
        SwingUtilities.invokeLater(() -> {
            if (mapViewer != null && currentRoute != null) {
                mapViewer.removeMapPolygon(currentRoute);
                currentRoute = null;
            }
        });
    }

    public void clearMap() {
        SwingUtilities.invokeLater(() -> {
            if (mapViewer != null) {
                mapViewer.removeAllMapMarkers();
                mapViewer.removeAllMapPolygons();
                entityMarkers.clear();
                currentRoute = null;
            }
        });
    }

    public void showRide(RideRequest request) {
        System.out.println("[MAP] Showing ride: " + request);
    }

    public void refresh() {
        SwingUtilities.invokeLater(() -> {
            if (mapViewer != null) {
                mapViewer.repaint();
            }
        });
    }

    // Listener interface
    public interface MapClickListener {
        void onMapClick(double lat, double lon);

        default void onDestinationSelected(double lat, double lon) {
        }
    }

    private MapClickListener mapClickListener;

    public void setMapClickListener(MapClickListener listener) {
        this.mapClickListener = listener;
    }
}
