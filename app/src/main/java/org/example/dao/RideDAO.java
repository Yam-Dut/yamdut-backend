package org.example.dao;

import org.example.model.Ride;
import java.util.List;

public interface RideDAO {
    boolean createRide(Ride ride);
    Ride getRideById(int id);
    boolean updateRide(Ride ride);
    boolean deleteRide(int id);
    List<Ride> getAllRides();
    List<Ride> getRidesByStatus(String status);
}
