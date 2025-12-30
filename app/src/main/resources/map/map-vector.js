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
        
        if (map) {
            log('Map already exists, resizing');
            map.resize();
            return;
        }

        log('Creating Mapbox GL map for vector tiles');
        
        map = new maplibregl.Map({
            container: 'map',
            style: {
                'version': 8,
                'sources': {
                    'kathmandu': {
                        'type': 'vector',
                        'tiles': ['http://localhost:8765/tiles/{z}/{x}/{y}.png'],
                        'minzoom': 12,
                        'maxzoom': 17
                    }
                },
                'layers': [
                    {
                        'id': 'background',
                        'type': 'background',
                        'paint': {
                            'background-color': '#f0f0f0'
                        }
                    },
                    {
                        'id': 'water',
                        'type': 'fill',
                        'source': 'kathmandu',
                        'source-layer': 'water',
                        'paint': {
                            'fill-color': '#a0c8f0'
                        }
                    },
                    {
                        'id': 'landuse',
                        'type': 'fill',
                        'source': 'kathmandu',
                        'source-layer': 'landuse',
                        'paint': {
                            'fill-color': '#e0e0d0'
                        }
                    },
                    {
                        'id': 'roads',
                        'type': 'line',
                        'source': 'kathmandu',
                        'source-layer': 'transportation',
                        'paint': {
                            'line-color': '#ffffff',
                            'line-width': 2
                        }
                    },
                    {
                        'id': 'buildings',
                        'type': 'fill',
                        'source': 'kathmandu',
                        'source-layer': 'building',
                        'paint': {
                            'fill-color': '#d0d0d0',
                            'fill-opacity': 0.7
                        }
                    },
                    {
                        'id': 'place-labels',
                        'type': 'symbol',
                        'source': 'kathmandu',
                        'source-layer': 'place',
                        'layout': {
                            'text-field': ['get', 'name'],
                            'text-size': 12
                        },
                        'paint': {
                            'text-color': '#333333'
                        }
                    }
                ]
            },
            center: [85.3240, 27.7172],
            zoom: 14,
            maxBounds: [[85.20, 27.60], [85.45, 27.80]]
        });

        map.on('load', () => {
            log('Map loaded successfully');
        });

        map.on('error', (e) => {
            log('Map error: ' + JSON.stringify(e));
        });

        map.on('click', (e) => {
            log('Map clicked at: ' + e.lngLat.lat + ', ' + e.lngLat.lng);
            if (window.javaConnector && window.javaConnector.recieveMapClick) {
                window.javaConnector.recieveMapClick(e.lngLat.lat, e.lngLat.lng);
            }
        });
    }

    function setCenter(lat, lon, zoom) {
        if (!map) {
            log('Cannot set center - map not initialized');
            return;
        }
        log('Setting center to: ' + lat + ', ' + lon + ' zoom: ' + zoom);
        map.flyTo({ center: [lon, lat], zoom: zoom });
    }

    function addOrUpdateMarker(id, lat, lon, type, label) {
        if (!map) {
            log('Cannot add marker - map not initialized');
            return;
        }

        if (markers[id]) {
            log('Updating marker: ' + id);
            markers[id].setLngLat([lon, lat]);
            return;
        }

        log('Adding new marker: ' + id + ' type: ' + type);
        
        const el = document.createElement('div');
        el.className = 'marker';
        el.style.width = '32px';
        el.style.height = '32px';
        el.style.borderRadius = '50%';
        el.style.backgroundColor = type === 'driver' ? '#4CAF50' : '#2196F3';
        el.style.border = '2px solid white';
        el.style.cursor = 'pointer';

        const marker = new maplibregl.Marker(el)
            .setLngLat([lon, lat])
            .addTo(map);

        if (label) {
            marker.setPopup(new maplibregl.Popup().setHTML(label));
        }

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
            markers[id].setLngLat([lon, lat]);
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
            
            const coords = data.routes[0].geometry;
            
            map.addSource('route', {
                'type': 'geojson',
                'data': {
                    'type': 'Feature',
                    'properties': {},
                    'geometry': coords
                }
            });

            map.addLayer({
                'id': 'route',
                'type': 'line',
                'source': 'route',
                'layout': {
                    'line-join': 'round',
                    'line-cap': 'round'
                },
                'paint': {
                    'line-color': '#2196F3',
                    'line-width': 5
                }
            });

            routeLayer = 'route';
            
            const bounds = new maplibregl.LngLatBounds();
            coords.coordinates.forEach(coord => bounds.extend(coord));
            map.fitBounds(bounds, { padding: 50 });
            
            log('Route displayed');
        } catch (error) {
            log('Route error: ' + error.message);
        }
    }

    function clearRoute() {
        if (routeLayer && map.getLayer(routeLayer)) {
            log('Clearing route');
            map.removeLayer(routeLayer);
            map.removeSource(routeLayer);
            routeLayer = null;
        }
    }

    function clearMap() {
        log('Clearing map');
        Object.values(markers).forEach(m => m.remove());
        markers = {};
        clearRoute();
    }

    function resize() {
        if (map) {
            log('Resizing map');
            map.resize();
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