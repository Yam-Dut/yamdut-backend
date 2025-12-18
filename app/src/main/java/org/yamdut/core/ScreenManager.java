package org.yamdut.core;


import org.yamdut.backend.model.Role;

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

    //centralized role based navigation
    public void showDashBoardForRole(Role role) {
        switch (role) {
            case PASSENGER -> show("PASSENGER_DASHBOARD");
            case DRIVER -> show("DRIVER_DASHBOARD");
            case ADMIN -> show("ADMIN DASHBOARD");
            default -> throw new IllegalStateException("Unknow role: " + role);
        }
    }
    // public void showOtpScreen(User user, boolean isSignup) {
    //     String email = user.getEmail();

    //     OtpService otpService = new OtpService();
    //     String otp = otpService.generateOtp(email);

    //     EmailService emailService = new EmailService();
    //     emailService.sendOtpEmail(email, otp);

    //     OtpScreen otpScreen = new OtpScreen(user, isSignup, this);

    //     String screenKey = "OTP_" + email;
    //     screens.put(screenKey, otpScreen);

    //     show(screenKey);

    // };
}
