package org.yamdut.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.yamdut.model.RideRequest;

public class RideMatchingService {
    private static final RideMatchingService INSTANCE = new RideMatchingService();

    public static RideMatchingService getInstance() {
        return INSTANCE;
    }

    // Use thread-safe list
    private final List<RideRequest> pendingRequests = new CopyOnWriteArrayList<>();
    private final List<Object> onlineDrivers = new ArrayList<>();

    private RideMatchingService() {
        System.out.println("[RideMatching] Service instance created: " + this);
    }

    public void submitRide(RideRequest request) {
        // Check if this passenger already has a pending request and remove it
        pendingRequests.removeIf(existing -> 
            existing.getPassengerId() != null && 
            existing.getPassengerId().equals(request.getPassengerId()) &&
            !existing.isAccepted()
        );
        
        pendingRequests.add(request);
        System.out.println("[RideMatching] New ride request added: " + request);
        System.out.println("[RideMatching] Passenger: " + request.getPassengerName() + " (ID: " + request.getPassengerId() + ")");
        System.out.println("[RideMatching] Total requests in list: " + pendingRequests.size());
        System.out.println("[RideMatching] Non-accepted requests: " + getPendingRequests().size());
        
        // Debug: print all pending requests
        System.out.println("[RideMatching] All pending requests:");
        for (RideRequest req : pendingRequests) {
            System.out.println("  - " + req + " (accepted: " + req.isAccepted() + ")");
        }
    }

    public List<RideRequest> getPendingRequests() {
        // Return only non-accepted requests
        List<RideRequest> pending = new ArrayList<>();
        System.out.println("[RideMatching] getPendingRequests() called on instance: " + this);
        System.out.println("[RideMatching] Total requests in list: " + pendingRequests.size());
        
        if (pendingRequests.isEmpty()) {
            System.out.println("[RideMatching] WARNING: pendingRequests list is empty!");
        }
        
        for (RideRequest request : pendingRequests) {
            boolean isAccepted = request.isAccepted();
            String passengerName = request.getPassengerName();
            System.out.println("[RideMatching] Checking request from: " + passengerName + 
                             " (ID: " + request.getPassengerId() + ")" +
                             " - accepted: " + isAccepted);
            if (!isAccepted) {
                pending.add(request);
                System.out.println("[RideMatching] ✓ Added to pending: " + request);
            } else {
                System.out.println("[RideMatching] ✗ Skipped (already accepted): " + request);
            }
        }
        
        System.out.println("[RideMatching] getPendingRequests() returning " + pending.size() + " requests");
        return pending;
    }

    /**
     * Very naive matching for now
     * returns all online drivers
     * Later: distance, rating, ETA
     * Note: Does NOT submit ride - that should be done separately
     *  **/
    public List<Object> findAvailableDrivers(RideRequest request) {
        // Don't submit here - ride should already be submitted
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
        request.markAccepted();
        System.out.println("[RideMatching] Ride assigned: " + request);
        // Don't remove - keep for tracking, but mark as accepted
    }
    
    public void cancelRide(RideRequest request) {
        pendingRequests.remove(request);
        System.out.println("[RideMatching] Ride cancelled: " + request);
        System.out.println("[RideMatching] Remaining pending requests: " + pendingRequests.size());
    }
    
    // Debug method
    public int getAllRequestsCount() {
        return pendingRequests.size();
    }
    
    public int getAcceptedRequestsCount() {
        int count = 0;
        for (RideRequest req : pendingRequests) {
            if (req.isAccepted()) count++;
        }
        return count;
    }
}
//this is main system--> later add db
