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
    }
    }
