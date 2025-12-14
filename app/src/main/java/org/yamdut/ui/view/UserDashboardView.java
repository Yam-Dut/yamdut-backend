package org.yamdut.view;

import javax.swing.*;
import java.awt.*;

public class UserDashboardView extends BaseView {

    public UserDashboardView() {
        super("YamDut - User Dashboard");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        initializeComponents();
    }

    @Override
    public void initializeComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_SILVER);

        JLabel placeholderLabel = new JLabel("User Dashboard - Coming Soon");
        placeholderLabel.setFont(new Font("Arial", Font.BOLD, 24));
        placeholderLabel.setForeground(TEXT_DARK);
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);

        mainPanel.add(placeholderLabel, BorderLayout.CENTER);
        add(mainPanel);
    }
}
