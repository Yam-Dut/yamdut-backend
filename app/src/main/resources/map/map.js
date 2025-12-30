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
        log('Init called with role: ' + role);
        
        // Only create map once
        if (map) {
            log('Map already exists, just invalidating size');
            map.invalidateSize(true);
            return;
        }

        const kathmanduBounds = L.latLngBounds(
            [27.60, 85.20],
            [27.80, 85.45]
        );

        log('Creating map centered at Kathmandu');
        map = L.map("map", {
            maxBounds: kathmanduBounds,
            maxBoundsViscosity: 1.0
        }).setView([27.7172, 85.3240], 14);

        // Use local tile server
        log('Using tile server at http://localhost:8765/tiles/{z}/{x}/{y}.png');

        const tileLayer = L.tileLayer('http://localhost:8765/tiles/{z}/{x}/{y}.png', {
            minZoom: 12,
            maxZoom: 17,
            attribution: 'Yamdut Map',
            errorTileUrl: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=='
        });

        // Listen for tile load events
        tileLayer.on('tileerror', function(error) {
            log('Tile error: ' + error.tile.src);
        });

        tileLayer.on('tileload', function(e) {
            log('Tile loaded: ' + e.tile.src);
        });

        tileLayer.addTo(map);
        log('Tile layer added');

        // Add click handler
        map.on("click", e => {
            log('Map clicked at: ' + e.latlng.lat + ', ' + e.latlng.lng);
            if (window.javaConnector && window.javaConnector.recieveMapClick) {
                window.javaConnector.recieveMapClick(e.latlng.lat, e.latlng.lng);
            }
        });

        // Log when map is ready
        map.whenReady(function() {
            log('Map is ready! Current zoom: ' + map.getZoom() + ', center: ' + JSON.stringify(map.getCenter()));
        });
    }

    function setCenter(lat, lon, zoom) {
        if (!map) {
            log('Cannot set center - map not initialized');
            return;
        }
        log('Setting center to: ' + lat + ', ' + lon + ' zoom: ' + zoom);
        map.setView([lat, lon], zoom);
    }

    function addOrUpdateMarker(id, lat, lon, type, label) {
        if (!map) {
            log('Cannot add marker - map not initialized');
            return;
        }

        if (markers[id]) {
            log('Updating marker: ' + id);
            markers[id].setLatLng([lat, lon]);
            return;
        }

        log('Adding new marker: ' + id + ' type: ' + type);
        
        // Use default marker if custom icons don't load
        const icon = L.icon({
            iconUrl: type === "driver" ? "driver.png" : "passenger.png",
            iconSize: [32, 32],
            iconAnchor: [16, 32],
            popupAnchor: [0, -32]
        });

        const marker = L.marker([lat, lon], { icon }).addTo(map);
        if (label) marker.bindPopup(label);
        markers[id] = marker;
    }

    function showEntities(entities) {
        if (!map) {
            log('Cannot show entities - map not initialized');
            return;
        }
        
        log('Showing ' + entities.length + ' entities');
        entities.forEach(e => {
            if (role === "driver" && e.type === "driver") return;
            if (role === "passenger" && e.type === "passenger") return;
            addOrUpdateMarker(e.id, e.lat, e.lon, e.type, e.name);
        });
    }

    function updateEntityPosition(id, lat, lon) {
        if (markers[id]) {
            log('Updating position for: ' + id);
            markers[id].setLatLng([lat, lon]);
        } else {
            log('Cannot update - marker not found: ' + id);
        }
    }

    async function setRoute(start, end) {
        if (!map) {
            log('Cannot set route - map not initialized');
            return;
        }
        
        clearRoute();
        log('Setting route from ' + start + ' to ' + end);
        
        try {
            const url =
                `https://router.project-osrm.org/route/v1/driving/` +
                `${start[1]},${start[0]};${end[1]},${end[0]}?overview=full&geometries=geojson`;
            
            const res = await fetch(url);
            const data = await res.json();
            
            if (!data.routes || data.routes.length === 0) {
                log('No route found');
                return;
            }
            
            const coords = data.routes[0].geometry.coordinates.map(c => [c[1], c[0]]);
            routeLayer = L.polyline(coords, { color: "blue", weight: 5 }).addTo(map);
            map.fitBounds(routeLayer.getBounds());
            log('Route displayed');
        } catch (error) {
            log('Route error: ' + error.message);
        }
    }

    function clearRoute() {
        if (routeLayer) {
            log('Clearing route');
            map.removeLayer(routeLayer);
            routeLayer = null;
        }
    }

    function clearMap() {
        log('Clearing map');
        Object.values(markers).forEach(m => map.removeLayer(m));
        markers = {};
        clearRoute();
    }

    function resize() {
        if (map) {
            log('Resizing map');
            map.invalidateSize(true);
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