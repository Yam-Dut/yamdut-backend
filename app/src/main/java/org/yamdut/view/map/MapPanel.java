package org.yamdut.view.map;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Map panel that uses JavaFX WebView embedded in Swing (JFXPanel) to render
 * Mapbox-powered HTML. Falls back to the bundled `/map/map.html` when no
 * `MAPBOX_TOKEN` is provided. If JavaFX is not available, opens the map in
 * the user's browser as a last resort.
 */
public class MapPanel extends JPanel {
    private javax.swing.JComponent browserComponent;

    public MapPanel() {
        super(new BorderLayout());
        setPreferredSize(new Dimension(900, 600));

        // try JavaFX WebView embedding
        try {
            initJavaFxWebView();
        } catch (Throwable t) {
            // JavaFX not available or failed — fall back to a simple HTML pane
            browserComponent = createFallbackHtmlPane();
            add(browserComponent, BorderLayout.CENTER);
        }
    }

    private void initJavaFxWebView() throws Exception {
        // Use reflection to avoid compile-time dependency on JavaFX classes.
        Class<?> jfxPanelClass = Class.forName("javafx.embed.swing.JFXPanel");
        Object jfxPanel = jfxPanelClass.getDeclaredConstructor().newInstance();
        browserComponent = (JComponent) jfxPanel;
        add(browserComponent, BorderLayout.CENTER);

        // Platform.runLater(() -> { ... }) via reflection
        Class<?> platformClass = Class.forName("javafx.application.Platform");
        java.lang.reflect.Method runLater = platformClass.getMethod("runLater", Runnable.class);

        Runnable r = () -> {
            try {
                Class<?> webViewClass = Class.forName("javafx.scene.web.WebView");
                Object webView = webViewClass.getDeclaredConstructor().newInstance();

                // engine = webView.getEngine();
                java.lang.reflect.Method getEngine = webViewClass.getMethod("getEngine");
                Object engine = getEngine.invoke(webView);

                String html = buildMapHtml();

                // engine.loadContent(html, "text/html");
                Class<?> engineClass = Class.forName("javafx.scene.web.WebEngine");
                java.lang.reflect.Method loadContent = engineClass.getMethod("loadContent", String.class, String.class);
                loadContent.invoke(engine, html, "text/html");

                // Scene scene = new Scene(webView);
                Class<?> sceneClass = Class.forName("javafx.scene.Scene");
                Class<?> parentClass = Class.forName("javafx.scene.Parent");
                java.lang.reflect.Constructor<?> sceneCtor = sceneClass.getConstructor(parentClass);
                Object scene = sceneCtor.newInstance(webView);

                // jfxPanel.setScene(scene);
                java.lang.reflect.Method setScene = jfxPanelClass.getMethod("setScene", sceneClass);
                setScene.invoke(jfxPanel, scene);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        };

        runLater.invoke(null, r);
    }

    private JComponent createFallbackHtmlPane() {
        JEditorPane pane = new JEditorPane();
        pane.setContentType("text/html");
        pane.setEditable(false);
        pane.setText(loadLocalMapHtml());
        return new JScrollPane(pane);
    }

    private String buildMapHtml() {
        String token = System.getenv("MAPBOX_TOKEN");
        if (token == null || token.isBlank()) {
            return loadLocalMapHtml();
        }

        // Minimal Mapbox HTML template — interactive and uses Mapbox GL JS
        return "<html><head>" +
                "<meta charset='utf-8'/>" +
                "<meta name='viewport' content='initial-scale=1,maximum-scale=1,user-scalable=no'/>" +
                "<script src='https://api.mapbox.com/mapbox-gl-js/v2.15.0/mapbox-gl.js'></script>" +
                "<link href='https://api.mapbox.com/mapbox-gl-js/v2.15.0/mapbox-gl.css' rel='stylesheet'/>" +
                "<style>html,body,#map{height:100%;margin:0;padding:0}</style>" +
                "</head><body><div id='map'></div><script>" +
                "mapboxgl.accessToken='" + escapeForJs(token) + "';" +
                "const map=new mapboxgl.Map({container:'map',style:'mapbox://styles/mapbox/streets-v11',center:[85.3150,27.7050],zoom:13});" +
                "map.addControl(new mapboxgl.NavigationControl());" +
                "</script></body></html>";
    }

    private String loadLocalMapHtml() {
        try (InputStream is = getClass().getResourceAsStream("/map/map.html")) {
            if (is == null) return "<html><body><p>Map not available</p></body></html>";
            byte[] b = is.readAllBytes();
            String html = new String(b, StandardCharsets.UTF_8);
            String token = System.getenv("MAPBOX_TOKEN");
            if (token != null && !token.isBlank()) {
                html = html.replace("###", token);
            }
            return html;
        } catch (Exception e) {
            return "<html><body><p>Map load error</p></body></html>";
        }
    }

    private String escapeForJs(String s) {
        return s.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n");
    }

    /**
     * Load arbitrary HTML into the view (thread-safe). If JavaFX is used it will
     * update the WebView content; otherwise it will update the fallback pane.
     */
    public void loadHtml(String html) {
        if (html == null) return;
        // If JavaFX JFXPanel is present, update via reflection
        try {
            if (browserComponent != null && browserComponent.getClass().getName().equals("javafx.embed.swing.JFXPanel")) {
                Class<?> platformClass = Class.forName("javafx.application.Platform");
                java.lang.reflect.Method runLater = platformClass.getMethod("runLater", Runnable.class);
                Object jfxPanel = browserComponent;
                Runnable r = () -> {
                    try {
                        Class<?> webViewClass = Class.forName("javafx.scene.web.WebView");
                        Object webView = webViewClass.getDeclaredConstructor().newInstance();
                        java.lang.reflect.Method getEngine = webViewClass.getMethod("getEngine");
                        Object engine = getEngine.invoke(webView);
                        Class<?> engineClass = Class.forName("javafx.scene.web.WebEngine");
                        java.lang.reflect.Method loadContent = engineClass.getMethod("loadContent", String.class, String.class);
                        loadContent.invoke(engine, html, "text/html");
                        Class<?> sceneClass = Class.forName("javafx.scene.Scene");
                        Class<?> parentClass = Class.forName("javafx.scene.Parent");
                        java.lang.reflect.Constructor<?> sceneCtor = sceneClass.getConstructor(parentClass);
                        Object scene = sceneCtor.newInstance(webView);
                        java.lang.reflect.Method setScene = jfxPanel.getClass().getMethod("setScene", sceneClass);
                        setScene.invoke(jfxPanel, scene);
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                };
                runLater.invoke(null, r);
                return;
            }
        } catch (Throwable ignored) {}

        // fallback: try to set text on editor pane if present
        if (browserComponent instanceof JScrollPane) {
            JScrollPane sp = (JScrollPane) browserComponent;
            JViewport vp = sp.getViewport();
            if (vp.getView() instanceof JEditorPane) {
                JEditorPane pane = (JEditorPane) vp.getView();
                SwingUtilities.invokeLater(() -> pane.setText(html));
            }
        }
    }
}


