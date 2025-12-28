package org.yamdut.controller;

import javax.swing.JOptionPane;
import org.yamdut.model.RideRequest;
import org.yamdut.service.RideMatchingService;
import org.yamdut.view.dashboard.DriverDashboard;

public class DriverDashboardController {

    private final DriverDashboard view;
    private final RideMatchingService matchingService;

    public DriverDashboardController(DriverDashboard view) {
        this.view = view;
        this.matchingService = RideMatchingService.getInstance();
        bindEvents();
    }
    private void bindEvents() {
        view.getOnlineToggle().addActionListener(e -> toggleOnline());
        view.getAcceptRideButton().addActionListener(e -> acceptRide());
    }
    private void toggleOnline() {
        boolean online = view.getOnlineToggle().isSelected();

        view.getOnlineToggle().setText(
            online ? "Go Offline" : "Go Online"
        );

        if (online) {
            matchingService.registerDriver(this);
            refreshRequests();
        } else {
            matchingService.unregisterDriver(this);
            view.getRequestListModel().clear();
        }
    }

    private void refreshRequests() {
        view.getRequestListModel().clear();

        for (RideRequest request : matchingService.getPendingRequests()) {
            view.getRequestListModel().addElement(request.toString());
        }

        view.getAcceptRideButton().setEnabled(
            !view.getRequestListModel().isEmpty()
        );
    }
    private void acceptRide() {
        int index = view.getRequestList().getSelectedIndex();

        if (index == -1) {
            JOptionPane.showMessageDialog(
                view,
                "Select a request first"
            );
            return;
        }

        RideRequest request =
            matchingService.getPendingRequests().get(index);

        matchingService.assignRide(request);

        view.getMapPanel().showRide(request);
        refreshRequests();
    }
}
