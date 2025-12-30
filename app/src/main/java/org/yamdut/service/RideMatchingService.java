package org.yamdut.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.yamdut.model.RideRequest;

public class RideMatchingService {
    private static final RideMatchingService INSTANCE = new RideMatchingService();

    public static RideMatchingService getInstance() {
        return INSTANCE;
    }


    private final List<RideRequest> pendingRequests = new ArrayList<>();
    private final List<Object> onlineDrivers = new ArrayList<>();

    private RideMatchingService() {}

    public void submitRide(RideRequest request) {
        pendingRequests.add(request);
    }

    public List<RideRequest> getPendingRequests() {
        return Collections.unmodifiableList(pendingRequests);
    }

    /**
     * Very naive matching for now
     * returns all online drivers
     * Later: distance, rating, ETA
     *  **/
    public List<Object> findAvailableDrivers(RideRequest request) {
        submitRide(request);
        return new ArrayList<>(onlineDrivers);
    }

    public void registerDriver(Object driver) {
        if (!onlineDrivers.contains(driver)) {
            onlineDrivers.add(driver);
        }
    }

    public void unregisterDriver(Object driver) {
        onlineDrivers.remove(driver);
    }
    public void assignRide(RideRequest request) {
        pendingRequests.remove(request);
        request.markAccepted(); 
    }
}
//this is main system--> later add db
