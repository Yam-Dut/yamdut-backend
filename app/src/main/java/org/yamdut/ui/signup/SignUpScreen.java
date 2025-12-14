package org.yamdut.ui.signup;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.yamdut.controller.SignupController;
import org.yamdut.ui.components.InputField;
import org.yamdut.ui.components.PrimaryButton;
import org.yamdut.utils.Theme;

public class SignUpScreen extends JPanel {
    private SignupController controller;
    
    // UI Components
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton signupButton;
    private JButton loginButton;
    private JComboBox<String> userTypeCombo;
    private JCheckBox termsCheckbox;
    private JLabel passwordStrengthLabel;
    
    // State
    private boolean isLoading = false;
    
    public SignUpScreen(SignupController controller) {
        this.controller = controller;
        initUI();
        applyTheme();
        setupListeners();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_PRIMARY);
        
        // Create scrollable panel for form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);
        
        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(0, 0, 10, 0);
        formPanel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Join YamDut for your ride sharing needs");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Theme.TEXT_SECONDARY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(0, 0, 30, 0);
        formPanel.add(subtitleLabel, gbc);
        
        // Name Field
        InputField nameInput = new InputField("Full Name");
        nameField = nameInput.getTextField();
        nameField.setPreferredSize(new Dimension(350, 40));
        formPanel.add(nameInput, gbc);
        
        // Email Field
        InputField emailInput = new InputField("Email Address");
        emailField = emailInput.getTextField();
        emailField.setPreferredSize(new Dimension(350, 40));
        formPanel.add(emailInput, gbc);
        
        // Phone Field
        InputField phoneInput = new InputField("Phone Number");
        phoneField = phoneInput.getTextField();
        phoneField.setPreferredSize(new Dimension(350, 40));
        formPanel.add(phoneInput, gbc);
        
        // User Type Selection
        JPanel userTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        userTypePanel.setBackground(Theme.BACKGROUND_PRIMARY);
        
        JLabel userTypeLabel = new JLabel("I want to:");
        userTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTypeLabel.setPreferredSize(new Dimension(100, 35));
        
        userTypeCombo = new JComboBox<>(new String[]{
            "Ride as Passenger", 
            "Drive as Partner"
        });
        userTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTypeCombo.setBackground(Color.WHITE);
        userTypeCombo.setPreferredSize(new Dimension(250, 35));
        userTypeCombo.setFocusable(false);
        
        userTypePanel.add(userTypeLabel);
        userTypePanel.add(Box.createHorizontalStrut(10));
        userTypePanel.add(userTypeCombo);
        formPanel.add(userTypePanel, gbc);
        
        // Password Field
        InputField passwordInput = new InputField("Password", true);
        passwordField = passwordInput.getPasswordField();
        passwordField.setPreferredSize(new Dimension(350, 40));
        formPanel.add(passwordInput, gbc);
        
        // Password Strength Indicator
        passwordStrengthLabel = new JLabel("");
        passwordStrengthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        passwordStrengthLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.insets = new Insets(-10, 0, 15, 0);
        formPanel.add(passwordStrengthLabel, gbc);
        gbc.insets = new Insets(0, 0, 15, 0);
        
        // Confirm Password Field
        InputField confirmPasswordInput = new InputField("Confirm Password", true);
        confirmPasswordField = confirmPasswordInput.getPasswordField();
        confirmPasswordField.setPreferredSize(new Dimension(350, 40));
        formPanel.add(confirmPasswordInput, gbc);
        
        // Terms Checkbox
        termsCheckbox = new JCheckBox("I agree to the Terms of Service and Privacy Policy");
        termsCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        termsCheckbox.setForeground(Theme.TEXT_SECONDARY);
        termsCheckbox.setBackground(Theme.BACKGROUND_PRIMARY);
        termsCheckbox.setFocusPainted(false);
        gbc.insets = new Insets(10, 0, 25, 0);
        formPanel.add(termsCheckbox, gbc);
        
        // Sign Up Button
        PrimaryButton signupBtn = new PrimaryButton("Create Account");
        signupButton = signupBtn.getButton();
        signupButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        signupButton.setPreferredSize(new Dimension(350, 45));
        gbc.insets = new Insets(0, 0, 20, 0);
        formPanel.add(signupButton, gbc);
        
        // Already have account
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        
        JLabel haveAccountLabel = new JLabel("Already have an account?");
        haveAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        haveAccountLabel.setForeground(Theme.TEXT_SECONDARY);
        
        loginButton = new JButton("Sign In");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setForeground(Theme.COLOR_PRIMARY);
        loginButton.setBackground(Theme.BACKGROUND_PRIMARY);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        loginPanel.add(haveAccountLabel);
        loginPanel.add(Box.createHorizontalStrut(5));
        loginPanel.add(loginButton);
        formPanel.add(loginPanel, gbc);
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void applyTheme() {
        setBackground(Theme.BACKGROUND_PRIMARY);
    }
    
    private void setupListeners() {
        // Sign up button action
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSignup();
            }
        });
        
        // Login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.navigateToLogin();
            }
        });
        
        // Real-time password strength check
        passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                updatePasswordStrength();
            }
        });
        
        // Enter key support
        nameField.addActionListener(e -> emailField.requestFocus());
        emailField.addActionListener(e -> phoneField.requestFocus());
        phoneField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> confirmPasswordField.requestFocus());
        confirmPasswordField.addActionListener(e -> performSignup());
    }
    
    private void performSignup() {
        if (isLoading) return;
        
        // Get form data
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        boolean isDriver = userTypeCombo.getSelectedIndex() == 1; // "Drive as Partner"
        boolean termsAccepted = termsCheckbox.isSelected();
        
        // Validate form
        if (!validateForm(name, email, phone, password, confirmPassword, termsAccepted)) {
            return;
        }
        
        // Show loading state
        setLoading(true);
        
        // Perform signup via controller
        controller.signup(name, email, password, phone, isDriver);
    }
    
    private boolean validateForm(String name, String email, String phone, 
                                String password, String confirmPassword, 
                                boolean termsAccepted) {
        // Name validation
        if (name.isEmpty()) {
            showError("Please enter your full name");
            nameField.requestFocus();
            return false;
        }
        
        if (name.length() < 2) {
            showError("Name must be at least 2 characters");
            nameField.requestFocus();
            return false;
        }
        
        // Email validation
        if (!Validators.isValidEmail(email)) {
            showError("Please enter a valid email address");
            emailField.requestFocus();
            return false;
        }
        
        // Phone validation
        if (!Validators.isValidPhone(phone)) {
            showError("Please enter a valid phone number (10-15 digits)");
            phoneField.requestFocus();
            return false;
        }
        
        // Password validation
        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            passwordField.requestFocus();
            return false;
        }
        
        // Password match validation
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            confirmPasswordField.requestFocus();
            return false;
        }
        
        // Terms acceptance
        if (!termsAccepted) {
            showError("Please accept the Terms of Service and Privacy Policy");
            return false;
        }
        
        return true;
    }
    
    private void updatePasswordStrength() {
        String password = new String(passwordField.getPassword());
        String strength = Validators.checkPasswordStrength(password);
        
        switch (strength) {
            case "Weak":
                passwordStrengthLabel.setForeground(Color.RED);
                passwordStrengthLabel.setText("Password strength: Weak");
                break;
            case "Medium":
                passwordStrengthLabel.setForeground(Color.ORANGE);
                passwordStrengthLabel.setText("Password strength: Medium");
                break;
            case "Strong":
                passwordStrengthLabel.setForeground(new Color(0, 150, 0));
                passwordStrengthLabel.setText("Password strength: Strong ✓");
                break;
            default:
                passwordStrengthLabel.setText("");
        }
    }
    
    // Public methods for controller to call
    public void setLoading(boolean loading) {
        this.isLoading = loading;
        signupButton.setEnabled(!loading);
        loginButton.setEnabled(!loading);
        signupButton.setText(loading ? "Creating Account..." : "Create Account");
    }
    
    public void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Signup Error",
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
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        termsCheckbox.setSelected(false);
        userTypeCombo.setSelectedIndex(0);
        passwordStrengthLabel.setText("");
        nameField.requestFocus();
    }
    
    public void setEmail(String email) {
        emailField.setText(email);
    }
}