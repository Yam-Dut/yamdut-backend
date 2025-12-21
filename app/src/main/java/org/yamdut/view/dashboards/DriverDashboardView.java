package org.yamdut.view.dashboards;

import org.yamdut.model.Trip;
import org.yamdut.model.Trip.TripStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverDashboardView extends JPanel {
    private JButton logoutButton = new JButton("Logout");
    private JPanel requestsPanel = new JPanel();
    private JEditorPane mapPane = new JEditorPane();
    private Map<Trip, JPanel> requestCards = new HashMap<>();
    private ActionListener logoutListener;

    public DriverDashboardView(String driverName) {
        setLayout(new BorderLayout());
        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Driver: " + driverName), BorderLayout.WEST);
        top.add(logoutButton, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        requestsPanel.setLayout(new BoxLayout(requestsPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(requestsPanel), BorderLayout.CENTER);

        mapPane.setContentType("text/html");
        mapPane.setEditable(false);
        add(new JScrollPane(mapPane), BorderLayout.SOUTH);

        logoutButton.addActionListener(e -> {
            if (logoutListener != null) logoutListener.actionPerformed(e);
        });
    }

    public void addLogoutListener(ActionListener al) {
        this.logoutListener = al;
    }

    public void displayTripRequests(List<Trip> requests) {
        requestsPanel.removeAll();
        requestCards.clear();
        for (Trip t : requests) {
            JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT));
            card.add(new JLabel(t.getRiderName() + " — " + t.getPickupLocation() + " → " + t.getDropLocation()));
            requestsPanel.add(card);
            requestCards.put(t, card);
        }
        revalidate();
        repaint();
    }

    public void addAcceptListener(Trip trip, ActionListener al) {
        // no-op: UI buttons not implemented; store listener for potential future use
    }

    public void addRejectListener(Trip trip, ActionListener al) {
    }

    public void addIncreaseFareListener(Trip trip, ActionListener al) {
    }

    public void addDecreaseFareListener(Trip trip, ActionListener al) {
    }

    public void updateCountdown(Trip trip, int secondsLeft) {
        // no-op for now
    }

    public void updateFare(Trip trip, int newFare) {
        // no-op for now
    }

    public void removeRequestCard(Trip trip) {
        JPanel card = requestCards.remove(trip);
        if (card != null) {
            requestsPanel.remove(card);
            revalidate();
            repaint();
        }
    }

    public void showMessage(String message, String title, int msgType) {
        JOptionPane.showMessageDialog(this, message, title, msgType);
    }

    public void showTripView() {
        // switch UI to trip view - not implemented
    }

    public void setTripPhase(TripStatus status) {
        // no-op
    }

    public void addCallListener(ActionListener al) {}
    public void addArrivedListener(ActionListener al) {}
    public void addCompleteListener(ActionListener al) {}
    public void addCancelListener(ActionListener al) {}

    public void enableCompleteButton(boolean enabled) {}

    public void showTripCompletionDialog(String riderName, String pickup, String drop, int elapsedSeconds, double totalDistance, int fare) {
        String msg = String.format("Rider: %s\nFrom: %s\nTo: %s\nDuration: %s\nDistance: %.2f km\nFare: %d",
                riderName, pickup, drop, formatDuration(elapsedSeconds), totalDistance, fare);
        JOptionPane.showMessageDialog(this, msg, "Trip Completed", JOptionPane.INFORMATION_MESSAGE);
    }

    private String formatDuration(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    public void showRequestsView() {
        // no-op
    }

    public void updateMapView(String html) {
        mapPane.setText(html);
    }
}
