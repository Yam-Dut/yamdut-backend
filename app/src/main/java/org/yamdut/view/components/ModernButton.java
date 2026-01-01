package org.yamdut.view.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import org.yamdut.utils.Theme;

public class ModernButton extends JButton {

    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private int arcWidth = 20;
    private int arcHeight = 20;

    public ModernButton(String text) {
        this(text, Theme.COLOR_PRIMARY);
    }

    public ModernButton(String text, Color baseColor) {
        super(text);
        this.normalColor = baseColor;
        this.hoverColor = baseColor.brighter();
        this.pressedColor = baseColor.darker();

        setFont(Theme.getButtonFont());
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(10, 20, 10, 20));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public void setBaseColor(Color color) {
        this.normalColor = color;
        this.hoverColor = color.brighter();
        this.pressedColor = color.darker();
        repaint();
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color paintColor = normalColor;
        if (!isEnabled()) {
            paintColor = Theme.TEXT_SECONDARY;
        } else if (isPressed) {
            paintColor = pressedColor;
        } else if (isHovered) {
            paintColor = hoverColor;
        }

        g2.setColor(paintColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);

        Font font = getFont();
        g2.setFont(font);

        // Draw centered text
        String text = getText();
        if (text != null) {
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int iconWidth = (getIcon() != null) ? getIcon().getIconWidth() + getIconTextGap() : 0;
            int textWidth = fm.stringWidth(text);
            int textX = (getWidth() - textWidth - iconWidth) / 2 + iconWidth;
            int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

            g2.setColor(getForeground());
            g2.drawString(text, textX, textY);

            if (getIcon() != null) {
                int iconY = (getHeight() - getIcon().getIconHeight()) / 2;
                getIcon().paintIcon(this, g2, textX - iconWidth, iconY);
            }
        }

        g2.dispose();
    }
}
