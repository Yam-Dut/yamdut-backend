package org.yamdut.backend.service;

import org.yamdut.backend.model.OtpToken;

import java.util.*;

public class OtpService {
    private static final Map<String, OtpToken> otpStore = new HashMap<>();

    public String generateOtp(String email) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        OtpToken token = new OtpToken();
        token.setEmail(email);
        token.setOtp(otp);
        token.setExpiresAt(System.currentTimeMillis() + (5 * 60 * 1000));

        otpStore.put(email, token);
        return otp;
    }

    public boolean verifyOtp(String email, OtpToken otp) {
        return otp.equals(otpStore.get(email));
    }

    public void clearOtp(String email) {
        otpStore.remove(email);
    }
}
