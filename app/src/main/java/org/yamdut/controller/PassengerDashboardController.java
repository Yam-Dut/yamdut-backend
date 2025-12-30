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
        initController();
    }

    private void initController() {
        view.getBookRideButton().addActionListener(e -> submitRideRequest());
        view.getLogoutButton().addActionListener(e -> logout());
    }

    private void submitRideRequest() {
        String pickup = view.getPickupField().getText();
        String destination = view.getDestinationField().getText();

        if (pickup.isEmpty() || destination.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Enter pickup and destination");
            return;
        }

        currentRequest = new RideRequest(pickup, destination);
        RideMatchingService.submit(currentRequest);

        showWaitingUI();
        waitForDriverAcceptance();
    }

    private void showWaitingUI() {
        JPanel routePanel = view.getRoutePanel();
        routePanel.removeAll();
        routePanel.add(new JLabel("Waiting for a driver to accept..."));
        routePanel.revalidate();
        routePanel.repaint();
    }

    private void waitForDriverAcceptance() {
        Timer timer = new Timer(1000, e -> {
            if (currentRequest.isAccepted()) {
                ((Timer) e.getSource()).stop();
                simulateRoute();
            }
        });
        timer.start();
    }

    private void simulateRoute() {
        JPanel routePanel = view.getRoutePanel();
        routePanel.removeAll();

        routePanel.add(new JLabel(
                "Driver " + currentRequest.getAcceptedDriver()
                        + " is on the way to " + currentRequest.getPickup()
        ));

        routePanel.revalidate();
        routePanel.repaint();
    }

    private void logout() {
        System.out.println("Passenger logged out");
    }
}
