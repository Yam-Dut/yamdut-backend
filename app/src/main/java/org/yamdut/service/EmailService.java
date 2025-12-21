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

    /*
    Send otp email (SignUp / Login verification)
     */
    public void sendOtpEmail(String toEmail, String otp) throws MessagingException {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(EmailConfig.getSenderEmail()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Yamdut - Email Verification OTP");
            message.setContent(buildOtpHtml(otp), "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("OTP email sent to " + toEmail);
    } 

    private String buildOtpHtml(String otp) {
        return """
        <div style="font-family: Arial, sans-serif; padding: 20px;">
                <h2 style="color:#2E86C1;">YamDut Email Verification</h2>
                <p>Your One-Time Password (OTP) is:</p>
                <h1 style="letter-spacing: 4px;">%s</h1>
                <p>This OTP is valid for <b>5 minutes</b>.</p>
                <p>If you did not request this, please ignore this email.</p>
                <hr/>
                <small>© YamDut</small>
            </div>
        """.formatted(otp);
    }

}

