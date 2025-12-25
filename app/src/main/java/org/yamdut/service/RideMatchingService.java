package org.yamdut.service;

import java.util.ArrayList;
import java.util.List;

import org.yamdut.model.RideRequest;

public class RideMatchingService {
    private static final List<RideRequest> requests = new ArrayList<>();

    public static void submit(RideRequest request) {
        requests.add(request);
    }

    public static List<RideRequest> getRequests() {
        return requests;
    }
}
//this is main system--> later add db
