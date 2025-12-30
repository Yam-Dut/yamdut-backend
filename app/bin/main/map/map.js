const YamdutMap = (() => {
    let map = null;
    let role = "passenger";
    let markers = {};
    let routeLayer = null;
    let currentTileLayer = null;

    function log(msg) {
        console.log('[YamdutMap] ' + msg);
        if (window.java && window.java.logDebug) {
            window.java.logDebug(msg);
        }
    }

    // Try different tile sources with fallbacks
    function createTileLayer() {
        // Primary: Standard OpenStreetMap tiles (most reliable)
        const osmTileLayer = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
            minZoom: 1,
            maxZoom: 19,
            tileSize: 256,
            zoomOffset: 0,
            updateWhenZooming: false,
            updateWhenIdle: true,
            keepBuffer: 2,
            crossOrigin: false
        });

        // Fallback 1: OpenStreetMap France (backup)
        const osmFrTileLayer = L.tileLayer('https://{s}.tile.openstreetmap.fr/osmfr/{z}/{x}/{y}.png', {
            attribution: '&copy; OpenStreetMap France',
            minZoom: 1,
            maxZoom: 20,
            subdomains: 'abc',
            tileSize: 256,
            updateWhenZooming: false,
            updateWhenIdle: true,
            keepBuffer: 2,
            crossOrigin: false
        });

        // Fallback 2: Wikimedia maps
        const wikimediaTileLayer = L.tileLayer('https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
            minZoom: 1,
            maxZoom: 19,
            tileSize: 256,
            updateWhenZooming: false,
            updateWhenIdle: true,
            keepBuffer: 2,
            crossOrigin: false
        });

        // Try primary first
        osmTileLayer.on('tileerror', function(error, tile) {
            log('OSM tile error, trying fallback');
            if (currentTileLayer === osmTileLayer) {
                map.removeLayer(osmTileLayer);
                osmFrTileLayer.addTo(map);
                currentTileLayer = osmFrTileLayer;
                
                osmFrTileLayer.on('tileerror', function() {
                    log('OSM-FR tile error, trying Wikimedia');
                    if (currentTileLayer === osmFrTileLayer) {
                        map.removeLayer(osmFrTileLayer);
                        wikimediaTileLayer.addTo(map);
                        currentTileLayer = wikimediaTileLayer;
                    }
                });
            }
        });

        return osmTileLayer;
    }

    function init(userRole) {
        role = userRole;
        log('Initializing map with role: ' + role);
        
        if (map) {
            log('Map exists, resizing');
            setTimeout(() => {
                map.invalidateSize(true);
            }, 100);
            return;
        }

        // Kathmandu bounds
        const kathmanduBounds = L.latLngBounds([27.60, 85.20], [27.80, 85.45]);
        
        log('Creating Leaflet map instance');
        
        // Simplified map options for better compatibility
        map = L.map("map", {
            maxBounds: kathmanduBounds,
            maxBoundsViscosity: 0.8,
            zoomControl: true,
            attributionControl: true,
            preferCanvas: false,  // Use SVG for better WebView compatibility
            fadeAnimation: false,  // Disable animations that might cause issues
            zoomAnimation: false,
            markerZoomAnimation: false,
            doubleClickZoom: true,
            scrollWheelZoom: true,
            boxZoom: true,
            keyboard: true,
            dragging: true,
            touchZoom: true,
            tap: true
        }).setView([27.7172, 85.3240], 14);

        // Create and add tile layer
        currentTileLayer = createTileLayer();
        currentTileLayer.addTo(map);
        log('Tile layer added');

        // Handle map click
        map.on("click", function(e) {
            log('Map clicked: ' + e.latlng.lat.toFixed(6) + ', ' + e.latlng.lng.toFixed(6));
            if (window.java && window.java.recieveMapClick) {
                window.java.recieveMapClick(e.latlng.lat, e.latlng.lng);
            }
        });

        // Handle map ready
        map.whenReady(function() {
            log('Map ready! Zoom: ' + map.getZoom());
            
            // Force multiple resize attempts
            setTimeout(() => {
                if (map) {
                    map.invalidateSize(true);
                    log('Resize attempt 1');
                }
            }, 100);
            
            setTimeout(() => {
                if (map) {
                    map.invalidateSize(true);
                    log('Resize attempt 2');
                }
            }, 300);
            
            setTimeout(() => {
                if (map) {
                    map.invalidateSize(true);
                    log('Resize attempt 3');
                }
            }, 600);
        });

        // Handle tile loading errors globally
        map.on('tileerror', function(error, tile) {
            log('Global tile error for: ' + (tile ? tile.src : 'unknown'));
        });
    }

    function setCenter(lat, lon, zoom) {
        if (!map) {
            log('Cannot set center - map not initialized');
            return;
        }
        map.setView([lat, lon], zoom || 14);
        log('Center set to: ' + lat + ',' + lon + ' zoom: ' + (zoom || 14));
    }

    function addOrUpdateMarker(id, lat, lon, type, label) {
        if (!map) {
            log('Cannot add marker - map not initialized');
            return;
        }
        
        if (markers[id]) {
            markers[id].setLatLng([lat, lon]);
            if (label) {
                markers[id].setPopupContent(label);
            }
            return;
        }

        // Create colored circle marker
        const color = type === 'driver' ? '#4CAF50' : '#2196F3';
        const icon = L.divIcon({
            className: 'yamdut-marker',
            html: '<div style="width:32px;height:32px;border-radius:50%;background:' + color + 
                  ';border:3px solid white;box-shadow:0 2px 6px rgba(0,0,0,0.3);"></div>',
            iconSize: [32, 32],
            iconAnchor: [16, 16]
        });

        const marker = L.marker([lat, lon], { icon: icon }).addTo(map);
        if (label) {
            marker.bindPopup(label);
        }
        markers[id] = marker;
        log('Marker added: ' + id);
    }

    function showEntities(entities) {
        if (!map) {
            log('Cannot show entities - map not initialized');
            return;
        }
        
        // Clear existing markers
        Object.values(markers).forEach(m => {
            map.removeLayer(m);
        });
        markers = {};
        
        // Parse entities
        let entitiesArray = entities;
        if (typeof entities === 'string') {
            try {
                entitiesArray = JSON.parse(entities);
            } catch (e) {
                log('Error parsing entities: ' + e.message);
                return;
            }
        }
        
        if (!Array.isArray(entitiesArray)) {
            log('Entities is not an array');
            return;
        }
        
        // Add markers
        entitiesArray.forEach(e => {
            // Don't show own type
            if ((role === "driver" && e.type === "driver") || 
                (role === "passenger" && e.type === "passenger")) {
                return;
            }
            addOrUpdateMarker(e.id, e.lat, e.lon, e.type, e.name || e.id);
        });
        
        log('Showed ' + entitiesArray.length + ' entities');
    }

    function updateEntityPosition(id, lat, lon) {
        if (markers[id]) {
            markers[id].setLatLng([lat, lon]);
        } else {
            log('Marker not found for update: ' + id);
        }
    }

    async function setRoute(start, end) {
        if (!map) {
            log('Cannot set route - map not initialized');
            return;
        }
        
        clearRoute();
        
        try {
            const url = 'https://router.project-osrm.org/route/v1/driving/' +
                start[1] + ',' + start[0] + ';' + end[1] + ',' + end[0] + 
                '?overview=full&geometries=geojson';
            
            log('Fetching route from OSRM');
            const res = await fetch(url);
            
            if (!res.ok) {
                throw new Error('Route API error: ' + res.status);
            }
            
            const data = await res.json();
            
            if (!data.routes || !data.routes.length) {
                log('No route found');
                return;
            }

            const coords = data.routes[0].geometry.coordinates.map(c => [c[1], c[0]]);
            routeLayer = L.polyline(coords, {
                color: '#2196F3',
                weight: 5,
                opacity: 0.8,
                smoothFactor: 1.0
            }).addTo(map);
            
            map.fitBounds(routeLayer.getBounds(), { padding: [50, 50] });
            log('Route displayed successfully');
        } catch(e) {
            log('Route error: ' + e.message);
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
            try {
                map.invalidateSize(true);
                // Force a redraw
                if (map._onResize) {
                    map._onResize();
                }
                log('Map resized');
            } catch (e) {
                log('Resize error: ' + e.message);
            }
        } else {
            log('Cannot resize - map not initialized');
        }
    }

    // Expose API
    return {
        init: init,
        setCenter: setCenter,
        addOrUpdateMarker: addOrUpdateMarker,
        showEntities: showEntities,
        updateEntityPosition: updateEntityPosition,
        setRoute: setRoute,
        clearRoute: clearRoute,
        clearMap: clearMap,
        resize: resize
    };
})();
