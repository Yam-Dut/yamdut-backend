package org.yamdut.controller;

import org.yamdut.view.dashboard.PassengerDashboard;
import org.yamdut.view.map.MapPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Controller for passenger dashboard actions.
 * Wires UI buttons to simple behaviors (map open, ride history/profile stubs).
 */
public class PassengerDashboardController {

    private final PassengerDashboard view;

    public PassengerDashboardController(PassengerDashboard view) {
        this.view = view;
        // Controllers can be extended here to wire additional behaviours.
    }
}


