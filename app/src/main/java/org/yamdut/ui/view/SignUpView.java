package org.yamdut.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SignUpView extends BaseView {
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JButton signUpButton;
    private JButton backButton;

    public SignUpView() {
        super("YamDut - Sign Up");
        setSize(500, 750);
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
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        addField(centerPanel, "Full Name", fullNameField = new JTextField());
        addField(centerPanel, "Email", emailField = new JTextField());
        addField(centerPanel, "Phone", phoneField = new JTextField());
        addField(centerPanel, "Username", usernameField = new JTextField());
        addField(centerPanel, "Password", passwordField = new JPasswordField());
        addField(centerPanel, "Confirm Password", confirmPasswordField = new JPasswordField());

        JLabel roleLabel = new JLabel("Role");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        roleLabel.setForeground(TEXT_DARK);
        centerPanel.add(roleLabel);

        roleComboBox = new JComboBox<>(new String[]{"USER", "ADMIN"});
        roleComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        centerPanel.add(roleComboBox);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        signUpButton = new JButton("Sign Up");
        signUpButton.setFont(new Font("Arial", Font.BOLD, 14));
        signUpButton.setBackground(ACCENT_GREEN);
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        signUpButton.setFocusPainted(false);
        signUpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        centerPanel.add(signUpButton);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        backButton = new JButton("Back to Login");
        backButton.setFont(new Font("Arial", Font.BOLD, 13));
        backButton.setBackground(ACCENT_BLUE);
        backButton.setForeground(Color.WHITE);
        backButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        centerPanel.add(backButton);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void addField(JPanel panel, String label, JTextField field) {
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        fieldLabel.setForeground(TEXT_DARK);
        panel.add(fieldLabel);

        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        panel.add(field);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    public void addSignUpListener(ActionListener listener) {
        signUpButton.addActionListener(listener);
    }

    public void addBackToLoginListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    public String getFullName() {
        return fullNameField.getText().trim();
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getPhone() {
        return phoneField.getText().trim();
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public String getConfirmPassword() {
        return new String(confirmPasswordField.getPassword());
    }

    public String getSelectedRole() {
        return (String) roleComboBox.getSelectedItem();
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearFields() {
        fullNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }
}