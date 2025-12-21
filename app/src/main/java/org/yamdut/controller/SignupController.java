package org.yamdut.controller;

import org.yamdut.core.ScreenManager;
import org.yamdut.model.*;
import org.yamdut.service.EmailService;
import org.yamdut.service.OtpService;
import org.yamdut.service.UserService;
import org.yamdut.view.signup.SignUpScreen;

import jakarta.mail.MessagingException;

import javax.swing.*;

public class SignupController {
    private final ScreenManager screenManager;
    private final UserService userService;
    private final SignUpScreen view;
    private final OtpService otpService;
    private final EmailService emailService;

    public SignupController (SignUpScreen view, ScreenManager screenManager) {
        this.view = view;
        this.screenManager = screenManager;
        this.userService = new UserService();
        this.otpService = OtpService.getInstance();
        this.emailService = new EmailService();
    }

    public void signup(String fullName, String email, String password, String phone, boolean isDriver) {
        SwingWorker<User, Void> worker = new SwingWorker<User,Void>() {
            private String errorMessage;

            @Override
            protected User doInBackground() {
                try {
                    User existing = userService.findByEmail(email);
                    
                    if (existing != null && existing.getVerified()) {
                        errorMessage = "Account with this email already exists.";
                        return null;
                    }
                    User savedUser = userService.registerBasicUser(fullName, email, password, phone, isDriver);

                    //generate otp
                    String otp = otpService.generateOtp(email);
                    //send otp to the email;
                    emailService.sendOtpEmail(email, otp);

                    return savedUser;
                } catch (MessagingException me) {
                    me.printStackTrace();
                    errorMessage = "Failed to send OTP email. Please check your email address";
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMessage = "Unexpected error occured: " + e.getMessage();
                    return null;
                }
            }

            @Override
            protected void done() {
                view.setLoading(false);
                try {
                    User user = get();
                    if (user != null && errorMessage == null) {
                       screenManager.showOtpScreen(user, true); 
                    } else {
                        view.showError(errorMessage != null ? errorMessage : "Signup Failed!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    view.showError("Unexpected error: " + e.getMessage());
                }
            }
        };
        view.setLoading(true);
        worker.execute();
    }

    public void navigateToLogin() {
        screenManager.show("LOGIN");
    }
}
