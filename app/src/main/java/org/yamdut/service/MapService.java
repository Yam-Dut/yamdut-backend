package org.yamdut.service;

/**
 * Utility service for generating Mapbox-based 3D map HTML snippets.
 * This is consumed by JavaFX WebView (MapViewer3D) inside the Swing app.
 */
public class MapService {

        // Demo token from your example – in real apps keep this outside source control
        private static final String HARDCODED_MAPBOX_TOKEN =
          "pk.eyJ1IjoieWFtZHV0YXBwIiwiYSI6ImNsdnBnaDZ3bzAxbGQya3BxbGpmN3N0ZXMifQ.dlqj5Uc75Wnq2D4XEQw7aQ";

    // lat, lng, name
    public static final double[][] MAJOR_CITIES = {
            {27.7172, 85.3240, /* Kathmandu */ 0},
            {28.7041, 77.1025, 0},
            {19.0760, 72.8777, 0},
            {22.5726, 88.3639, 0},
            {20.5937, 78.9629, 0}
    };

    public static String getMapboxToken() {
      String env = System.getenv("MAPBOX_TOKEN");
      if (env != null && !env.isBlank()) return env;
      return HARDCODED_MAPBOX_TOKEN;
    }

    /**
     * Simple embedded 3D-capable Mapbox map centered at given coordinates.
     */
    public static String getEmbedded3DMapHTML(double lat, double lng, String title, boolean showTerrain) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head>")
          .append("<meta charset='utf-8'>")
          .append("<title>Yamdut 3D Map</title>")
          .append("<style>")
          .append("body{margin:0;padding:0;}#map{position:absolute;top:0;bottom:0;width:100%;height:100%;}")
          .append(".map-overlay{position:absolute;left:0;padding:10px;background:rgba(255,255,255,0.9);")
          .append("border-radius:0 0 10px 0;z-index:1000;}")
          .append("</style>")
          .append("<script src='https://api.mapbox.com/mapbox-gl-js/v2.9.1/mapbox-gl.js'></script>")
          .append("<link href='https://api.mapbox.com/mapbox-gl-js/v2.9.1/mapbox-gl.css' rel='stylesheet'/>")
          .append("</head><body>")
          .append("<div id='map'></div>")
          .append("<div class='map-overlay'>")
          .append("<h3 style='margin:0 0 5px 0;'>").append(title).append("</h3>")
          .append("<div>Lat: <span id='lat'>").append(lat).append("</span></div>")
          .append("<div>Lng: <span id='lng'>").append(lng).append("</span></div>")
          .append("<div>Zoom: <span id='zoom'>12</span></div>")
          .append("</div>")
          .append("<script>")
            .append("mapboxgl.accessToken='").append(getMapboxToken()).append("';")
          .append("const center=[").append(lng).append(",").append(lat).append("];")
          .append("const map=new mapboxgl.Map({container:'map',style:'mapbox://styles/mapbox/streets-v11',")
          .append("center:center,zoom:12,pitch:0,bearing:0});")
          .append("map.on('load',()=>{");
        if (showTerrain) {
            sb.append("map.addSource('mapbox-dem',{type:'raster-dem',url:'mapbox://mapbox.mapbox-terrain-dem-v1',")
              .append("tileSize:512,maxzoom:14});")
              .append("map.setTerrain({source:'mapbox-dem',exaggeration:1.5});");
        }
        sb.append("new mapboxgl.Marker({color:'#FF0000'}).setLngLat(center).addTo(map);")
          .append("map.addControl(new mapboxgl.NavigationControl());")
          .append("});")
          .append("</script></body></html>");
        return sb.toString();
    }

    public static String getRouteSimulationHTML(double startLat, double startLng,
                                                double endLat, double endLng) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head>")
          .append("<meta charset='utf-8'>")
          .append("<title>Yamdut Ride Simulation</title>")
          .append("<style>")
          .append("body{margin:0;padding:0;}#map{position:absolute;top:0;bottom:0;width:100%;height:100%;}")
          .append("</style>")
          .append("<script src='https://api.mapbox.com/mapbox-gl-js/v2.9.1/mapbox-gl.js'></script>")
          .append("<link href='https://api.mapbox.com/mapbox-gl-js/v2.9.1/mapbox-gl.css' rel='stylesheet'/>")
          .append("</head><body><div id='map'></div><script>")
            .append("mapboxgl.accessToken='").append(getMapboxToken()).append("';")
          .append("const start=[").append(startLng).append(",").append(startLat).append("];")
          .append("const end=[").append(endLng).append(",").append(endLat).append("];")
          .append("const map=new mapboxgl.Map({container:'map',style:'mapbox://styles/mapbox/streets-v11',")
          .append("center:start,zoom:13,pitch:45,bearing:-17.6});")
          .append("map.on('load',()=>{")
          .append("map.addSource('mapbox-dem',{type:'raster-dem',url:'mapbox://mapbox.mapbox-terrain-dem-v1',")
          .append("tileSize:512,maxzoom:14});")
          .append("map.setTerrain({source:'mapbox-dem',exaggeration:1.5});")
          .append("new mapboxgl.Marker({color:'#4CAF50'}).setLngLat(start).addTo(map);")
          .append("new mapboxgl.Marker({color:'#F44336'}).setLngLat(end).addTo(map);")
          .append("});</script></body></html>");
        return sb.toString();
    }
}


