package org.yamdut.controller;

import org.yamdut.view.dashboard.DriverDashboard;

import javax.swing.*;

/**
 * Controller for driver dashboard actions.
 * Handles online toggle and basic stubs for requests/earnings.
 */
public class DriverDashboardController {

    private final DriverDashboard view;

    public DriverDashboardController(DriverDashboard view) {
        this.view = view;
        initListeners();
    }

    private void initListeners() {
        view.getGoOnlineButton().addActionListener(e -> view.toggleOnlineStatus());

        view.getViewRequestsButton().addActionListener(e ->
                JOptionPane.showMessageDialog(view,
                        "Ride requests view is not implemented yet.",
                        "Info", JOptionPane.INFORMATION_MESSAGE));

        view.getEarningsButton().addActionListener(e ->
                JOptionPane.showMessageDialog(view,
                        "Earnings view is not implemented yet.",
                        "Info", JOptionPane.INFORMATION_MESSAGE));
    }
}


