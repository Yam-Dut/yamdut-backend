package org.yamdut.controller;

import org.yamdut.core.ScreenManager;
import org.yamdut.ui.signup.SignUpScreen;
import org.yamdut.backend.service.UserService;

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

    public void signup(String name, String email, String password, String phone, boolean isDriver) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean,Void>() {
            private String errorMessage;
            @Override
            protected Boolean doInBackground() {
                try {
                    return userService.registerUser(
                        name, email, password, phone, isDriver
                    ); 
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
                        view.showSuccess("Account created successfully");
                        view.clearFields();
                        screenManager.show("LOGIN");
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
        worker.execute();
    }
    public void navigateToLogin() {
        screenManager.show("LOGIN");
    }
}
