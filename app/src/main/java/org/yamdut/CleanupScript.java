package org.yamdut;

import org.yamdut.service.RideMatchingService;

public class CleanupScript {
    public static void main(String[] args) {
        System.out.println("Cleaning up stale requests...");
        RideMatchingService.getInstance().clearAllPendingRequests();
        System.out.println("Done.");
        System.exit(0);
    }
}
