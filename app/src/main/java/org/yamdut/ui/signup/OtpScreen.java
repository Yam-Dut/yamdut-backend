package org.yamdut.ui.signup;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.yamdut.backend.model.OtpToken;
import org.yamdut.backend.model.User;
import org.yamdut.backend.service.AuthService;
import org.yamdut.backend.service.OtpService;
import org.yamdut.backend.service.UserService;
import org.yamdut.core.ScreenManager;
import org.yamdut.ui.components.OtpField;
import org.yamdut.ui.components.PrimaryButton;
import org.yamdut.utils.Theme;
import org.yamdut.utils.UserSession;

public class OtpScreen extends JPanel {
    private final AuthService authService;
    private final UserService userService;
    private final ScreenManager screenManager;
    private final User user;
    private final boolean isSignup;
    private final String email;
    
    private JLabel titleLabel;
    private JLabel instructionLabel;
    private JLabel resendLabel;
    private OtpField otpField;
    private JButton verifyButton;
    private JButton resendButton;
    private Timer resendTimer;
    private int resendCountdown = 60;
    private boolean isVerified = false;
    
    public OtpScreen(User user, boolean isSignup, ScreenManager screenManager) {
        this.authService = new AuthService();
        this.userService = new UserService();
        this.user = user;
        this.isSignup = isSignup;
        this.email = user.getEmail();
        this.screenManager = screenManager;
        
        initUI();
        startResendTimer();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_PRIMARY);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 20, 0);
        
        // Title
        titleLabel = new JLabel(isSignup ? "Verify Your Account" : "Verify Your Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(0, 0, 10, 0);
        contentPanel.add(titleLabel, gbc);
        
        // Instruction
        instructionLabel = new JLabel("<html><center>We've sent a 6-digit verification code to:<br><b>" + 
                                     email + "</b></center></html>");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructionLabel.setForeground(Theme.TEXT_SECONDARY);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(0, 0, 30, 0);
        contentPanel.add(instructionLabel, gbc);
        
        // OTP Input
        otpField = new OtpField(6);
        contentPanel.add(otpField, gbc);
        
        // Verify Button
        PrimaryButton verifyBtn = new PrimaryButton("Verify");
        verifyButton = verifyBtn.getButton();
        verifyButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        verifyButton.setPreferredSize(new Dimension(250, 45));
        gbc.insets = new Insets(20, 0, 15, 0);
        contentPanel.add(verifyButton, gbc);
        
        // Resend Panel
        JPanel resendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        resendPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        
        JLabel resendText = new JLabel("Didn't receive the code?");
        resendText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resendText.setForeground(Theme.TEXT_SECONDARY);
        
        resendButton = new JButton("Resend");
        resendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resendButton.setForeground(Theme.COLOR_PRIMARY);
        resendButton.setBackground(Theme.BACKGROUND_PRIMARY);
        resendButton.setBorderPainted(false);
        resendButton.setFocusPainted(false);
        resendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        resendButton.setEnabled(false);
        
        resendLabel = new JLabel("(60s)");
        resendLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resendLabel.setForeground(Theme.TEXT_SECONDARY);
        
        resendPanel.add(resendText);
        resendPanel.add(resendButton);
        resendPanel.add(resendLabel);
        gbc.insets = new Insets(10, 0, 0, 0);
        contentPanel.add(resendPanel, gbc);
        
        // Back to login option (for signup)
        if (isSignup) {
            JButton backButton = new JButton("← Back to Sign Up");
            backButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            backButton.setForeground(Theme.COLOR_PRIMARY);
            backButton.setBackground(Theme.BACKGROUND_PRIMARY);
            backButton.setBorderPainted(false);
            backButton.setFocusPainted(false);
            backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            backButton.addActionListener(e -> screenManager.show("SIGNUP"));
            
            contentPanel.add(backButton, gbc);
        }
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Set up listeners
        setupListeners();
        
        // Focus on first OTP field
        SwingUtilities.invokeLater(() -> otpField.setFocus());
    }
    
    private void setupListeners() {
        verifyButton.addActionListener(e -> verifyOtp());
        
        resendButton.addActionListener(e -> resendOtp());
        
        // Auto-verify when 6 digits are entered
        otpField.addPropertyChangeListener(e -> {
            String otp = otpField.getOtp();
            if (otp.length() == 6 && !isVerified) {
                verifyOtp();
            }
        });
    }
    
    private void verifyOtp() {
        String otp = otpField.getOtp();
        
        if (otp.length() != 6) {
            JOptionPane.showMessageDialog(this, 
                "Please enter all 6 digits", 
                "Incomplete OTP", 
                JOptionPane.WARNING_MESSAGE);
            otpField.setFocus();
            return;
        }
        
        // Show loading
        verifyButton.setText("Verifying...");
        verifyButton.setEnabled(false);
        otpField.setEnabled(false);
        
        // Verify OTP
        OtpToken otptoken = new OtpToken(email, otp, 0L);
        OtpService otpService = new OtpService();
        boolean isValid = otpService.verifyOtp(email, otptoken);
        
        if (isValid) {
            isVerified = true;
            
            if (isSignup) {
                // Activate user account
                userService.activateUser(email);
            }
            
            // Store user in session
            UserSession.setUser(user);
            
            // Show success message
            JOptionPane.showMessageDialog(this,
                "Verification successful!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Navigate to appropriate dashboard
            switch (user.getRole()) {
                case DRIVER -> screenManager.show("DRIVER_DASHBOARD");
                case PASSENGER -> screenManager.show("PASSENGER_DASHBOARD");
                case ADMIN -> screenManager.show("ADMIN_DASHBOARD");
            }
            
        } else {
            // Show error
            JOptionPane.showMessageDialog(this, 
                "Invalid or expired OTP. Please try again.", 
                "Verification Failed", 
                JOptionPane.ERROR_MESSAGE);
            
            // Reset UI
            otpField.clear();
            otpField.setFocus();
            verifyButton.setText("Verify");
            verifyButton.setEnabled(true);
            otpField.setEnabled(true);
        }
    }
    
    private void resendOtp() {
        boolean sent = authService.resendOtp(email, isSignup);
        
        if (sent) {
            JOptionPane.showMessageDialog(this,
                "New OTP sent to your email.",
                "OTP Resent",
                JOptionPane.INFORMATION_MESSAGE);
            
            otpField.clear();
            otpField.setFocus();
            
            // Reset timer
            resendCountdown = 60;
            resendButton.setEnabled(false);
            resendLabel.setText("(60s)");
            startResendTimer();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to resend OTP. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void startResendTimer() {
        resendTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resendCountdown--;
                resendLabel.setText("(" + resendCountdown + "s)");
                
                if (resendCountdown <= 0) {
                    resendTimer.stop();
                    resendButton.setEnabled(true);
                    resendLabel.setText("");
                }
            }
        });
        resendTimer.start();
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible && otpField != null) {
            otpField.setFocus();
        }
    }
}