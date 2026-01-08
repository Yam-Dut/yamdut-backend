package org.yamdut.view.map;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.DefaultMapController;
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
    private final Map<String, MapPolygon> activeRoutes = new HashMap<>(); // Name -> Route
    private Image bikeIcon;

    public MapPanel(Role role) {
        this.role = role;
        setLayout(new BorderLayout());

        // CRITICAL: Set User-Agent for OSM
        System.setProperty("http.agent", "YamDut/1.0");

        mapViewer = new JMapViewer();

        mapViewer.setZoomControlsVisible(true);
        mapViewer.setScrollWrapEnabled(true);
        mapViewer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Custom Map Controller to ensure Proper Drag works
        MouseAdapter mapController = new MouseAdapter() {
            private Point lastDragPoint;
            private boolean isDragging = false;

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    lastDragPoint = e.getPoint();
                    isDragging = false;
                    mapViewer.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    mapViewer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && lastDragPoint != null) {
                    isDragging = true;
                    int dx = e.getX() - lastDragPoint.x;
                    int dy = e.getY() - lastDragPoint.y;

                    if (mapViewer != null) {
                        mapViewer.moveMap(dx, dy);
                    }

                    lastDragPoint = e.getPoint();
                }
            }

            @Override
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
                if (mapViewer != null) {
                    if (e.getWheelRotation() < 0) {
                        mapViewer.zoomIn(e.getPoint());
                    } else {
                        mapViewer.zoomOut(e.getPoint());
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (isDragging)
                    return;

                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    ICoordinate coord = mapViewer.getPosition(e.getPoint());
                    if (mapClickListener != null) {
                        mapClickListener.onDestinationSelected(coord.getLat(), coord.getLon());
                    }
                } else if (SwingUtilities.isLeftMouseButton(e) && mapClickListener != null) {
                    ICoordinate coord = mapViewer.getPosition(e.getPoint());
                    mapClickListener.onMapClick(coord.getLat(), coord.getLon());
                }
            }
        };

        mapViewer.addMouseListener(mapController);
        mapViewer.addMouseMotionListener(mapController);
        mapViewer.addMouseWheelListener(mapController);

        // Default position (Kathmandu)
        mapViewer.setDisplayPosition(new Coordinate(27.7172, 85.3240), 13);

        add(mapViewer, BorderLayout.CENTER);
        loadImages();
    }

    private void loadImages() {
        try {
            File bikeFile = new File("app/src/main/resources/images/bike_icon.png");
            if (bikeFile.exists()) {
                bikeIcon = ImageIO.read(bikeFile).getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            } else {
                // Try classpath
                URL url = getClass().getResource("/images/bike_icon.png");
                if (url != null) {
                    bikeIcon = ImageIO.read(url).getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                }
            }
        } catch (Exception e) {
            System.err.println("[MapPanel] Could not load bike icon: " + e.getMessage());
        }
    }

    // Pronounced Marker Class
    public static class PronouncedMarker extends MapMarkerDot {
        private final int radius = 12;
        private final Color ringColor;

        public PronouncedMarker(Color color, double lat, double lon) {
            super(color, lat, lon);
            this.ringColor = color.darker();
        }

        @Override
        public void paint(Graphics g, Point position, int radius) {
            int r = this.radius;
            int x = position.x - r;
            int y = position.y - r;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Shadow
            g2.setColor(new Color(0, 0, 0, 50));
            g2.fillOval(x + 2, y + 2, r * 2, r * 2);

            // Outer Ring
            g2.setColor(Color.WHITE);
            g2.fillOval(x, y, r * 2, r * 2);

            // Inner Core
            g2.setColor(getBackColor());
            g2.fillOval(x + 3, y + 3, (r - 3) * 2, (r - 3) * 2);

            // Border
            g2.setStroke(new BasicStroke(2));
            g2.setColor(ringColor);
            g2.drawOval(x, y, r * 2, r * 2);
        }
    }

    public class ImageMarker extends MapMarkerDot {
        private final Image image;

        public ImageMarker(Image image, double lat, double lon) {
            super(Color.RED, lat, lon);
            this.image = image;
        }

        @Override
        public void paint(Graphics g, Point position, int radius) {
            if (image != null) {
                int w = image.getWidth(null);
                int h = image.getHeight(null);
                g.drawImage(image, position.x - w / 2, position.y - h / 2, null);
            } else {
                super.paint(g, position, radius);
            }
        }
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
                double mlat = lat;
                double mlon = lon;

                if (marker != null) {
                    mapViewer.removeMapMarker(marker);
                }

                if ("pickup".equals(id) || "ME".equals(id)) {
                    // Blue for pickup
                    marker = new PronouncedMarker(Color.BLUE, mlat, mlon);
                    marker.setBackColor(Color.BLUE);
                } else if ("destination".equals(id) || "DEST".equals(id)) {
                    // Red for destination
                    marker = new PronouncedMarker(Color.RED, mlat, mlon);
                    marker.setBackColor(Color.RED);
                } else if ("PASSENGER_ONBOARD".equals(type)) {
                    // Yellow for onboard passenger
                    marker = new PronouncedMarker(Color.YELLOW, mlat, mlon);
                    marker.setBackColor(Color.YELLOW);
                } else if ("DRIVER".equals(type) && bikeIcon != null) {
                    marker = new ImageMarker(bikeIcon, mlat, mlon);
                } else {
                    Color color = "PASSENGER".equals(type) ? Color.BLUE : Color.RED;
                    marker = new MapMarkerDot(color, mlat, mlon);
                    marker.setBackColor(color);
                }

                if ("NONE".equals(type)) {
                    entityMarkers.remove(id);
                    return;
                }

                entityMarkers.put(id, marker);
                mapViewer.addMapMarker(marker);
            }
        });
    }

    public void clearRoutes() {
        SwingUtilities.invokeLater(() -> {
            if (mapViewer != null) {
                for (MapPolygon poly : activeRoutes.values()) {
                    mapViewer.removeMapPolygon(poly);
                }
                activeRoutes.clear();
            }
        });
    }

    public void removeEntityMarker(String id) {
        SwingUtilities.invokeLater(() -> {
            if (mapViewer != null) {
                org.openstreetmap.gui.jmapviewer.MapMarkerDot marker = entityMarkers.remove(id);
                if (marker != null) {
                    mapViewer.removeMapMarker(marker);
                }
            }
        });
    }

    // Overload for backward compatibility (defaults to RED/Driver if unknown, but
    // useful for generic updates)
    public void updateEntityPosition(String id, double lat, double lon) {
        updateEntityPosition(id, lat, lon, "DRIVER");
    }

    public void setRoute(double slat, double slon, double elat, double elon, Color color) {
        setRoute("DEFAULT", slat, slon, elat, elon, color);
    }

    public void setRoute(String name, double slat, double slon, double elat, double elon, Color color) {
        List<Coordinate> points = new ArrayList<>();
        points.add(new Coordinate(slat, slon));
        points.add(new Coordinate(elat, elon));
        setRoutePoints(name, points, color);
    }

    // MapPolyline Class to draw open paths without filling
    public static class MapPolyline extends MapPolygonImpl {
        public MapPolyline(List<Coordinate> points) {
            super(points);
        }

        @Override
        public void paint(Graphics g, List<Point> points) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getColor());
            g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            for (int i = 0; i < points.size() - 1; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
            g2.dispose();
        }
    }

    public void setRoutePoints(List<Coordinate> points, Color color) {
        setRoutePoints("DEFAULT", points, color);
    }

    public void setRoutePoints(String name, List<Coordinate> points, Color color) {
        SwingUtilities.invokeLater(() -> {
            if (mapViewer != null) {
                MapPolygon oldRoute = activeRoutes.get(name);
                if (oldRoute != null) {
                    mapViewer.removeMapPolygon(oldRoute);
                }

                if (points == null || points.isEmpty()) {
                    activeRoutes.remove(name);
                    return;
                }

                // Create polyline instead of polygon to avoid "blobs"
                MapPolygon newRoute = new MapPolyline(points);
                ((MapPolyline) newRoute).setColor(color);
                ((MapPolyline) newRoute).setBackColor(new Color(0, 0, 0, 0)); // Transparent

                activeRoutes.put(name, newRoute);
                mapViewer.addMapPolygon(newRoute);
            }
        });
    }

    public void setRoute(double slat, double slon, double elat, double elon) {
        setRoute(slat, slon, elat, elon, Color.BLUE);
    }

    public void clearRoute() {
        clearRoute("DEFAULT");
    }

    public void clearRoute(String name) {
        SwingUtilities.invokeLater(() -> {
            if (mapViewer != null) {
                MapPolygon route = activeRoutes.remove(name);
                if (route != null) {
                    mapViewer.removeMapPolygon(route);
                }
            }
        });
    }

    public void clearAllRoutes() {
        SwingUtilities.invokeLater(() -> {
            if (mapViewer != null) {
                for (MapPolygon route : activeRoutes.values()) {
                    mapViewer.removeMapPolygon(route);
                }
                activeRoutes.clear();
            }
        });
    }

    public void clearMap() {
        SwingUtilities.invokeLater(() -> {
            if (mapViewer != null) {
                mapViewer.removeAllMapMarkers();
                mapViewer.removeAllMapPolygons();
                entityMarkers.clear();
                activeRoutes.clear();
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
