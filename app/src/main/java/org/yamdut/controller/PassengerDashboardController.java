package org.yamdut.controller;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.yamdut.model.RideRequest;
import org.yamdut.service.RideMatchingService;
import org.yamdut.view.dashboard.PassengerDashboard;

public class PassengerDashboardController {

    private final PassengerDashboard view;
    private RideRequest currentRequest;

    public PassengerDashboardController(PassengerDashboard view) {
        this.view = view;
    }

    private void logout() {
        System.out.println("Passenger logged out");
    }
}
