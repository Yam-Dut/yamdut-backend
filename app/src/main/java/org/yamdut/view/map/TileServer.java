package org.yamdut.view.map;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class TileServer {
    private HttpServer server;
    private static final int PORT = 8765;
    private static TileServer instance;

    private TileServer() {}

    public static TileServer getInstance() {
        if (instance == null) {
            instance = new TileServer();
        }
        return instance;
    }

    public synchronized void start() throws IOException {
        if (server != null) {
            System.out.println("[TileServer] Already running on port " + PORT);
            return;
        }

        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/tiles", new TileHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("[TileServer] Started on port " + PORT);
        } catch (java.net.BindException e) {
            System.out.println("[TileServer] Port " + PORT + " already in use - assuming server is already running");
            // Port already in use, which means server is already running somewhere
            // This is fine, we can continue
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
            System.out.println("[TileServer] Stopped");
        }
    }

    static class TileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            // path will be like /tiles/14/12004/7420.png
            // Vector tiles are gzip compressed .pbf files with .png extension
            String resourcePath = "/map" + path;
            
            System.out.println("[TileServer] Requesting: " + resourcePath);
            
            InputStream is = TileHandler.class.getResourceAsStream(resourcePath);
            
            if (is != null) {
                try {
                    // Read the gzipped data
                    byte[] compressedBytes = is.readAllBytes();
                    is.close();
                    
                    // Set headers for vector tiles
                    exchange.getResponseHeaders().set("Content-Type", "application/x-protobuf");
                    exchange.getResponseHeaders().set("Content-Encoding", "gzip");
                    exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().set("Cache-Control", "public, max-age=86400");
                    
                    exchange.sendResponseHeaders(200, compressedBytes.length);
                    
                    OutputStream os = exchange.getResponseBody();
                    os.write(compressedBytes);
                    os.close();
                    
                    System.out.println("[TileServer] Served vector tile: " + resourcePath + " (" + compressedBytes.length + " bytes compressed)");
                } catch (Exception e) {
                    System.err.println("[TileServer] Error serving tile: " + e.getMessage());
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, 0);
                    exchange.getResponseBody().close();
                }
            } else {
                System.err.println("[TileServer] Tile not found: " + resourcePath);
                String response = "Tile not found: " + resourcePath;
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}