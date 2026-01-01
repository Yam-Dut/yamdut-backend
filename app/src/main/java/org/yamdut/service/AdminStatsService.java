package org.yamdut.service;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.yamdut.model.Driver;
import org.yamdut.model.Trip;

public class AdminStatsService {

    private final Random random = new Random();

    public int getTotalUsers() {
        // Mock random value
        return 100 + random.nextInt(500);
    }

    public int getActiveDrivers() {
        // Mock random value
        return 20 + random.nextInt(50);
    }

    public int getTodaysTrips() {
        // Mock random value
        return 50 + random.nextInt(150);
    }

    public int getPendingTrips() {
        // Mock random value
        return random.nextInt(20);
    }

    public double getTodaysRevenue() {
        // Mock random value
        return 1500 + random.nextDouble() * 5000;
    }

    public List<Trip> getRecentTrips(int limit) {
        return Collections.emptyList();
    }

    public List<Driver> getTopDrivers(int limit) {
        return Collections.emptyList();
    }
}
