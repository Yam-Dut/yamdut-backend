package org.yamdut.controller;

import org.yamdut.core.ScreenManager;
import org.yamdut.ui.signup.SignUpScreen;
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
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean,Void>() {
            private String errorMessage;
            @Override
            protected Boolean doInBackground() {
                try {
                    boolean created = userService.registerBasicUser(fullName, email, password, phone, isDriver);
                    if (!created) {
                        errorMessage = "Account with this email already exists";
                        return false;
                    }

                    OtpService otpService = new OtpService();
                    String otp = otpService.generateOtp(email);

                    EmailService emailService = new EmailService();
                    emailService.sendOtpEmail(email, otp);

                    return true;

                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    return false;
                }
            }

            @Override
            protected void done() {
                view.setLoading(false);
                try {
                    if (get()) {
                        view.showSuccess("Account created Successfully, Please verify Otp sent to your email.");
                        view.clearFields();

                        User newUser = userService.getUserByEmail(email);

                        screenManager.showOtpScreen(newUser, true);
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
