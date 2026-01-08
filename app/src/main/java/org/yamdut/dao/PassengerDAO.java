package org.yamdut.dao;

import java.util.List;
import org.yamdut.model.Passenger;

public interface PassengerDAO {
    boolean createPassenger(Passenger passenger);

    Passenger getPassengerById(long id);

    Passenger getPassengerByUserId(long userId);

    boolean updatePassenger(Passenger passenger);

    boolean deletePassenger(long id);

    List<Passenger> getAllPassengers();
}
