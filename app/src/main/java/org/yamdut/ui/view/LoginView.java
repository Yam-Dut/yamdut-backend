package org.yamdut.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginView extends BaseView {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;

    public LoginView() {
        super("YamDut - Login");
        setSize(500, 600);
        setLocationRelativeTo(null);
        initializeComponents();
    }

    @Override
    public void initializeComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_SILVER);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(PANEL_WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel logoLabel = new JLabel("🚖");
        logoLabel.setFont(new Font("Arial", Font.PLAIN, 60));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel titleLabel = new JLabel("YamDut - Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameLabel.setForeground(TEXT_DARK);
        centerPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        usernameField.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        centerPanel.add(usernameField);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setForeground(TEXT_DARK);
        centerPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        centerPanel.add(passwordField);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(ACCENT_BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        centerPanel.add(loginButton);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel signUpLabel = new JLabel("Don't have an account?");
        signUpLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        signUpLabel.setForeground(TEXT_DARK);
        signUpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(signUpLabel);

        signUpButton = new JButton("Sign Up");
        signUpButton.setFont(new Font("Arial", Font.BOLD, 13));
        signUpButton.setBackground(ACCENT_GREEN);
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        signUpButton.setFocusPainted(false);
        signUpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        centerPanel.add(signUpButton);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    public void addSignUpNavigationListener(ActionListener listener) {
        signUpButton.addActionListener(listener);
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}
