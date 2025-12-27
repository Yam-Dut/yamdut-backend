package org.yamdut.view.dashboard;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import org.yamdut.model.Role;
import org.yamdut.utils.Theme;
import org.yamdut.view.map.MapPanel;

public class DriverDashboard extends BaseDashboard {

    // Controls
    private JToggleButton onlineToggle;
    private JButton acceptRideButton;

    // Requests list
    private DefaultListModel<String> requestListModel;
    private JList<String> requestList;

    // Map
    private MapPanel mapPanel;

    public DriverDashboard() {
        super();
        setWelcomeMessage("Welcome, Driver");
        initContent();
    }

    @Override
    protected void initContent() {

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Theme.BACKGROUND_PRIMARY);

        // Left control panel

        JPanel controlPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        controlPanel.setBackground(Theme.BACKGROUND_PRIMARY);

        onlineToggle = new JToggleButton("Go Online");
        acceptRideButton = new JButton("Accept Ride");
        acceptRideButton.setEnabled(false);

        controlPanel.add(onlineToggle);
        controlPanel.add(acceptRideButton);

        // Request list

        requestListModel = new DefaultListModel<>();
        requestList = new JList<>(requestListModel);

        JScrollPane requestScroll = new JScrollPane(requestList);
        requestScroll.setBorder(
            BorderFactory.createTitledBorder("Passenger Requests")
        );

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        leftPanel.add(controlPanel, BorderLayout.NORTH);
        leftPanel.add(requestScroll, BorderLayout.CENTER);

        // Map panel (core)

        mapPanel = new MapPanel(Role.DRIVER);

        // Layout

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(mapPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    // Getters (controller access)

    public JToggleButton getOnlineToggle() {
        return onlineToggle;
    }

    public JButton getAcceptRideButton() {
        return acceptRideButton;
    }

    public DefaultListModel<String> getRequestListModel() {
        return requestListModel;
    }

    public JList<String> getRequestList() {
        return requestList;
    }

    public MapPanel getMapPanel() {
        return mapPanel;
    }
}
