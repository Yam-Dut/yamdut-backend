package org.yamdut.model;

import org.jxmapviewer.viewer.GeoPosition;

public class MapEntity {
   private final GeoPosition position;
   private final MapEntityType type;

   public MapEntity(double lat, double lon, MapEntityType type) {
    this.position = new GeoPosition(lat, lon);
    this.type = type;
   }

   public GeoPosition getPosition() {
    return position;
   }
   public MapEntityType getType() {
    return type;
   }
}
