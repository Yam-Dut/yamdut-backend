package org.yamdut.core;


import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.yamdut.controller.DriverDashboardController;
import org.yamdut.controller.PassengerDashboardController;
import org.yamdut.controller.AdminDashboardController;

import org.yamdut.model.Role;
import org.yamdut.model.User;
import org.yamdut.view.dashboard.AdminDashboard;
import org.yamdut.view.dashboard.DriverDashboard;
import org.yamdut.view.dashboard.PassengerDashboard;
import org.yamdut.view.signup.OtpScreen;

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
            case PASSENGER:
                PassengerDashboard passengerDashboard = new PassengerDashboard();
                new PassengerDashboardController(passengerDashboard); // controller instance
                this.register("PASSENGER_DASHBOARD", passengerDashboard);
                this.show("PASSENGER_DASHBOARD");
                break;

            case DRIVER:
                DriverDashboard driverDashboard = new DriverDashboard();
                new DriverDashboardController(driverDashboard);
                this.register("DRIVER_DASHBOARD", driverDashboard);
                this.show("DRIVER_DASHBOARD");
                break;

            case ADMIN:
                AdminDashboard adminDashboard = new AdminDashboard();
                AdminDashboardController adminController = new AdminDashboardController(adminDashboard);
                this.register("ADMIN_DASHBOARD", adminDashboard);
                this.show("ADMIN_DASHBOARD");
                break;

            default:
                throw new IllegalStateException("Unknown role: " + role);
        }
    }
    public void showOtpScreen(User user, boolean isSignup) {
        OtpScreen otpScreen = new OtpScreen(user, isSignup, this);
        screens.put("OTP", otpScreen);

        show("OTP");

    };
}
