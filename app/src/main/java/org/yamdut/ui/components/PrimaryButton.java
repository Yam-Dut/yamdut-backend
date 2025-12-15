package org.yamdut.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.yamdut.utils.Theme;

public class PrimaryButton extends JPanel {
   private JButton button;

   public PrimaryButton(String text) {
    initButton(text);
    setupStyles();
   }

   private void initButton(String text) {
        setLayout(new BorderLayout());
        setOpaque(false);
        
        button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(Theme.COLOR_PRIMARY);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        add(button, BorderLayout.CENTER);
    }
    
    private void setupStyles() {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Theme.COLOR_PRIMARY.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Theme.COLOR_PRIMARY);
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(Theme.COLOR_PRIMARY.darker().darker());
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(Theme.COLOR_PRIMARY.darker());
            }
        });
    }
    
    public JButton getButton() {
        return button;
    }
    
    public void setText(String text) {
        button.setText(text);
    }
    
    public void setEnabled(boolean enabled) {
        button.setEnabled(enabled);
        button.setBackground(enabled ? Theme.COLOR_PRIMARY : Theme.TEXT_SECONDARY);
    }
}
