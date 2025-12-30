const YamdutMap = (() => {
    let map = null;
    let role = "passenger";
    let markers = {};
    let routeLayer = null;

    function log(msg) {
        console.log('[YamdutMap] ' + msg);
        if (window.javaConnector && window.javaConnector.logDebug) {
            window.javaConnector.logDebug(msg);
        }
    }

    function init(userRole) {
        role = userRole;
        log('Init with role: ' + role);
        
        if (map) {
            log('Map exists, resizing');
            setTimeout(() => map.invalidateSize(true), 100);
            return;
        }

        const kathmanduBounds = L.latLngBounds([27.60, 85.20], [27.80, 85.45]);

        log('Creating Leaflet map');
        map = L.map("map", {
            maxBounds: kathmanduBounds,
            maxBoundsViscosity: 1.0,
            zoomControl: true,
            attributionControl: true
        }).setView([27.7172, 85.3240], 14);

        // Mapbox Raster Tiles - YOU NEED TO GET A FREE API KEY FROM https://account.mapbox.com/
        // Replace 'YOUR_MAPBOX_TOKEN' with your actual token
        L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>',
            subdomains: 'abcd',
            maxZoom: 20
        }).addTo(map);
        log('Mapbox tiles added');

        // Force resize after tiles load
        setTimeout(() => {
            map.invalidateSize(true);
            log('Resized after init');
        }, 500);

        map.on("click", e => {
            log('Click: ' + e.latlng.lat.toFixed(4) + ', ' + e.latlng.lng.toFixed(4));
            if (window.javaConnector && window.javaConnector.recieveMapClick) {
                window.javaConnector.recieveMapClick(e.latlng.lat, e.latlng.lng);
            }
        });

        map.whenReady(() => {
            log('Map ready!');
            map.invalidateSize(true);
        });
    }

    function setCenter(lat, lon, zoom) {
        if (!map) {
            log('No map for setCenter');
            return;
        }
        map.setView([lat, lon], zoom);
        setTimeout(() => map.invalidateSize(true), 100);
    }

    function addOrUpdateMarker(id, lat, lon, type, label) {
        if (!map) return;

        if (markers[id]) {
            markers[id].setLatLng([lat, lon]);
            return;
        }

        const color = type === 'driver' ? '#4CAF50' : '#2196F3';
        const icon = L.divIcon({
            className: 'custom-marker',
            html: '<div style="width:30px;height:30px;border-radius:50%;background:' + color + ';border:4px solid white;box-shadow:0 3px 8px rgba(0,0,0,0.4);"></div>',
            iconSize: [30, 30],
            iconAnchor: [15, 15]
        });

        const marker = L.marker([lat, lon], { icon }).addTo(map);
        if (label) marker.bindPopup(label);
        markers[id] = marker;
    }

    function showEntities(entities) {
        if (!map) return;
        
        // Clear old markers first
        Object.values(markers).forEach(m => map.removeLayer(m));
        markers = {};
        
        entities.forEach(e => {
            if (role === "driver" && e.type === "driver") return;
            if (role === "passenger" && e.type === "passenger") return;
            addOrUpdateMarker(e.id, e.lat, e.lon, e.type, e.name);
        });
    }

    function updateEntityPosition(id, lat, lon) {
        if (markers[id]) {
            markers[id].setLatLng([lat, lon]);
        }
    }

    async function setRoute(start, end) {
        if (!map) return;
        clearRoute();
        
        try {
            const url = 'https://router.project-osrm.org/route/v1/driving/' +
                start[1] + ',' + start[0] + ';' + end[1] + ',' + end[0] + 
                '?overview=full&geometries=geojson';
            
            const res = await fetch(url);
            const data = await res.json();
            
            if (!data.routes || data.routes.length === 0) {
                log('No route found');
                return;
            }
            
            const coords = data.routes[0].geometry.coordinates.map(c => [c[1], c[0]]);
            routeLayer = L.polyline(coords, { 
                color: '#2196F3', 
                weight: 6,
                opacity: 0.7 
            }).addTo(map);
            map.fitBounds(routeLayer.getBounds(), { padding: [50, 50] });
            log('Route displayed');
        } catch (error) {
            log('Route error: ' + error.message);
        }
    }

    function clearRoute() {
        if (routeLayer) {
            map.removeLayer(routeLayer);
            routeLayer = null;
        }
    }

    function clearMap() {
        Object.values(markers).forEach(m => map.removeLayer(m));
        markers = {};
        clearRoute();
    }

    function resize() {
        if (map) {
            setTimeout(() => {
                map.invalidateSize(true);
                log('Map resized');
            }, 50);
        }
    }

    return {
        init,
        setCenter,
        addOrUpdateMarker,
        showEntities,
        updateEntityPosition,
        setRoute,
        clearRoute,
        clearMap,
        resize
    };
})();
