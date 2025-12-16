mapboxgl.accessToken = "pk.eyJ1IjoiYWJoaXNoZWs2OSIsImEiOiJjbWo2MXBweGsxdGwzM2ZzYmlwMTBmeHV5In0.zuYgQ4F5JiCH6R6znK5T-w";

const map = new mapboxgl.Map({
    container: "map",
    style: "mapbox://styles/mapbox/standard", 
    center: [85.3240, 27.7172], // Kathmandu
    zoom: 14,
    pitch: 60,        // 🔥 3D ANGLE
    bearing: -17.6,   // 🔥 ROTATION
    antialias: true
});

// Controls
map.addControl(new mapboxgl.NavigationControl());
map.addControl(new mapboxgl.FullscreenControl());

map.on('load', () => {

    // 🔥 Enable 3D Terrain
    map.addSource('mapbox-dem', {
        type: 'raster-dem',
        url: 'mapbox://mapbox.mapbox-terrain-dem-v1',
        tileSize: 512,
        maxzoom: 14
    });

    map.setTerrain({ source: 'mapbox-dem', exaggeration: 1.5 });

    // 🔥 3D Buildings
    map.addLayer({
        id: '3d-buildings',
        source: 'composite',
        'source-layer': 'building',
        filter: ['==', 'extrude', 'true'],
        type: 'fill-extrusion',
        minzoom: 15,
        paint: {
            'fill-extrusion-color': '#aaa',
            'fill-extrusion-height': ['get', 'height'],
            'fill-extrusion-base': ['get', 'min_height'],
            'fill-extrusion-opacity': 0.9
        }
    });

});
