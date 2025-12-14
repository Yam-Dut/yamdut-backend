package org.example.dao;

import org.example.model.Driver;
import java.util.List;

public interface DriverDAO {
    boolean createDriver(Driver driver);
    Driver getDriverById(int id);
    boolean updateDriver(Driver driver);
    boolean deleteDriver(int id);
    List<Driver> getAllDrivers();
}
