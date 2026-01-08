package org.yamdut.dao;

import java.util.List;

import org.yamdut.model.Driver;

public interface DriverDAO {
    boolean createDriver(Driver driver);

    Driver getDriverById(long id);

    Driver getDriverByUserId(long userId);

    boolean updateDriver(Driver driver);

    boolean deleteDriver(long id);

    List<Driver> getAllDrivers();
}
