package org.yamdut.utils;

import java.util.regex.Pattern;


public class Validators {


    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\d{10,15}$"
    );


    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }


    /**
     * Login only needs non-empty password
     */
    public static boolean isValidPasswordForLogin(String password) {
        return password != null && !password.isEmpty();
    }


    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;

        String cleaned = phone.replaceAll("[\\s\\-()]", "");
        return PHONE_PATTERN.matcher(cleaned).matches();
    }

    public static boolean isValidName(String name) {
        if (name == null) return false;
        return name.trim().length() >= 2;
    }


    /**
     * Used in Signup screen
     * Returns: "Weak", "Medium", "Strong"
     */
    public static String checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return "Weak";
        }

        int score = 0;

        if (password.length() >= 8) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[@#$%^&+=!].*")) score++;

        if (score >= 4) return "Strong";
        if (score >= 2) return "Medium";
        return "Weak";
    }
}