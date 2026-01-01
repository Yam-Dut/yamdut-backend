package org.yamdut.service;

import org.yamdut.dao.UserDAO;
import org.yamdut.dao.UserDAOImpl;
import org.yamdut.helpers.PasswordHasher;
import org.yamdut.model.*;

/**
 * Authentication service responsible for validating credentials and
 * returning the authenticated user with their role (DRIVER/PASSENGER/ADMIN).
 *
 * The UI layer (single login screen) does not need to know about roles
 * up front – it just passes identifier + password and
 * this service decides who the user is and what their role is.
 */
public class AuthService {

    private final UserService userService;
    private final UserDAO userDAO;
    private final OtpService otpService;
    private final EmailService emailService;

    public AuthService() {
        this.userService = new UserService();
        this.otpService = OtpService.getInstance();
        this.userDAO = new UserDAOImpl();
        this.emailService = new EmailService();
    }

    public void verifySignup(String email, String otp) {

        boolean valid = otpService.verifyOtp(email, otp, org.yamdut.model.OtpPurpose.SIGNUP);
        if (!valid) {
            throw new IllegalStateException("Invalid OTP");
        }
        userService.activateUser(email);
    }

    /**
     * Attempts to authenticate a user.
     *
     * @param identifier  username/email used at signup (we use email as username)
     * @param rawPassword plain text password from the login form
     * @return the authenticated {@link User} (including role) or {@code null} if
     *         auth fails
     */
    public User login(String identifier, String rawPassword) {
        User user = userDAO.findByLogin(identifier);
        if (user == null) {
            return null;
        }

        boolean ok = PasswordHasher.verifyPassword(rawPassword, user.getPasswordHash());
        if (!ok)
            return null;
        if (!user.getVerified()) {
            throw new IllegalStateException("Please verify your account first.");
        }
        return user;
    }

    public void requestPasswordReset(String email) {
        if (!userDAO.existsByEmail(email)) {
            // For security, we might not want to reveal if email exists,
            // but for this app's UX we can either return silently or throw.
            // The user prompt said "do not leak existence" but the code example had "if
            // user == null return".
            // So I will return silently.
            return;
        }

        String otp = otpService.generateOtp(email, OtpPurpose.PASSWORD_RESET);
        try {
            emailService.sendOtpEmail(email, otp);
        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    public void resetPassword(String email, String otp, String newPassword) {
        boolean valid = otpService.verifyOtp(email, otp, OtpPurpose.PASSWORD_RESET);
        if (!valid) {
            throw new IllegalStateException("Invalid or expired OTP");
        }

        String hash = PasswordHasher.hashPassword(newPassword);
        userDAO.updatePassword(email, hash);
    }
}