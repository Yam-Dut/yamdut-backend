package org.yamdut.backend.config;

import java.util.Properties;
import io.github.cdimascio.dotenv.Dotenv;

public class EmailConfig {
  private static final Properties props = new Properties();
  private static final Dotenv dotenv = Dotenv.load();

  static {
    //Gmail smtp configuration
    props.put("mail.smtp.auth", true);
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
    String email = dotenv.get("EMAIL_USERNAME");
    if (email == null || email.isBlank()) {
      throw new IllegalStateException("EMAIL_USERNAME not set in .env file");
    }
    return email;
  }

  public static String getSenderPassword() {
    String password = dotenv.get("EMAIL_PASSWORD");
    if (password == null || password.isBlank()) {
      throw new IllegalStateException("EMAIL_PASSWORD not set in .env file");
    }
    return password;
  }
}
