package org.yamdut.service;

import java.util.Properties;

import org.yamdut.config.EmailConfig;

import jakarta.mail.*;
import jakarta.mail.internet.*;

public class EmailService {

    private final Session session;

    public EmailService() {
        Properties props = EmailConfig.getProperties(); 

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailConfig.getSenderEmail(), EmailConfig.getSenderPassword());
            }
        });
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

