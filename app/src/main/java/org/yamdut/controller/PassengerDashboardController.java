package org.yamdut.controller;

import javax.swing.JOptionPane;

import org.yamdut.model.RideRequest;
import org.yamdut.service.RideMatchingService;
import org.yamdut.view.dashboard.PassengerDashboard;

public class PassengerDashboardController {

    private final PassengerDashboard view;
    private final RideMatchingService matchingService;

    private RideRequest currenRequest;

    public PassengerDashboardController(PassengerDashboard view) {
        this.view = view;
        this.matchingService = RideMatchingService.getInstance();
        bindEvents();
    }

    private void bindEvents() {
        view.getBookRideButton().addActionListener(e -> bookRide());
    }

    private void bookRide() {
        String pickup = view.getPickupField().getText().trim();
        String destination = view.getDestinationField().getText().trim();

        if (pickup.isEmpty() || destination.isEmpty()) {
            JOptionPane.showMessageDialog(
                view,
                "Pickup and destination are required"
            );
            return;
        }

        currenRequest = new RideRequest(pickup, destination);

        var drivers = matchingService.findAvailableDrivers(currenRequest);

        view.getDriverListModel().clear();
        drivers.forEach(driver ->
            view.getDriverListModel().addElement(driver.toString())
        );
        view.getMapPanel().showPickupAndDestination(pickup, destination);
    }

    private void logout() {
        System.out.println("Passenger logged out");
    }
}
