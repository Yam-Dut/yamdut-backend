package org.yamdut.view.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.yamdut.utils.Theme;

public class ModernButton extends JButton {
    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;
    
    public ModernButton(String text, Color bgColor) {
        super(text);
        this.normalColor = bgColor;
        this.hoverColor = bgColor.darker();
        this.pressedColor = bgColor.darker().darker();
        
        setupButton();
    }
    
    public ModernButton(String text) {
        this(text, Theme.COLOR_PRIMARY);
    }
    
    private void setupButton() {
        setFont(Theme.getButtonFont());
        setForeground(Color.WHITE);
        setBackground(normalColor);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(0, 48));
        setBorder(new EmptyBorder(0, 24, 0, 24));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(hoverColor);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(normalColor);
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(pressedColor);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(hoverColor);
                }
            }
        });
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            setBackground(normalColor);
        } else {
            setBackground(Theme.TEXT_SECONDARY);
        }
    }
}

