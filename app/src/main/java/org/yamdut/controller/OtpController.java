package org.yamdut.controller;

import org.yamdut.core.ScreenManager;
import org.yamdut.model.User;
import org.yamdut.service.OtpService;
import org.yamdut.service.UserService;

public class OtpController {
    private final UserService userService;
    // private final ScreenManager screenManager;
    private final OtpService otpService;
    private final org.yamdut.service.EmailService emailService;

    public OtpController(ScreenManager screenManager) {
        this.userService = new UserService();
        // this.screenManager = screenManager;
        this.otpService = OtpService.getInstance();
        this.emailService = new org.yamdut.service.EmailService();
    }

    public boolean verify(User user, String otp, boolean isSignup) {
        boolean valid = otpService.verifyOtp(user.getEmail(), otp, org.yamdut.model.OtpPurpose.SIGNUP);

        if (valid && isSignup) {
            userService.activateUser(user.getEmail());
            otpService.clearOtp(user.getEmail(), org.yamdut.model.OtpPurpose.SIGNUP);
        }
        return valid;
    }

    public boolean resendOtp(User user) {
        String otp = otpService.generateOtp(user.getEmail(), org.yamdut.model.OtpPurpose.SIGNUP);
        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
