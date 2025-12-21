package org.yamdut.service;

import org.yamdut.config.EmailConfig;

/**
 * Lightweight no-op EmailService used during local builds when Jakarta Mail is
 * not available. This avoids adding the mail dependency while keeping the
 * service API stable; it prints the OTP to stdout instead of sending email.
 */
public class EmailService {

    public EmailService() {
        // no-op
    }

    public void sendOtpEmail(String toEmail, String otp) {
        System.out.println("[EmailService] OTP for " + toEmail + " => " + otp);
    }

}

