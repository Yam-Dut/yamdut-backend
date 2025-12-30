package org.yamdut.view.dashboard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import org.yamdut.utils.Theme;

public class DriverDashboard extends BaseDashboard {

    private JToggleButton onlineToggle;
    private JList<String> requestList;
    private DefaultListModel<String> requestModel;

    private JXMapViewer mapPanel;
    private GeoPosition currentLocation;

    public DriverDashboard() {
        super();
        initContent();
        initMapDefaults();
    }

    @Override
    protected void initContent() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Theme.BACKGROUND_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        onlineToggle = new JToggleButton("Go Online");

        requestModel = new DefaultListModel<>();
        requestList = new JList<>(requestModel);

        JScrollPane scroll = new JScrollPane(requestList);
        scroll.setBorder(BorderFactory.createTitledBorder("Passenger Requests"));

        mapPanel = new JXMapViewer();
        mapPanel.setPreferredSize(new Dimension(100, 200));
        mapPanel.setBorder(BorderFactory.createTitledBorder("Route Simulation"));

        panel.add(onlineToggle, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(mapPanel, BorderLayout.SOUTH);

        add(panel, BorderLayout.CENTER);
    }

    private void initMapDefaults() {
        mapPanel.setTileFactory(new DefaultTileFactory(new OSMTileFactoryInfo()));

        // TEMP: Kathmandu
        currentLocation = new GeoPosition(27.7172, 85.3240);

        mapPanel.setAddressLocation(currentLocation);
        mapPanel.setZoom(7);
    }

    public void showEntities(GeoPosition driverPos, GeoPosition passengerPos) {
        Set<Waypoint> waypoints = new HashSet<>();

        waypoints.add(new DefaultWaypoint(driverPos));
        waypoints.add(new DefaultWaypoint(passengerPos));

        WaypointPainter<Waypoint> painter = new WaypointPainter<>();
        painter.setWaypoints(waypoints);

        mapPanel.setOverlayPainter(painter);
        mapPanel.repaint();
    }

    public JToggleButton getOnlineToggle() {
        return onlineToggle;
    }

    public DefaultListModel<String> getRequestModel() {
        return requestModel;
    }

    public JList<String> getRequestList() {
        return requestList;
    }

    public JXMapViewer getMapPanel() {
        return mapPanel;
    }

    public GeoPosition getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(GeoPosition position) {
        this.currentLocation = position;
        mapPanel.setAddressLocation(position);
    }

    // compatibility since controller still uses "routePanel"
    public JPanel getRoutePanel() {
        return mapPanel;
    }
}
