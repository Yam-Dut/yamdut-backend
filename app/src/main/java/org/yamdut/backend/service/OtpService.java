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

    public boolean verifyOtp(String email, String enteredOtp) {
        OtpToken token = otpStore.get(email);

        if (token == null) {
            return false;
        }
        if (token.isExpired()) {
            return false;
        }

        boolean valid = token.getOtp().equals(enteredOtp);

        if (valid) {
           otpStore.remove(email); 
        }
        return valid;
    }
    public boolean resendOtp(String email) {
        generateOtp(email);
        return true;
    }
}
