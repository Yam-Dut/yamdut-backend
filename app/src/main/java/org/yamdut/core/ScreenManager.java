package org.yamdut.core;

import org.yamdut.ui.signup.*;
import org.yamdut.backend.service.AuthService;
import org.yamdut.backend.model.User;


import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ScreenManager {
    private final JFrame frame;
    private final Map<String, JPanel> screens = new HashMap<>();
    
    public ScreenManager(JFrame frame) {
        this.frame = frame;
    }

    public void register(String name, JPanel panel) {
        screens.put(name, panel);
    }

    public void show(String name) {
        JPanel panel = screens.get(name);
        if (panel == null) {
            throw new IllegalArgumentException("Screen not found: " + name);
        }
        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }
    public void showOtpScreen(User user, boolean isSignup) {
        OtpScreen otpScreen = new OtpScreen(user, isSignup, this);
        
        // Generate and send OTP
        AuthService authService = new AuthService();
        String otp = authService.generateAndSendOtp(
            user.getEmail(), 
            isSignup ? "signup" : "login"
        );
        
        // Store the OTP in service
        // (Your AuthService should handle this)
        
        // Show OTP screen
        String screenName = "OTP_" + user.getEmail();
        screens.put(screenName, otpScreen);
        mainPanel.add(otpScreen, screenName);
        show(screenName);
    }
}
