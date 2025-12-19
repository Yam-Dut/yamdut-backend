package org.yamdut.service;

import java.util.*;

import org.yamdut.model.OtpToken;

public class OtpService {
    private static final OtpService instance = new OtpService();
    private static final Map<String, OtpToken> otpStore = new HashMap<>();

    private OtpService() {}

    public static OtpService getInstance() {
        return instance;
    }

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

        if (token == null || token.isExpired()) {
            otpStore.remove(email);
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

    public void clearOtp(String email) {
        otpStore.remove(email);
    }
}
