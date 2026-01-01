package org.yamdut.service;

import java.util.*;

import org.yamdut.model.OtpToken;

public class OtpService {
    private static final OtpService instance = new OtpService();
    private static final Map<String, OtpToken> otpStore = new HashMap<>();
    private final java.security.SecureRandom random = new java.security.SecureRandom();

    private OtpService() {
    }

    public static OtpService getInstance() {
        return instance;
    }

    private String key(String email, org.yamdut.model.OtpPurpose purpose) {
        return email + ":" + purpose.name();
    }

    public String generateOtp(String email, org.yamdut.model.OtpPurpose purpose) {
        String otp = String.valueOf(100000 + random.nextInt(900000));

        OtpToken token = new OtpToken();
        token.setEmail(email);
        token.setOtp(otp);
        token.setPurpose(purpose);
        token.setExpiresAt(System.currentTimeMillis() + (5 * 60 * 1000));

        otpStore.put(key(email, purpose), token);
        return otp;
    }

    public boolean verifyOtp(String email, String enteredOtp, org.yamdut.model.OtpPurpose purpose) {
        String k = key(email, purpose);
        OtpToken token = otpStore.get(k);

        if (token == null || token.isExpired()) {
            otpStore.remove(k);
            return false;
        }

        boolean valid = token.getOtp().equals(enteredOtp);
        if (valid) {
            otpStore.remove(k);
        }
        return valid;
    }

    public void clearOtp(String email, org.yamdut.model.OtpPurpose purpose) {
        otpStore.remove(key(email, purpose));
    }
}
