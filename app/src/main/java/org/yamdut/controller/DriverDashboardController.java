package org.yamdut.controller;

import org.yamdut.view.dashboards.DriverDashboardView;
import org.yamdut.model.User;
import org.yamdut.model.Trip;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DriverDashboardController {
    private DriverDashboardView view;
    private User currentDriver;
    private Trip currentTrip;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public DriverDashboardController(DriverDashboardView view, User currentDriver) {
        this.view = view;
        this.currentDriver = currentDriver;
        this.currentTrip = null;

        view.addArrivedListener(e -> handleArrived());
        view.addStartTripListener(e -> handleStartTrip());
        view.addEndTripListener(e -> handleEndTrip());
        view.addLogoutListener(e -> handleLogout());

        initializeDummyTrip();
    }

    private void initializeDummyTrip() {
        currentTrip = new Trip(
            currentDriver.getId(),
            1,
            "John Doe",
            "9841234567",
            27.7172,
            85.3240,
            27.7300,
            85.3400
        );
        currentTrip.setStatus("PENDING");

        view.setRiderInfo(currentTrip.getRiderName(), currentTrip.getRiderPhone());
        view.setTripStatus("PENDING");
    }

    private void handleArrived() {
        if (currentTrip == null) {
            view.showError("No active trip");
            return;
        }

        Timestamp arrivedTime = Timestamp.valueOf(LocalDateTime.now());
        currentTrip.setArrivedAt(arrivedTime);
        currentTrip.setStatus("ARRIVED");

        view.setTripStatus("ARRIVED");
        view.updateTimestamps(
            formatTimestamp(arrivedTime),
            null,
            null
        );
        view.showSuccess("Arrived at pickup location!");
    }

    private void handleStartTrip() {
        if (currentTrip == null) {
            view.showError("No active trip");
            return;
        }

        if (!currentTrip.getStatus().equals("ARRIVED")) {
            view.showError("Trip must be in ARRIVED status to start");
            return;
        }

        Timestamp startTime = Timestamp.valueOf(LocalDateTime.now());
        currentTrip.setStartedAt(startTime);
        currentTrip.setStatus("IN_PROGRESS");

        view.setTripStatus("IN_PROGRESS");
        view.updateTimestamps(
            formatTimestamp(currentTrip.getArrivedAt()),
            formatTimestamp(startTime),
            null
        );
        view.showSuccess("Trip started! Heading to destination...");
    }

    private void handleEndTrip() {
        if (currentTrip == null) {
            view.showError("No active trip");
            return;
        }

        if (!currentTrip.getStatus().equals("IN_PROGRESS")) {
            view.showError("Trip must be IN_PROGRESS to end");
            return;
        }

        Timestamp endTime = Timestamp.valueOf(LocalDateTime.now());
        currentTrip.setEndedAt(endTime);
        currentTrip.setStatus("COMPLETED");

        view.setTripStatus("COMPLETED");
        view.updateTimestamps(
            formatTimestamp(currentTrip.getArrivedAt()),
            formatTimestamp(currentTrip.getStartedAt()),
            formatTimestamp(endTime)
        );

        showTripSummary();
    }

    private void showTripSummary() {
        long arrivedToStartMs = currentTrip.getStartedAt().getTime() - currentTrip.getArrivedAt().getTime();
        long startToEndMs = currentTrip.getEndedAt().getTime() - currentTrip.getStartedAt().getTime();
        long totalMs = currentTrip.getEndedAt().getTime() - currentTrip.getArrivedAt().getTime();

        long arrivedToStartMins = arrivedToStartMs / 60000;
        long startToEndMins = startToEndMs / 60000;
        long totalMins = totalMs / 60000;

        StringBuilder summary = new StringBuilder();
        summary.append("TRIP SUMMARY\n");
        summary.append("================\n\n");
        summary.append("Rider: ").append(currentTrip.getRiderName()).append("\n");
        summary.append("Phone: ").append(currentTrip.getRiderPhone()).append("\n\n");
        summary.append("Pickup: (").append(String.format("%.4f", currentTrip.getPickupLat())).append(", ")
            .append(String.format("%.4f", currentTrip.getPickupLng())).append(")\n");
        summary.append("Dropoff: (").append(String.format("%.4f", currentTrip.getDropoffLat())).append(", ")
            .append(String.format("%.4f", currentTrip.getDropoffLng())).append(")\n\n");
        summary.append("Arrived: ").append(formatTimestamp(currentTrip.getArrivedAt())).append("\n");
        summary.append("Started: ").append(formatTimestamp(currentTrip.getStartedAt())).append("\n");
        summary.append("Ended: ").append(formatTimestamp(currentTrip.getEndedAt())).append("\n\n");
        summary.append("Wait Time: ").append(arrivedToStartMins).append(" minutes\n");
        summary.append("Trip Duration: ").append(startToEndMins).append(" minutes\n");
        summary.append("Total Time: ").append(totalMins).append(" minutes\n\n");
        summary.append("Distance: Pending calculation\n");
        summary.append("Fare: Pending calculation\n");

        view.showTripSummary(summary.toString());

        resetForNextTrip();
    }

    private void resetForNextTrip() {
        currentTrip = null;
        view.setTripStatus("PENDING");
        view.setRiderInfo("Waiting for next trip...", "-");
        view.updateTimestamps(null, null, null);
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "-";
        return timestamp.toLocalDateTime().format(timeFormatter);
    }

    private void handleLogout() {
        view.dispose();
    }
}
