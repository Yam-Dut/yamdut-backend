package org.yamdut.view.map;



import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;


import javax.swing.*;
import java.awt.*;


public class MapPanel extends JPanel {
    private final JFXPanel fxPanel;
    private WebEngine webEngine;

    public MapPanel() {
        setLayout(new BorderLayout);
        fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);
    }
}