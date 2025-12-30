package org.yamdut.view.dashboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jxmapviewer.viewer.GeoPosition;   // <-- ADD THIS
import org.yamdut.utils.Theme;

public class PassengerDashboard extends BaseDashboard {
    private JTextField pickupField;
    private JTextField destinationField;
    private JButton bookRideButton;
    private JList<String> driverList;
    private DefaultListModel<String> driverListModel;
    private JPanel routePanel;

    public PassengerDashboard() {
        super();
        initContent(); // REQUIRED
    }

    @Override
    protected void initContent() {
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Theme.BACKGROUND_PRIMARY);

        // ── Top: Location inputs ───────────────────────────
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBackground(Theme.BACKGROUND_PRIMARY);

        pickupField = new JTextField();
        destinationField = new JTextField();

        bookRideButton = new JButton("Book Ride");

        inputPanel.add(new JLabel("Your Location"));
        inputPanel.add(pickupField);

        inputPanel.add(new JLabel("Destination"));
        inputPanel.add(destinationField);

        inputPanel.add(new JLabel());
        inputPanel.add(bookRideButton);

        // ── Center: Driver list ───────────────────────────
        driverListModel = new DefaultListModel<>();
        driverList = new JList<>(driverListModel);
        JScrollPane driverScroll = new JScrollPane(driverList);
        driverScroll.setBorder(BorderFactory.createTitledBorder("Available Drivers"));

        // ── Bottom: Route simulation panel ─────────────────
        routePanel = new JPanel();
        routePanel.setPreferredSize(new Dimension(100, 200));
        routePanel.setBackground(Color.WHITE);
        routePanel.setBorder(BorderFactory.createTitledBorder("Route Simulation"));

        contentPanel.add(inputPanel, BorderLayout.NORTH);
        contentPanel.add(driverScroll, BorderLayout.CENTER);
        contentPanel.add(routePanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
    }

    // ── Getters for controller ─────────────────────────────
    public JTextField getPickupField() {
        return pickupField;
    }

    public JTextField getDestinationField() {
        return destinationField;
    }

    public JButton getBookRideButton() {
        return bookRideButton;
    }

    public JList<String> getDriverList() {
        return driverList;
    }

    public DefaultListModel<String> getDriverListModel() {
        return driverListModel;
    }

    public JPanel getRoutePanel() {
        return routePanel;
    }

    // ── TEMP COORDINATE PROVIDERS (will replace with geocoding later) ──
    public GeoPosition getPickupLocation() {
        return new GeoPosition(27.7172, 85.3240);
    }

    public GeoPosition getDropoffLocation() {
        return new GeoPosition(27.6730, 85.3250);
    }
}
