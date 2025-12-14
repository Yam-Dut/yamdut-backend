package org.example.model;

import java.sql.Timestamp;

public class Ride {
    private int id;
    private int driverId;
    private int passengerId;
    private String fromLocation;
    private String toLocation;
    private String status;
    private int duration;
    private Timestamp startTime;

    public Ride() {}

    public Ride(int driverId, int passengerId, String fromLocation, String toLocation, String status, int duration) {
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.status = status;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }
}
