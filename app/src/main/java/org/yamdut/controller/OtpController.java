package org.yamdut.controller;

import org.yamdut.core.ScreenManager;
import org.yamdut.model.User;
import org.yamdut.service.OtpService;
import org.yamdut.service.UserService;
import org.yamdut.service.EmailService;

public class OtpController {
    private final UserService userService;
    // private final ScreenManager screenManager;
    private final OtpService otpService;
    

    public OtpController(ScreenManager screenManager) {
        this.userService = new UserService();
        // this.screenManager = screenManager;
        this.otpService = OtpService.getInstance();
    }


    public boolean verify(User user, String otp, boolean isSignup) {
        boolean valid = otpService.verifyOtp(user.getEmail(), otp);

        if (valid && isSignup) {
            userService.activateUser(user.getEmail());
            otpService.clearOtp(user.getEmail());
        }
        return valid;
    }

    public boolean resendOtp(User user) {
        String newOtp = otpService.generateOtp(user.getEmail());
        EmailService emailService = new EmailService();
        emailService.sendOtpEmail(user.getEmail(), newOtp);
        return true;
    }
}
