// Yamdut Map Module - Leaflet Version (No WebGL required)
var YamdutMap = (function() {
    'use strict';
    
    var map = null;
    var markers = {};
    var routes = {};
    var userRole = 'passenger';
    var userLocation = [27.7172, 85.3240]; // Default: Kathmandu, Nepal
    var userMarker = null;
    var watchId = null;
    
    function log(msg) {
        console.log('[YamdutMap] ' + msg);
    }
    
    function logError(msg, err) {
        console.error('[YamdutMap ERROR] ' + msg, err);
    }
    
    function createMarkerIcon(type) {
        var colors = {
            driver: '#4CAF50',
            passenger: '#2196F3',
            pickup: '#FF9800',
            dropoff: '#F44336'
        };
        
        var color = colors[type] || colors.passenger;
        var letter = type === 'driver' ? 'D' : type === 'passenger' ? 'P' : '';
        
        return L.divIcon({
            html: '<div style="background: ' + color + '; width: 36px; height: 36px; border-radius: 50%; border: 3px solid white; display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; font-size: 16px; box-shadow: 0 2px 6px rgba(0,0,0,0.3);">' + letter + '</div>',
            iconSize: [36, 36],
            className: 'marker-' + type
        });
    }
    
    function init(role) {
        try {
            log('Initializing Leaflet map with role: ' + role);
            userRole = role || 'passenger';
            
            // Get map container
            var mapContainer = document.getElementById('map');
            if (!mapContainer) {
                logError('Map container not found!');
                return false;
            }
            
            log('Map container found, creating L.map instance...');
            
            // Initialize Leaflet map centered on Kathmandu
            map = L.map('map', {
                center: userLocation,
                zoom: 13,
                zoomControl: true,
                attributionControl: true
            });
            
            log('Map instance created');
            
            // Add OpenStreetMap tiles (free, no API key needed)
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '© OpenStreetMap contributors',
                maxZoom: 19,
                minZoom: 11,
                maxNativeZoom: 18,
                updateWhenZooming: false,
                updateWhenIdle: true
            }).addTo(map);
            
            log('Tile layer added - centered on Kathmandu');
            
            
            // Add zoom/pan controls
            map.zoomControl.setPosition('topright');
            
            // Add scale control
            L.control.scale({
                position: 'bottomleft',
                imperial: false,
                metric: true
            }).addTo(map);
            
            // Update info panel on move
            map.on('move', function() {
                var center = map.getCenter();
                var zoom = map.getZoom();
                document.getElementById('lat').textContent = center.lat.toFixed(4);
                document.getElementById('lng').textContent = center.lng.toFixed(4);
                document.getElementById('zoom').textContent = zoom;
            });
            
            // Show info panel
            var infoPanel = document.getElementById('infoPanel');
            if (infoPanel) {
                infoPanel.style.display = 'block';
                document.getElementById('role').textContent = userRole;
            }
            
            // Start location tracking
            startLocationTracking();
            
            log('Map initialization complete. Role: ' + userRole + ', Center: Kathmandu');
            return true;
            
        } catch (e) {
            logError('init() failed: ' + e.message, e);
            return false;
        }
    }
    
    function setCenter(lat, lng, zoom) {
        try {
            if (!map) {
                logError('Map not initialized');
                return false;
            }
            zoom = zoom || 13;
            map.setView([lat, lng], zoom);
            log('Map centered at ' + lat + ', ' + lng);
            return true;
        } catch (e) {
            logError('setCenter() failed: ' + e.message, e);
            return false;
        }
    }
    
    function addOrUpdateMarker(id, type, lat, lng, label) {
        try {
            if (!map) {
                logError('Map not initialized');
                return false;
            }
            
            // Remove old marker if exists
            if (markers[id]) {
                map.removeLayer(markers[id]);
            }
            
            // Create new marker
            var icon = createMarkerIcon(type);
            var marker = L.marker([lat, lng], { icon: icon });
            
            // Add popup with label
            if (label) {
                marker.bindPopup(label);
            }
            
            marker.addTo(map);
            markers[id] = marker;
            
            log('Marker added: ' + id + ' (' + type + ') at ' + lat + ', ' + lng);
            return true;
            
        } catch (e) {
            logError('addOrUpdateMarker() failed: ' + e.message, e);
            return false;
        }
    }
    
    function removeMarker(id) {
        try {
            if (markers[id]) {
                map.removeLayer(markers[id]);
                delete markers[id];
                log('Marker removed: ' + id);
                return true;
            }
            return false;
        } catch (e) {
            logError('removeMarker() failed: ' + e.message, e);
            return false;
        }
    }
    
    function showRoute(id, coordinates) {
        try {
            if (!map) {
                logError('Map not initialized');
                return false;
            }
            
            // Remove old route if exists
            if (routes[id]) {
                map.removeLayer(routes[id]);
            }
            
            if (!coordinates || coordinates.length === 0) {
                return false;
            }
            
            // Convert to Leaflet LatLng format
            var latlngs = [];
            for (var i = 0; i < coordinates.length; i++) {
                if (coordinates[i].lat !== undefined && coordinates[i].lng !== undefined) {
                    latlngs.push([coordinates[i].lat, coordinates[i].lng]);
                } else if (Array.isArray(coordinates[i]) && coordinates[i].length === 2) {
                    latlngs.push([coordinates[i][1], coordinates[i][0]]); // GeoJSON order
                }
            }
            
            if (latlngs.length > 0) {
                var polyline = L.polyline(latlngs, {
                    color: '#2196F3',
                    weight: 4,
                    opacity: 0.8,
                    dashArray: '5, 5'
                }).addTo(map);
                
                routes[id] = polyline;
                log('Route added: ' + id + ' with ' + latlngs.length + ' points');
                return true;
            }
            
            return false;
            
        } catch (e) {
            logError('showRoute() failed: ' + e.message, e);
            return false;
        }
    }
    
    function showRouteWithDistance(pickupLat, pickupLng, destLat, destLng, callback) {
        try {
            if (!map) {
                logError('Map not initialized');
                return;
            }
            
            log('Fetching route from OSRM: ' + pickupLat + ',' + pickupLng + ' to ' + destLat + ',' + destLng);
            
            // Clear existing route
            clearAllRoutes();
            
            // Add pickup and destination markers
            addOrUpdateMarker('route-pickup', 'pickup', pickupLat, pickupLng, 'Pickup');
            addOrUpdateMarker('route-destination', 'dropoff', destLat, destLng, 'Destination');
            
            // OSRM routing API (free service)
            var url = 'https://router.project-osrm.org/route/v1/driving/' + 
                      pickupLng + ',' + pickupLat + ';' + 
                      destLng + ',' + destLat + 
                      '?overview=full&geometries=geojson';
            
            fetch(url)
                .then(function(response) {
                    return response.json();
                })
                .then(function(data) {
                    if (data.code === 'Ok' && data.routes && data.routes.length > 0) {
                        var route = data.routes[0];
                        var coordinates = route.geometry.coordinates;
                        var distanceMeters = route.distance;
                        var durationSeconds = route.duration;
                        
                        log('Route found: ' + distanceMeters + 'm, ' + durationSeconds + 's');
                        
                        // Convert coordinates to Leaflet format
                        var latlngs = [];
                        for (var i = 0; i < coordinates.length; i++) {
                            latlngs.push([coordinates[i][1], coordinates[i][0]]);
                        }
                        
                        // Draw route on map
                        var polyline = L.polyline(latlngs, {
                            color: '#2196F3',
                            weight: 5,
                            opacity: 0.7
                        }).addTo(map);
                        
                        routes['main-route'] = polyline;
                        
                        // Fit map to show entire route
                        map.fitBounds(polyline.getBounds(), { padding: [50, 50] });
                        
                        // Calculate fare: 5 meters = 10 NPR
                        var fareNPR = Math.ceil((distanceMeters / 5) * 10);
                        var distanceKm = (distanceMeters / 1000).toFixed(2);
                        
                        log('Fare calculated: ' + fareNPR + ' NPR for ' + distanceKm + ' km');
                        
                        // Call Java callback if available
                        if (callback && typeof callback === 'function') {
                            callback(distanceMeters, fareNPR, durationSeconds);
                        }
                        
                        // Also call Java bridge if available
                        if (window.java && window.java.onRouteCalculated) {
                            window.java.onRouteCalculated(distanceMeters, fareNPR, durationSeconds);
                        }
                    } else {
                        logError('No route found');
                    }
                })
                .catch(function(error) {
                    logError('Route fetch error: ' + error.message, error);
                });
            
        } catch (e) {
            logError('showRouteWithDistance() failed: ' + e.message, e);
        }
    }
    
    function clearAllMarkers() {
        try {
            for (var id in markers) {
                if (markers.hasOwnProperty(id)) {
                    map.removeLayer(markers[id]);
                }
            }
            markers = {};
            log('All markers cleared');
            return true;
        } catch (e) {
            logError('clearAllMarkers() failed: ' + e.message, e);
            return false;
        }
    }
    
    function clearAllRoutes() {
        try {
            for (var id in routes) {
                if (routes.hasOwnProperty(id)) {
                    map.removeLayer(routes[id]);
                }
            }
            routes = {};
            log('All routes cleared');
            return true;
        } catch (e) {
            logError('clearAllRoutes() failed: ' + e.message, e);
            return false;
        }
    }
    
    function getMarkers() {
        var result = [];
        for (var id in markers) {
            if (markers.hasOwnProperty(id)) {
                var latLng = markers[id].getLatLng();
                result.push({
                    id: id,
                    lat: latLng.lat,
                    lng: latLng.lng
                });
            }
        }
        return result;
    }
    
    function setStyle(options) {
        try {
            if (!map) return false;
            if (options.zoom) map.setZoom(options.zoom);
            if (options.markerColor) {
                // Could customize marker colors here
            }
            log('Map style updated');
            return true;
        } catch (e) {
            logError('setStyle() failed: ' + e.message, e);
            return false;
        }
    }
    
    /**
     * Fit map to show all pickup and destination markers with animation
     */
    function fitToRoute() {
        try {
            if (!map) return false;
            
            var pickupMarker = markers['route-pickup'];
            var destMarker = markers['route-destination'];
            
            if (pickupMarker && destMarker) {
                var group = new L.featureGroup([pickupMarker, destMarker]);
                map.fitBounds(group.getBounds(), {
                    padding: [50, 50],
                    animate: true,
                    duration: 1.2
                });
                log('Map fitted to route with animation');
                return true;
            } else {
                log('No route markers available to fit bounds');
                return false;
            }
        } catch (e) {
            logError('fitToRoute() failed: ' + e.message, e);
            return false;
        }
    }
    
    /**
     * Animate map to center with zoom
     */
    function animateTo(lat, lng, zoom, duration) {
        try {
            if (!map) return false;
            
            duration = duration || 1;
            zoom = zoom || 14;
            
            map.flyTo([lat, lng], zoom, {
                animate: true,
                duration: duration
            });
            
            log('Map animating to: ' + lat + ', ' + lng + ' zoom: ' + zoom);
            return true;
        } catch (e) {
            logError('animateTo() failed: ' + e.message, e);
            return false;
        }
    }
    
    function startLocationTracking() {
        try {
            if (!navigator.geolocation) {
                log('Geolocation not supported by browser');
                return false;
            }
            
            log('Starting location tracking...');
            
            // Get current position once
            navigator.geolocation.getCurrentPosition(
                function(position) {
                    var lat = position.coords.latitude;
                    var lng = position.coords.longitude;
                    log('Current location: ' + lat + ', ' + lng);
                    
                    // Update user location
                    userLocation = [lat, lng];
                    
                    // Add or update user marker
                    if (userMarker) {
                        userMarker.setLatLng([lat, lng]);
                    } else {
                        var icon = createMarkerIcon(userRole);
                        userMarker = L.marker([lat, lng], { icon: icon })
                            .bindPopup('You are here')
                            .addTo(map);
                        markers['user-location'] = userMarker;
                    }
                    
                    // Center map on user location
                    map.setView([lat, lng], 15);
                },
                function(error) {
                    log('Geolocation error: ' + error.message);
                },
                {
                    enableHighAccuracy: true,
                    timeout: 5000,
                    maximumAge: 0
                }
            );
            
            // Watch position for live tracking
            watchId = navigator.geolocation.watchPosition(
                function(position) {
                    var lat = position.coords.latitude;
                    var lng = position.coords.longitude;
                    
                    userLocation = [lat, lng];
                    
                    if (userMarker) {
                        userMarker.setLatLng([lat, lng]);
                    }
                    
                    log('Location updated: ' + lat + ', ' + lng);
                },
                function(error) {
                    log('Watch position error: ' + error.message);
                },
                {
                    enableHighAccuracy: true,
                    timeout: 10000,
                    maximumAge: 30000
                }
            );
            
            return true;
        } catch (e) {
            logError('startLocationTracking() failed: ' + e.message, e);
            return false;
        }
    }
    
    function stopLocationTracking() {
        if (watchId) {
            navigator.geolocation.clearWatch(watchId);
            watchId = null;
            log('Location tracking stopped');
        }
    }
    
    // Public API
    return {
        init: init,
        setCenter: setCenter,
        addOrUpdateMarker: addOrUpdateMarker,
        removeMarker: removeMarker,
        showRoute: showRoute,
        showRouteWithDistance: showRouteWithDistance,
        clearAllMarkers: clearAllMarkers,
        clearAllRoutes: clearAllRoutes,
        getMarkers: getMarkers,
        setStyle: setStyle,
        fitToRoute: fitToRoute,
        animateTo: animateTo,
        startLocationTracking: startLocationTracking,
        stopLocationTracking: stopLocationTracking
    };
})();

// Make it available globally
console.log('[map.js] YamdutMap module loaded');
