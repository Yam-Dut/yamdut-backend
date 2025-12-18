package org.yamdut.view.auth;

import org.yamdut.utils.*;
import org.yamdut.controller.LoginController;
import org.yamdut.view.components.InputField;
import org.yamdut.view.components.PrimaryButton;

import javax.swing.border.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginScreen extends JPanel {
    private LoginController controller;

    // UI Components
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JLabel forgotPasswordLabel;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JCheckBox rememberMeCheckbox;

    // State
    private boolean isLoading = false;
    
    public LoginScreen(LoginController controller) {
        this.controller = controller;
        initUI();
        applyTheme();
        setupListeners();
    }

    private void initUI() {
        setLayout(new BorderLayout());
            setBackground(Theme.BACKGROUND_PRIMARY);
            
            // Create main content panel
            JPanel mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBackground(Theme.BACKGROUND_PRIMARY);
            mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 0, 15, 0);
            
            // App Logo/Title
            titleLabel = new JLabel("YamDut");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
            titleLabel.setForeground(Theme.COLOR_PRIMARY);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gbc.insets = new Insets(0, 0, 10, 0);
            mainPanel.add(titleLabel, gbc);
            
            // Subtitle
            subtitleLabel = new JLabel("Ride with convenience");
            subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            subtitleLabel.setForeground(Theme.TEXT_SECONDARY);
            subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gbc.insets = new Insets(0, 0, 30, 0);
            mainPanel.add(subtitleLabel, gbc);
            
            // Email Field
            InputField emailInput = new InputField("Email");
            emailField = emailInput.getTextField();
            emailField.setPreferredSize(new Dimension(300, 40));
            gbc.insets = new Insets(0, 0, 15, 0);
            mainPanel.add(emailInput, gbc);
            
            // Password Field
            InputField passwordInput = new InputField("Password", true);
            passwordField = passwordInput.getPasswordField();
            passwordField.setPreferredSize(new Dimension(300, 40));
            mainPanel.add(passwordInput, gbc);
            
            // Remember me and Forgot Password
            JPanel optionsPanel = new JPanel(new BorderLayout());
            optionsPanel.setBackground(Theme.BACKGROUND_PRIMARY);
            
            rememberMeCheckbox = new JCheckBox("Remember me");
            rememberMeCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            rememberMeCheckbox.setForeground(Theme.TEXT_SECONDARY);
            rememberMeCheckbox.setBackground(Theme.BACKGROUND_PRIMARY);
            rememberMeCheckbox.setFocusPainted(false);
            
            forgotPasswordLabel = new JLabel("Forgot Password?");
            forgotPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            forgotPasswordLabel.setForeground(Theme.COLOR_PRIMARY);
            forgotPasswordLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            optionsPanel.add(rememberMeCheckbox, BorderLayout.WEST);
            optionsPanel.add(forgotPasswordLabel, BorderLayout.EAST);
            gbc.insets = new Insets(5, 0, 25, 0);
            mainPanel.add(optionsPanel, gbc);
            
            // Login Button
            PrimaryButton loginBtn = new PrimaryButton("Sign In");
            loginButton = loginBtn.getButton();
            loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
            loginButton.setPreferredSize(new Dimension(300, 45));
            gbc.insets = new Insets(0, 0, 20, 0);
            mainPanel.add(loginButton, gbc);
            
            // Divider
            JPanel dividerPanel = new JPanel(new GridBagLayout());
            dividerPanel.setBackground(Theme.BACKGROUND_PRIMARY);
            
            JSeparator leftSeparator = new JSeparator();
            leftSeparator.setPreferredSize(new Dimension(100, 1));
            leftSeparator.setForeground(Theme.BORDER_COLOR);
            
            JLabel orLabel = new JLabel("or");
            orLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            orLabel.setForeground(Theme.TEXT_SECONDARY);
            
            JSeparator rightSeparator = new JSeparator();
            rightSeparator.setPreferredSize(new Dimension(100, 1));
            rightSeparator.setForeground(Theme.BORDER_COLOR);
            
            GridBagConstraints dividerGbc = new GridBagConstraints();
            dividerGbc.fill = GridBagConstraints.HORIZONTAL;
            dividerPanel.add(leftSeparator, dividerGbc);
            dividerPanel.add(Box.createHorizontalStrut(10));
            dividerPanel.add(orLabel);
            dividerPanel.add(Box.createHorizontalStrut(10));
            dividerPanel.add(rightSeparator, dividerGbc);
            
            gbc.insets = new Insets(0, 0, 20, 0);
            mainPanel.add(dividerPanel, gbc);
            
            // Sign Up Button
            signupButton = new JButton("Create New Account");
            signupButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            signupButton.setForeground(Theme.COLOR_PRIMARY);
            signupButton.setBackground(Color.WHITE);
            signupButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.COLOR_PRIMARY, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));
            signupButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            signupButton.setFocusPainted(false);
            signupButton.setPreferredSize(new Dimension(300, 45));
            mainPanel.add(signupButton, gbc);
            
            // Add main panel to center
            add(mainPanel, BorderLayout.CENTER);
            
            // Add footer
            JPanel footerPanel = new JPanel();
            footerPanel.setBackground(Theme.BACKGROUND_PRIMARY);
            footerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
            
            JLabel footerLabel = new JLabel("By signing in, you agree to our Terms & Privacy Policy");
            footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            footerLabel.setForeground(Theme.TEXT_SECONDARY);
            footerPanel.add(footerLabel);
            
            add(footerPanel, BorderLayout.SOUTH);
    }
    
    private void applyTheme() {
        // Apply colors from Theme class
        setBackground(Theme.BACKGROUND_PRIMARY);
    }

    private void setupListeners() {
        // Login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        // Sign up button action
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.navigateToSignup();
            }
        });
        
        // Forgot password click
        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleForgotPassword();
            }
        });
        
        // Enter key support
        emailField.addActionListener(e -> performLogin());
        passwordField.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        if (isLoading) return;
        
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Validate inputs
        if (!Validators.isValidEmail(email)) {
            showError("Please enter a valid email address");
            emailField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Please enter your password");
            passwordField.requestFocus();
            return;
        }
        
        // Show loading state
        setLoading(true);
        
        // Perform login via controller
        controller.login(email, password, rememberMeCheckbox.isSelected());
    }

    private void handleForgotPassword() {
        String email = JOptionPane.showInputDialog(
            this,
            "Enter your email address:",
            "Reset Password",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (email != null && !email.trim().isEmpty()) {
            if (Validators.isValidEmail(email)) {
                controller.requestPasswordReset(email);
            } else {
                showError("Please enter a valid email address");
            }
        }
    }
    public void setLoading(boolean loading) {
        this.isLoading = loading;
        loginButton.setEnabled(!loading);
        signupButton.setEnabled(!loading);
        loginButton.setText(loading ? "Signing In..." : "Sign In");
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Login Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    public void clearFields() {
        emailField.setText("");
        passwordField.setText("");
        emailField.requestFocus();
    }
    
    public void setEmail(String email) {
        emailField.setText(email);
    }

    public void setRememberMe(boolean remember) {
        rememberMeCheckbox.setSelected(remember);
    }
}


