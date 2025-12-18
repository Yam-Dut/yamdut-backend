package org.yamdut.controller;

import org.yamdut.backend.service.OtpService;
import org.yamdut.backend.service.UserService;
import org.yamdut.core.ScreenManager;
import org.yamdut.utils.UserSession;
import org.yamdut.backend.model.User;

public class OtpController {
    private final UserService userService;
    private final ScreenManager screenManager;
    private final OtpService otpService;
    

    public OtpController(ScreenManager screenManager) {
        this.userService = new UserService();
        this.screenManager = screenManager;
        this.otpService = OtpService.getInstance();
    }


    public boolean verify(User user, String otp, boolean isSignup) {
        boolean valid = otpService.verifyOtp(user.getEmail(), otp);

        if (valid) {
            if (isSignup) {
                userService.activateUser(user.getEmail());
            }
            UserSession.getInstance().login(user);

            screenManager.showDashBoardForRole(user.getRole());
        }
        return valid;
    }

    public boolean resendOtp(User user) {
        return otpService.resendOtp(user.getEmail());
    }
}
