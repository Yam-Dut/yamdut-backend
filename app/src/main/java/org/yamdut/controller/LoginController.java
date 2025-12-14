package org.yamdut.controller;

import org.yamdut.ui.login.LoginScreen;
import org.yamdut.backend.service.AuthService;

import javax.swing.*;

public class LoginController {
    private final AuthService authService;
    private final LoginScreen view;

    public LoginController(LoginScreen view) {
        this.view = view;
        this.authService = new AuthService();
    }
}