package org.yamdut.model;

public class OtpToken {
    private String email;
    private String otp;
    private long expiresAt;

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    public OtpToken() {}

    public OtpToken(String email, String otp, long expiresAt) {
        this.email = email;
        this.otp = otp;
        this.expiresAt = expiresAt;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }
    public void setOtp(String otp) {
        this.otp = otp;
    }
    public long getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }
}
