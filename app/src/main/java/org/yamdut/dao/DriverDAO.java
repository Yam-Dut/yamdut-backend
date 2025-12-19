package org.yamdut.dao;

import java.util.List;

import org.yamdut.model.Driver;

public interface DriverDAO {
    boolean createDriver(Driver driver);
    Driver getDriverById(int id);
    boolean updateDriver(Driver driver);
    boolean deleteDriver(int id);
    List<Driver> getAllDrivers();
}
