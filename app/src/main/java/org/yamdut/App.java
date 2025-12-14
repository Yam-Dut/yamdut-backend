package org.yamdut;

import javax.swing.*;

import org.yamdut.core.ScreenManager;
import org.yamdut.ui.signup.SignUpScreen;
import org.yamdut.ui.login.LoginScreen;
import org.yamdut.controller.LoginController;
import org.yamdut.controller.SignupController;



public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Yamdut");
            frame.setSize(420, 720);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            ScreenManager screenManager = new ScreenManager(frame);

            LoginScreen loginScreen = new LoginScreen(null);
            SignUpScreen signupScreen = new SignUpScreen(null);

            //LoginController loginController = new LoginController(loginScreen, screenManager);

            SignupController signupController = new SignupController(signupScreen, screenManager);

            //loginScreen = new LoginScreen(loginController);
            signupScreen = new SignUpScreen(signupController);

            screenManager.register("LOGIN", loginScreen);
            screenManager.register("SIGNUP", signupScreen);

            screenManager.show("LOGIN");
            frame.setVisible(true);
        });
    }
}