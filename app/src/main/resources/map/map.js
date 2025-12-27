const YamdutMap = (() => {
    let map;
    let role = "passenger";
    let markers = {};
    let routeLayer = null;

    function init(userRole) {
        role = userRole;

        map = L.map("map").setView([27.7172, 85.3240], 14);
        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
            maxZoom: 19
        }).addTo(map);

        map.on("click", e => {
            if (window.javaConnector) {
                window.javaConnector.receiveMapClick(e.latlng.lat, e.latlng.lng);
            }
        });
    }

    function setCenter(lat, lon, zoom) {
        map.setView([lat, lon], zoom);
    }

    function addOrUpdateMarker(id, lat, lon, type, label) {
        if (markers[id]) {
            markers[id].setLatlng([lat, lon]);
            return;
        }

        const icon = L.icon({
            iconUrl:
            type === "driver" ? "driver.png": "passenger.png",
            iconSize: [32, 32]
        });

        const marker = L.marker([lat, lon], {icon}).addTo(map);
        if (label) marker.bindPopup(label);
        markers[id] = marker;
    }

    function showEntities(entities) {
        entities.forEach(element => {
            if (role === "driver" && e.type === "driver") return;
            if (role === "passenger" && e.type === "passenger") return;

            addOrUpdateMarker(e.id, e.lat, e.lon, e.type, e.name);
        });
    }

    function updateEntityPosition(id, lat, lon) {
        if (markers[id]) {
            marker[id].setLatlng([lat, lon]);
        }
    }

    async function setRoute(start, end) {
        clearRoute();
        const url = 
            `https://router.project-osrm.org/route/v1/driving/` +
            `${start[1]},${start[0]};${end[1]},${end[0]}?overview=full&geometries=geojson`;


            const res = await fetch(url);
            const data = await res.json();

            const coords = data.routes[0].geometry.coordinates.map(
                c => [c[1], c[0]]
            );

            routeLayer = L.polyline(coords, {
                color: "blue",
                weight: 5
            }).addTo(map);
            map.fitBounds(routeLayer.getBounds());
    }

    function clearRoute() {
        if (routeLayer) {
            map.removeLayer(routeLayer);
            routeLayer = null;
        }
    }

    function clearMap() {
        Object.values(markers).forEach(m => map.removeLayer(m));
        clearRoute();
    }

    return {
        init,
        setCenter,
        showEntities,
        updateEntityPosition,
        setRoute,
        clearRoute,
        clearMap
    };
})();