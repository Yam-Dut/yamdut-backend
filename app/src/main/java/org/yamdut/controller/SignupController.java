package org.yamdut.controller;

import org.yamdut.core.ScreenManager;
import org.yamdut.view.signup.SignUpScreen;
import org.yamdut.backend.service.EmailService;
import org.yamdut.backend.service.OtpService;
import org.yamdut.backend.service.UserService;
import org.yamdut.backend.model.*;

import javax.swing.*;

public class SignupController {
    private final ScreenManager screenManager;
    private final UserService userService;
    private final SignUpScreen view;

    public SignupController (SignUpScreen view, ScreenManager screenManager) {
        this.view = view;
        this.screenManager = screenManager;
        this.userService = new UserService();
    }

    public void signup(String fullName, String email, String password, String phone, boolean isDriver) {
        SwingWorker<User, Void> worker = new SwingWorker<User,Void>() {
            private String errorMessage;

            @Override
            protected User doInBackground() {
                try {
                    User user = userService.registerBasicUser(
                        fullName,
                        email, 
                        password,
                        phone,
                        isDriver
                    );
                    
                    if (user == null) {
                        errorMessage = "Account with this email already exists";
                        return null;
                    }
                    
                    OtpService otpService = OtpService.getInstance();
                    String otp = otpService.generateOtp(email);

                    EmailService emailService = new EmailService();
                    emailService.sendOtpEmail(email, otp);

                    return user;

                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void done() {
                view.setLoading(false);
                try {
                    User user = get();
                    if (user != null) {
                        screenManager.showOtpScreen(user, true);
                    } else {
                        view.showError(
                            errorMessage != null
                                ? errorMessage
                                : "Signup Failed"
                        );
                    }
                } catch (Exception e) {
                    view.showError("Unexpected error occured");
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
