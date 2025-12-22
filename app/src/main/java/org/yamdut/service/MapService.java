package org.yamdut.service;

import java.nio.channels.UnsupportedAddressTypeException;

import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

import java.util.List;

public class MapService {
  //OSRM integration
  public List<GeoPosition> fetchRoute(List<GeoPosition> points) {
    // TODO: call OSRM
    throws new UnsupportedAddressException("OSRM not wired yet");
  }
}