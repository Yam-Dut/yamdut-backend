package org.yamdut.view.components;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.Properties;

/**
 * Simple Swing-only MapPanel placeholder to avoid requiring JavaFX at build time.
 * It attempts to load the local HTML map file into an editor pane as fallback.
 */
public class MapPanel extends JPanel {

    private JEditorPane htmlPane = new JEditorPane();

    public MapPanel() {
        super(new BorderLayout());
        setPreferredSize(new Dimension(900, 600));
        htmlPane.setContentType("text/html");
        htmlPane.setEditable(false);

        String html = loadMapHtml();
        // If HTML contains interactive Mapbox JS, JEditorPane can't render it — open in browser and show notice
        if (html.contains("mapboxgl") || html.contains("api.mapbox.com")) {
            htmlPane.setText("<html><body><p>Interactive map requires a browser. Opening in your default browser...</p></body></html>");
            try {
                java.io.File tmp = java.io.File.createTempFile("yamdut-map-", ".html");
                try (java.io.FileWriter fw = new java.io.FileWriter(tmp)) { fw.write(html); }
                if (java.awt.Desktop.isDesktopSupported()) java.awt.Desktop.getDesktop().browse(tmp.toURI());
            } catch (Exception ignored) {}
        } else {
            htmlPane.setText(html);
        }

        add(new JScrollPane(htmlPane), BorderLayout.CENTER);
    }

    private String loadMapHtml() {
        try (InputStream is = getClass().getResourceAsStream("/map/map.html")) {
            if (is == null) return "<html><body><p>Map not available</p></body></html>";
            byte[] bytes = is.readAllBytes();
            String html = new String(bytes);
            String token = System.getenv("MAPBOX_TOKEN");
            if (token == null || token.isBlank()) {
                token = "pk.eyJ1IjoiYWJoaXNoZWs2OSIsImEiOiJjbWo2MXBweGsxdGwzM2ZzYmlwMTBmeHV5In0.zuYgQ4F5JiCH6R6znK5T-w";
            }
            html = html.replace("###", token);
            return html;
        } catch (Exception e) {
            return "<html><body><p>Map load error</p></body></html>";
        }
    }

    public void stopTracking() {
        // no-op in placeholder
    }

    /**
     * Update the displayed HTML map content at runtime.
     */
    public void updateMapView(String html) {
        if (html == null) return;
        try {
            SwingUtilities.invokeLater(() -> htmlPane.setText(html));
        } catch (Exception ignored) {}
    }
}
