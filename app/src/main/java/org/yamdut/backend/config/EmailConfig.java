package org.yamdut.backend.config;

import java.util.Properties;

public class EmailConfig {
  private static final Properties props = new Properties();

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
    return System.
  }
}
