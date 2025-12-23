package org.yamdut.dao;

import java.util.List;

import org.yamdut.model.Trip;

public interface TripDAO {
    boolean createTrip(Trip trip);
    List<Trip> getAllTrips();
    List<Trip> getTripsByDriver(int driverId);
    List<Trip> getPendingTripsByDriver(int driverId);
    List<Trip> getTripsSince(long requestTimeMillis);
}
