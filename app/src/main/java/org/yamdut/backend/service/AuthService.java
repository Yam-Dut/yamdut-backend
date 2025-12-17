package org.yamdut.backend.service;

import javax.management.RuntimeErrorException;

import org.yamdut.backend.dao.UserDAO;
import org.yamdut.backend.dao.UserDAOImpl;
import org.yamdut.backend.model.User;
import org.yamdut.backend.utils.PasswordHasher;
import org.yamdut.backend.service.*;

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
        this.otpService = new OtpService();
        this.userDAO = new UserDAOImpl();
    }


    public void signup(String email, String rawPassword, User.Role role) {
        if (userService.exists(email)) {
            throw new RuntimeException("User already exists");
        }

        userService.createUnverifiedUser();

        String otpcode = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, otpcode);
    }

    public void verifySignup(String email, String otp) {

        boolean valid = otpService.verifyOtp(email, otp);
        if (!valid) {
            throw new RuntimeErrorException(null, "Invalid OTP");
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
        User user = userDAO.getUserByUsername(identifier);
        if (user == null) {
            return null;
        }

        boolean ok = PasswordHasher.verifyPassword(rawPassword, user.getPasswordHash());
        return ok ? user : null;
    }
}