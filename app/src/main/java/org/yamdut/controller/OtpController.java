package org.yamdut.controller;

import org.yamdut.backend.service.OtpService;
import org.yamdut.backend.service.UserService;
import org.yamdut.core.ScreenManager;

public class OtpController {
    private final UserService userService;
    private final ScreenManager screenManager;
    private final OtpService otpService;
    

    public OtpController() {
        this.userService = new UserService();
        this.screenManager = ScreenManager.getInstance();
        this.otpService = new OtpService();
    }
}
