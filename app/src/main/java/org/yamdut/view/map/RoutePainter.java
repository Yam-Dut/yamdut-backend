package org.yamdut.view.map;


import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;

import org.jxmapviewer.painter.Painter;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import org.yamdut.utils.Theme;

public class RoutePainter implements Painter<JXMapViewer> {

    private final List<GeoPosition> track;

    public RoutePainter(List<GeoPosition> track) {
        this.track = track;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
        g = (Graphics2D) g.create();
        g.setColor(Theme.COLOR_PRIMARY);
        g.setStroke(new BasicStroke(4f));

        Rectangle viewport = map.getViewportBounds();
        Point2D lastPoint = null;

        for (GeoPosition gp : track) {
            Point2D worldPt =
                    map.getTileFactory().geoToPixel(gp, map.getZoom());

            Point2D screen = new Point2D.Double(
                    worldPt.getX() - viewport.getX(),
                    worldPt.getY() - viewport.getY()
            );

            if (lastPoint != null) {
                g.drawLine(
                        (int) lastPoint.getX(),
                        (int) lastPoint.getY(),
                        (int) screen.getX(),
                        (int) screen.getY()
                );
            }
            lastPoint = screen;
        }
        g.dispose();
    }
}
