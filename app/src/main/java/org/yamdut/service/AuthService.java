package org.yamdut.service;

import java.nio.channels.IllegalSelectorException;

import javax.management.RuntimeErrorException;

import org.yamdut.dao.UserDAO;
import org.yamdut.dao.UserDAOImpl;
import org.yamdut.helpers.PasswordHasher;
import org.yamdut.model.*;

import jakarta.mail.MessagingException;

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
    private final EmailService emailService;
    private final OtpService otpService;

    public AuthService() {
        this.userService = new UserService();
        this.emailService = new EmailService();
        this.otpService = OtpService.getInstance();
        this.userDAO = new UserDAOImpl();
    }


    public void signup(String email, String rawPassword, Role role) {
        if (userService.exists(email)) {
            throw new RuntimeException("User already exists");
        }
        String passwordHash = PasswordHasher.hashPassword(rawPassword);
        String fullName = email.split("@")[0];
        String phone = "";
        userService.createUnverifiedUser(fullName, email, phone, passwordHash, role);

        String otpcode = otpService.generateOtp(email);
        try {
            emailService.sendOtpEmail(email, otpcode);
        } catch (MessagingException e) {
            throw new IllegalStateException("Invalid email address or email service failure", e);
        }
    }

    public void verifySignup(String email, String otp) {

        boolean valid = otpService.verifyOtp(email, otp);
        if (!valid) {
            throw new IllegalStateException("Invalid OTP");
        }
        userService.activateUser(email);
    }
    /**
     * Attempts to authenticate a user.
     *
     * @param identifier username/email used at signup (we use email as username)
     * @param rawPassword plain text password from the login form
     * @return the authenticated {@link User} (including role) or {@code null} if auth fails
     */
    public User login(String identifier, String rawPassword) {
        User user = userDAO.getUserByEmail(identifier);
        if (user == null) {
            return null;
        }

        boolean ok = PasswordHasher.verifyPassword(rawPassword, user.getPasswordHash());
        if (!ok) return null;
        if (!user.getVerified()) {
            throw new IllegalStateException("Please verify your account first.");
        }
        return user;
    }
}