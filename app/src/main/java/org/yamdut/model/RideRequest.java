package org.yamdut.model;

public class RideRequest {

    private final String pickup;
    private final String destination;
    private boolean accepted;

    public RideRequest(String pickup, String destination) {
        this.pickup = pickup;
        this.destination = destination;
        this.accepted = false;
    }

    public void markAccepted() {
        this.accepted = true;
    }

    public boolean isAccepted() {
        return accepted;
    }

    @Override
    public String toString() {
        return pickup + " → " + destination;
    }
}
