package org.yamdut.core;


import org.yamdut.backend.model.*;
import org.yamdut.view.signup.*;

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
            case ADMIN -> show("ADMIN_DASHBOARD");
            default -> throw new IllegalStateException("Unknow role: " + role);
        }
    }
    public void showOtpScreen(User user, boolean isSignup) {
        OtpScreen otpScreen = new OtpScreen(user, isSignup, this);
        screens.put("OTP", otpScreen);

        show("OTP");

    };
}
