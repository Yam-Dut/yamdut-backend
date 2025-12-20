<<<<<<<< HEAD:app/src/main/java/org/yamdut/view/components/BaseDashboard.java
package org.yamdut.view.components;
========
package org.yamdut.view.dashboard;
>>>>>>>> 66332dfeaf8362fee426a76653917012e42a3180:app/src/main/java/org/yamdut/view/dashboard/BaseDashboard.java

import javax.swing.*;
import java.awt.*;

import org.yamdut.utils.Theme;

public abstract class BaseDashboard extends JPanel {
    protected JPanel headerPanel;
    protected JLabel welcomeLabel;
    protected JButton logoutButton;

    public BaseDashboard() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_PRIMARY);
        initHeader();
    }

    private void initHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.COLOR_PRIMARY);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        welcomeLabel = new JLabel();
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);

        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setForeground(Theme.COLOR_PRIMARY);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    public void setWelcomeMessage(String message) {
        welcomeLabel.setText(message);
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    protected abstract void initContent();
}


