// =============================================
// CONFIGURATION & CONSTANTS
// =============================================
const CONFIG = {
    INITIAL_VIEW: [27.7172, 85.3240], // Kathmandu
    ZOOM_LEVEL: 14,
    TILE_LAYER: 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
    ATTRIBUTION: '© OpenStreetMap contributors',
    
    // Icon configuration
    ICONS: {
        driver: {
            url: '../icons/driver.png',
            size: [38, 38],
            anchor: [19, 38]
        },
        passenger: {
            url: '../icons/passenger.png',
            size: [32, 32],
            anchor: [16, 32]
        },
        pickup: {
            url: '../icons/pickup.png',
            size: [36, 36],
            anchor: [18, 36]
        },
        destination: {
            url: '../icons/destination.png',
            size: [36, 36],
            anchor: [18, 36]
        },
        vehicle: {
            url: '../icons/vehicle.png',
            size: [40, 40],
            anchor: [20, 40]
        }
    }
};

// =============================================
// STATE MANAGEMENT
// =============================================
const state = {
    map: null,
    markers: new Map(), // entityId -> marker
    routeLayer: null,
    selectedEntity: null,
    simulation: {
        active: false,
        interval: null,
        speed: 5,
        index: 0,
        positions: []
    },
    entities: []
};

// =============================================
// ICON MANAGEMENT
// =============================================
class IconManager {
    constructor() {
        this.iconCache = new Map();
        this.defaultIcon = L.divIcon({
            html: '<div class="default-marker"></div>',
            className: 'custom-marker',
            iconSize: [30, 30],
            iconAnchor: [15, 30]
        });
    }

    getIcon(type, status = 'active') {
        const cacheKey = `${type}_${status}`;
        
        if (this.iconCache.has(cacheKey)) {
            return this.iconCache.get(cacheKey);
        }

        const config = CONFIG.ICONS[type];
        if (!config) {
            console.warn(`No icon config for type: ${type}`);
            return this.defaultIcon;
        }

        const icon = L.icon({
            iconUrl: config.url,
            iconSize: config.size,
            iconAnchor: config.anchor,
            className: `map-icon ${type}-icon ${status}`
        });

        this.iconCache.set(cacheKey, icon);
        return icon;
    }

    createPopupContent(entity) {
        return `
            <div class="popup-content">
                <div class="popup-header">
                    <div class="popup-icon ${entity.type}">
                        <i class="fas ${this.getEntityIcon(entity.type)}"></i>
                    </div>
                    <h4>${entity.name || entity.type.toUpperCase()}</h4>
                </div>
                <div class="popup-details">
                    <div class="detail">
                        <span class="label">Type:</span>
                        <span class="value">${entity.type}</span>
                    </div>
                    <div class="detail">
                        <span class="label">Status:</span>
                        <span class="value ${entity.status}">${entity.status}</span>
                    </div>
                    ${entity.speed ? `<div class="detail">
                        <span class="label">Speed:</span>
                        <span class="value">${entity.speed} km/h</span>
                    </div>` : ''}
                    ${entity.eta ? `<div class="detail">
                        <span class="label">ETA:</span>
                        <span class="value">${entity.eta}</span>
                    </div>` : ''}
                </div>
                <button onclick="selectEntity('${entity.id}')" class="popup-btn">
                    <i class="fas fa-info-circle"></i> View Details
                </button>
            </div>
        `;
    }

    getEntityIcon(type) {
        const icons = {
            driver: 'fa-car',
            passenger: 'fa-user',
            vehicle: 'fa-truck',
            pickup: 'fa-map-marker-alt',
            destination: 'fa-flag-checkered'
        };
        return icons[type] || 'fa-map-marker';
    }
}

// =============================================
// MAP INITIALIZATION
// =============================================
class MapManager {
    constructor() {
        this.iconManager = new IconManager();
        this.initMap();
        this.setupControls();
        this.showStatus('Map loaded successfully', 'success');
    }

    initMap() {
        this.showLoading(true);
        
        // Initialize map
        state.map = L.map('map').setView(CONFIG.INITIAL_VIEW, CONFIG.ZOOM_LEVEL);
        
        // Add tile layer
        L.tileLayer(CONFIG.TILE_LAYER, {
            maxZoom: 19,
            attribution: CONFIG.ATTRIBUTION
        }).addTo(state.map);
        
        // Add scale control
        L.control.scale({ imperial: false }).addTo(state.map);
        
        // Fit bounds to Nepal
        state.map.fitBounds([
            [26.0, 84.0],
            [28.0, 88.0]
        ]);
        
        this.showLoading(false);
        
        // Add click handler for map
        state.map.on('click', (e) => {
            console.log('Map clicked at:', e.latlng);
        });
    }

    setupControls() {
        // Zoom controls
        document.getElementById('btnZoomIn').addEventListener('click', () => {
            state.map.zoomIn();
        });
        
        document.getElementById('btnZoomOut').addEventListener('click', () => {
            state.map.zoomOut();
        });
        
        // Location control
        document.getElementById('btnLocateMe').addEventListener('click', () => {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition((position) => {
                    const { latitude, longitude } = position.coords;
                    state.map.setView([latitude, longitude], 15);
                    this.showStatus('Location found', 'success');
                }, (error) => {
                    this.showStatus('Could not get location', 'error');
                });
            }
        });
        
        // Refresh button
        document.getElementById('btnRefresh').addEventListener('click', () => {
            this.refreshMap();
        });
        
        // Route controls
        document.getElementById('btnLoadRoute').addEventListener('click', () => {
            this.loadRoute();
        });
        
        document.getElementById('btnClearRoute').addEventListener('click', () => {
            this.clearRoute();
        });
        
        // Simulation controls
        document.getElementById('btnStartSim').addEventListener('click', () => {
            this.startSimulation();
        });
        
        document.getElementById('btnStopSim').addEventListener('click', () => {
            this.stopSimulation();
        });
        
        document.getElementById('btnPauseSim').addEventListener('click', () => {
            this.pauseSimulation();
        });
        
        // Speed control
        const speedSlider = document.getElementById('simSpeed');
        const speedValue = document.getElementById('speedValue');
        
        speedSlider.addEventListener('input', (e) => {
            state.simulation.speed = parseInt(e.target.value);
            speedValue.textContent = `${state.simulation.speed}x`;
            if (state.simulation.active) {
                this.updateSimulationSpeed();
            }
        });
        
        // Close panel button
        document.getElementById('btnClosePanel').addEventListener('click', () => {
            this.closeInfoPanel();
        });
    }

    // =============================================
    // PUBLIC API (Called from Java)
    // =============================================
    showEntities(entities) {
        if (!Array.isArray(entities)) {
            console.error('Entities must be an array');
            return;
        }

        // Clear existing markers
        this.clearMarkers();
        
        // Store entities
        state.entities = entities;
        
        // Update entity list in sidebar
        this.updateEntityList(entities);
        
        // Add markers to map
        entities.forEach(entity => {
            this.addEntityMarker(entity);
        });
        
        // Fit bounds to show all markers
        if (entities.length > 0) {
            const markers = Array.from(state.markers.values());
            const group = L.featureGroup(markers);
            state.map.fitBounds(group.getBounds().pad(0.1));
        }
        
        this.showStatus(`Loaded ${entities.length} entities`, 'success');
    }

    drawRoute(routePoints) {
        if (!Array.isArray(routePoints) || routePoints.length < 2) {
            console.error('Invalid route points');
            return;
        }

        // Clear existing route
        if (state.routeLayer) {
            state.map.removeLayer(state.routeLayer);
        }

        // Convert to LatLng array
        const latLngs = routePoints.map(point => [point.lat, point.lng || point.lon]);
        
        // Create polyline
        state.routeLayer = L.polyline(latLngs, {
            color: '#4f46e5',
            weight: 5,
            opacity: 0.8,
            lineJoin: 'round',
            lineCap: 'round',
            dashArray: '10, 10'
        }).addTo(state.map);

        // Add markers for start and end points
        const startIcon = this.iconManager.getIcon('pickup');
        const endIcon = this.iconManager.getIcon('destination');
        
        L.marker(latLngs[0], { icon: startIcon })
            .addTo(state.map)
            .bindPopup('Start Point');
        
        L.marker(latLngs[latLngs.length - 1], { icon: endIcon })
            .addTo(state.map)
            .bindPopup('End Point');

        // Fit bounds to show route
        state.map.fitBounds(state.routeLayer.getBounds().pad(0.2));
        
        // Store for simulation
        state.simulation.positions = latLngs;
        
        // Update route info
        this.updateRouteInfo(latLngs);
        
        this.showStatus(`Route drawn with ${latLngs.length} points`, 'success');
    }

    startSimulation() {
        if (state.simulation.positions.length < 2) {
            this.showStatus('No route available for simulation', 'error');
            return;
        }

        this.stopSimulation();
        
        state.simulation.active = true;
        state.simulation.index = 0;
        
        // Create simulation marker
        const startPos = state.simulation.positions[0];
        const simMarker = L.marker(startPos, {
            icon: L.divIcon({
                html: '<div class="simulation-marker"><i class="fas fa-car"></i></div>',
                className: 'simulation-marker-container',
                iconSize: [30, 30]
            })
        }).addTo(state.map);
        
        state.markers.set('simulation', simMarker);
        
        // Start simulation interval
        this.updateSimulationSpeed();
        
        this.showStatus('Simulation started', 'success');
        document.getElementById('btnStartSim').disabled = true;
        document.getElementById('btnStopSim').disabled = false;
    }

    stopSimulation() {
        if (state.simulation.interval) {
            clearInterval(state.simulation.interval);
            state.simulation.interval = null;
        }
        
        if (state.markers.has('simulation')) {
            state.map.removeLayer(state.markers.get('simulation'));
            state.markers.delete('simulation');
        }
        
        state.simulation.active = false;
        document.getElementById('btnStartSim').disabled = false;
        document.getElementById('btnStopSim').disabled = true;
        
        this.showStatus('Simulation stopped', 'info');
    }

    pauseSimulation() {
        if (state.simulation.interval) {
            clearInterval(state.simulation.interval);
            state.simulation.interval = null;
            this.showStatus('Simulation paused', 'warning');
        }
    }

    updateEntityPosition(entityId, position) {
        const marker = state.markers.get(entityId);
        if (marker) {
            marker.setLatLng([position.lat, position.lng || position.lon]);
            
            // Update entity in state
            const entityIndex = state.entities.findIndex(e => e.id === entityId);
            if (entityIndex !== -1) {
                state.entities[entityIndex] = {
                    ...state.entities[entityIndex],
                    ...position
                };
                
                // Update UI if this entity is selected
                if (state.selectedEntity?.id === entityId) {
                    this.updateInfoPanel(state.entities[entityIndex]);
                }
            }
        }
    }

    clearMap() {
        this.clearMarkers();
        this.clearRoute();
        this.stopSimulation();
        state.entities = [];
        this.updateEntityList([]);
        this.clearInfoPanel();
        
        // Reset view
        state.map.setView(CONFIG.INITIAL_VIEW, CONFIG.ZOOM_LEVEL);
        
        this.showStatus('Map cleared', 'info');
    }

    // =============================================
    // PRIVATE METHODS
    // =============================================
    addEntityMarker(entity) {
        const icon = this.iconManager.getIcon(entity.type, entity.status);
        const marker = L.marker([entity.lat, entity.lng || entity.lon], { icon })
            .addTo(state.map)
            .bindPopup(this.iconManager.createPopupContent(entity));
        
        marker.on('click', () => {
            this.selectEntity(entity.id);
        });
        
        state.markers.set(entity.id, marker);
    }

    selectEntity(entityId) {
        const entity = state.entities.find(e => e.id === entityId);
        if (!entity) return;
        
        state.selectedEntity = entity;
        this.updateInfoPanel(entity);
        
        // Highlight in list
        document.querySelectorAll('.entity-item').forEach(item => {
            item.classList.toggle('active', item.dataset.entityId === entityId);
        });
        
        // Center map on entity
        state.map.setView([entity.lat, entity.lng || entity.lon], 16);
        
        // Open info panel on mobile
        if (window.innerWidth < 992) {
            document.querySelector('.info-panel').classList.add('active');
        }
    }

    updateEntityList(entities) {
        const container = document.getElementById('entityList');
        container.innerHTML = '';
        
        entities.forEach(entity => {
            const item = document.createElement('div');
            item.className = `entity-item ${entity.id === state.selectedEntity?.id ? 'active' : ''}`;
            item.dataset.entityId = entity.id;
            
            item.innerHTML = `
                <div class="entity-icon ${entity.type}">
                    <i class="fas ${this.iconManager.getEntityIcon(entity.type)}"></i>
                </div>
                <div class="entity-info-mini">
                    <div class="entity-name">${entity.name || entity.type}</div>
                    <div class="entity-status">${entity.status || 'Active'}</div>
                </div>
            `;
            
            item.addEventListener('click', () => this.selectEntity(entity.id));
            container.appendChild(item);
        });
    }

    updateInfoPanel(entity) {
        document.getElementById('entityName').textContent = entity.name || entity.type;
        document.getElementById('entityType').textContent = entity.type;
        document.getElementById('entityStatus').textContent = entity.status || 'Active';
        document.getElementById('entityLocation').textContent = 
            `${entity.lat.toFixed(4)}, ${entity.lng?.toFixed(4) || entity.lon?.toFixed(4)}`;
        document.getElementById('entityEta').textContent = entity.eta || '-';
        
        // Update icon
        const iconElement = document.querySelector('.entity-icon-large');
        iconElement.className = `entity-icon-large ${entity.type}`;
        iconElement.innerHTML = `<i class="fas ${this.iconManager.getEntityIcon(entity.type)}"></i>`;
    }

    updateRouteInfo(latLngs) {
        // Calculate approximate distance (simplified)
        const distance = this.calculateDistance(latLngs);
        const duration = Math.round(distance / 20 * 60); // Assuming 20 km/h average
        
        document.getElementById('routeDistance').textContent = `${distance.toFixed(1)} km`;
        document.getElementById('routeDuration').textContent = `${duration} min`;
        document.getElementById('routePoints').textContent = latLngs.length;
    }

    clearInfoPanel() {
        document.getElementById('entityName').textContent = 'No Selection';
        document.getElementById('entityType').textContent = '-';
        document.getElementById('entityStatus').textContent = '-';
        document.getElementById('entityLocation').textContent = '-';
        document.getElementById('entityEta').textContent = '-';
        
        document.getElementById('routeDistance').textContent = '-';
        document.getElementById('routeDuration').textContent = '-';
        document.getElementById('routePoints').textContent = '-';
    }

    closeInfoPanel() {
        document.querySelector('.info-panel').classList.remove('active');
    }

    clearMarkers() {
        state.markers.forEach(marker => {
            state.map.removeLayer(marker);
        });
        state.markers.clear();
    }

    clearRoute() {
        if (state.routeLayer) {
            state.map.removeLayer(state.routeLayer);
            state.routeLayer = null;
        }
        state.simulation.positions = [];
        this.clearInfoPanel();
    }

    refreshMap() {
        this.showStatus('Refreshing map...', 'info');
        state.map.invalidateSize();
        setTimeout(() => {
            this.showStatus('Map refreshed', 'success');
        }, 500);
    }

    updateSimulationSpeed() {
        if (state.simulation.interval) {
            clearInterval(state.simulation.interval);
        }
        
        const intervalTime = 1000 / state.simulation.speed;
        
        state.simulation.interval = setInterval(() => {
            if (state.simulation.index >= state.simulation.positions.length - 1) {
                this.stopSimulation();
                return;
            }
            
            state.simulation.index++;
            const position = state.simulation.positions[state.simulation.index];
            
            const marker = state.markers.get('simulation');
            if (marker) {
                marker.setLatLng(position);
                
                // Center map on marker if near edge
                const bounds = state.map.getBounds();
                if (!bounds.contains(position)) {
                    state.map.panTo(position);
                }
            }
        }, intervalTime);
    }

    calculateDistance(latLngs) {
        // Simplified distance calculation (Haversine)
        let total = 0;
        for (let i = 1; i < latLngs.length; i++) {
            const [lat1, lng1] = latLngs[i - 1];
            const [lat2, lng2] = latLngs[i];
            
            const R = 6371; // Earth's radius in km
            const dLat = (lat2 - lat1) * Math.PI / 180;
            const dLng = (lng2 - lng1) * Math.PI / 180;
            const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                      Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                      Math.sin(dLng/2) * Math.sin(dLng/2);
            const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            total += R * c;
        }
        return total;
    }

    showStatus(message, type = 'info') {
        const statusBar = document.getElementById('statusBar');
        const icon = statusBar.querySelector('i');
        
        statusBar.querySelector('span').textContent = message;
        statusBar.className = `status-bar ${type}`;
        
        // Auto-hide after 3 seconds
        setTimeout(() => {
            if (statusBar.querySelector('span').textContent === message) {
                statusBar.className = 'status-bar';
            }
        }, 3000);
    }

    showLoading(show) {
        const overlay = document.getElementById('loadingOverlay');
        overlay.classList.toggle('active', show);
    }
}

// =============================================
// INITIALIZATION & WINDOW EXPORTS
// =============================================
let mapManager;

document.addEventListener('DOMContentLoaded', () => {
    mapManager = new MapManager();
    
    // Export to window for Java access
    window.YamdutMap = {
        showEntities: (entities) => mapManager.showEntities(entities),
        drawRoute: (routePoints) => mapManager.drawRoute(routePoints),
        startSimulation: () => mapManager.startSimulation(),
        stopSimulation: () => mapManager.stopSimulation(),
        pauseSimulation: () => mapManager.pauseSimulation(),
        updateEntityPosition: (id, pos) => mapManager.updateEntityPosition(id, pos),
        clearMap: () => mapManager.clearMap(),
        setCenter: (lat, lon, zoom) => state.map.setView([lat, lon], zoom || CONFIG.ZOOM_LEVEL),
        getState: () => ({
            entities: state.entities.length,
            markers: state.markers.size,
            simulation: state.simulation.active
        })
    };
    // For backward compatibility
    window.showEntities = (entities) => mapManager.showEntities(entities);
    window.drawRoute = (routePoints) => mapManager.drawRoute(routePoints);

    window.setMapClickListener = function(callback) {
        if (state.map) {
            state.map.on('click', (e) => {
                callback(e.latlng.lat, e.latlng.lng);
            });
        }
    };
});

// Helper function for popup buttons
window.selectEntity = (entityId) => {
    if (mapManager) {
        mapManager.selectEntity(entityId);
    }
};