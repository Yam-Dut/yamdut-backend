package org.yamdut.view.map;

import org.yamdut.model.MapEntity;
import org.yamdut.utils.*;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.WaypointPainter;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapPanel extends JPanel {

    private final JXMapViewer mapViewer;
    
    private final Set<Waypoint> waypoints = new HashSet<>();
    private final WaypointPainter<Waypoint> waypointPainter = 
        new org.jxmapviewer.viewer.WaypointPainter<>();


    private List<GeoPosition> route;
    private int routeIndex = 0;
    private Timer simulationTimer;

    public MapPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_PRIMARY);

        //map viewer
        mapViewer = new JXMapViewer();
        mapViewer.setTileFactory(
                new DefaultTileFactory(new OSMTileFactoryInfo())
        );

        mapViewer.setZoom(15);
        mapViewer.setAddressLocation(
                new GeoPosition(27.7172, 85.3240) // Kathmandu
        );

        waypointPainter.setWaypoints(waypoints);

        add(createHeader(), BorderLayout.NORTH);
        add(mapViewer, BorderLayout.CENTER);
    }

    //headers
    private JComponent createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BACKGROUND_CARD);
        header.setBorder(BorderFactory.createEmptyBorder(
                Theme.PADDING_MEDIUM,
                Theme.PADDING_LARGE,
                Theme.PADDING_MEDIUM,
                Theme.PADDING_LARGE
        ));

        JLabel title = new JLabel("Live Ride Map");
        title.setFont(Theme.getHeadingFont());
        title.setForeground(Theme.TEXT_PRIMARY);

        header.add(title, BorderLayout.WEST);
        return header;
    }

    public void showEntities(Collection<MapEntity> entities) {
        waypoints.clear();
        for (MapEntity e : entities) {
            waypoints.add(new DefaultWaypoint(e.getPosition()));
        }
    }

    public void drawRoute(List<GeoPosition> route) {
        this.route = route;
        this.routeIndex = 0;

        Painter<JXMapViewer> routePainter =
                new RoutePainter(route);

        List<Painter<JXMapViewer>> painters = Arrays.asList(routePainter, waypointPainter);
        Painter<JXMapViewer> compound = new CompoundPainter<>(painters);

        mapViewer.setOverlayPainter(compound);
    }

    public void startSimulation() {
        if (route == null || route.isEmpty()) return;

        simulationTimer = new Timer(1000, e -> {
            if (routeIndex >= route.size()) {
                simulationTimer.stop();
                return;
            }
            GeoPosition pos = route.get(routeIndex++);
            waypoints.clear();
            waypoints.add(new DefaultWaypoint(pos));
            mapViewer.setAddressLocation(pos);
            mapViewer.repaint();
        });

        simulationTimer.start();
    }
}