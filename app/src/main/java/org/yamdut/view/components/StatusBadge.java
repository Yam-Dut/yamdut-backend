package org.yamdut.view.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import org.yamdut.utils.Theme;

public class StatusBadge extends JLabel {

    public StatusBadge(String text, Color bgColor, Color textColor) {
        super(text);
        setFont(Theme.getCaptionFont());
        setForeground(textColor);
        setBackground(bgColor);
        setOpaque(true);
        setBorder(new EmptyBorder(4, 12, 4, 12));
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    public static StatusBadge online() {
        return new StatusBadge("Online", Theme.SUCCESS_COLOR, Color.WHITE);
    }

    public static StatusBadge offline() {
        return new StatusBadge("Offline", Theme.TEXT_SECONDARY, Color.WHITE);
    }

    public static StatusBadge active() {
        return new StatusBadge("Active", Theme.INFO_COLOR, Color.WHITE);
    }
}
