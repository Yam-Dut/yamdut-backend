package org.yamdut.dao;

import java.util.List;

import org.yamdut.model.Ride;

public interface RideDAO {
    boolean createRide(Ride ride);
    Ride getRideById(int id);
    boolean updateRide(Ride ride);
    boolean deleteRide(int id);
    List<Ride> getAllRides();
    List<Ride> getRidesByStatus(String status);
}
