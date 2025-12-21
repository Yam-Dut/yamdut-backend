package org.yamdut.config;

import java.util.Properties;

// Gmail config for OTP. Use environment variables to avoid requiring external libs.
public class EmailConfig {
  private static final Properties props = new Properties();

  static {
    //Gmail smtp configuration
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");
    props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

    props.put("mail.smtp.timeout", "5000");
    props.put("mail.smtp.connectionTimeout", "5000");
  }

  public static Properties getProperties() {
    return props;
  }

  public static String getSenderEmail() {
    String email = System.getenv("EMAIL_USERNAME");
    if (email == null || email.isBlank()) {
      throw new IllegalStateException("EMAIL_USERNAME not set in environment variables");
    }
    return email;
  }

  public static String getSenderPassword() {
    String password = System.getenv("EMAIL_PASSWORD");
    if (password == null || password.isBlank()) {
      throw new IllegalStateException("EMAIL_PASSWORD not set in environment variables");
    }
    return password;
  }
}
