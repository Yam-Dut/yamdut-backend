package org.yamdut.controller;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.yamdut.model.RideRequest;
import org.yamdut.service.RideMatchingService;
import org.yamdut.view.dashboard.DriverDashboard;

public class DriverDashboardController {

    private final DriverDashboard view;
    private final String driverName = "Driver A";

    public DriverDashboardController(DriverDashboard view) {
        this.view = view;
        initController();
    }

    private void initController() {

        view.getOnlineToggle().addActionListener(e -> {
            if (view.getOnlineToggle().isSelected()) {
                view.getOnlineToggle().setText("Online");
                loadRequests();
            } else {
                view.getOnlineToggle().setText("Go Online");
                view.getRequestModel().clear();
            }
        });

        view.getRequestList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                acceptRide();
            }
        });
    }

    private void loadRequests() {
        view.getRequestModel().clear();

        for (RideRequest request : RideMatchingService.getRequests()) {
            if (!request.isAccepted()) {
                view.getRequestModel().addElement(
                        request.getPickup() + " → " + request.getDestination()
                );
            }
        }
    }

    private void acceptRide() {
        int index = view.getRequestList().getSelectedIndex();
        if (index == -1) return;

        RideRequest request = RideMatchingService.getRequests().get(index);

        request.accept(driverName);
        simulateRoute(request);
    }

    private void simulateRoute(RideRequest request) {
        JPanel panel = view.getRoutePanel();
        panel.removeAll();

        JLabel label = new JLabel(
                "Driving to pickup at: " + request.getPickup()
        );

        panel.add(label);
        panel.revalidate();
        panel.repaint();
    }
}
