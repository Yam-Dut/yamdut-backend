package org.yamdut.service;

import org.yamdut.dao.DriverDAO;
import org.yamdut.dao.DriverDAOImpl;
import org.yamdut.dao.PassengerDAO;
import org.yamdut.dao.PassengerDAOImpl;
import org.yamdut.dao.UserDAO;
import org.yamdut.dao.UserDAOImpl;
import org.yamdut.helpers.PasswordHasher;
import org.yamdut.model.Driver;
import org.yamdut.model.Passenger;
import org.yamdut.model.Role;
import org.yamdut.model.User;

public class UserService {

    private final UserDAO userDAO;
    private final DriverDAO driverDAO;
    private final PassengerDAO passengerDAO;

    public UserService() {
        this.userDAO = new UserDAOImpl();
        this.driverDAO = new DriverDAOImpl();
        this.passengerDAO = new PassengerDAOImpl();
    }

    
    public boolean exists(String email) {
        return userDAO.getUserByEmail(email) != null;
    }

    public void createUnverifiedUser(String fullname, String email, String phone, String passwordHash, Role role) {
        User user = new User(
                fullname,
                email,
                phone,
                email, // userrname
                passwordHash,
                role,
                false);
        userDAO.save(user);

        // If it's a driver, create driver record too (even if unverified)
        if (role == Role.DRIVER) {
            createDriverRecord(user);
        } else if (role == Role.PASSENGER) {
            createPassengerRecord(user);
        }
    }

    public void activateUser(String email) {
        userDAO.markVerified(email);
    }

    /**
     * Registers a new user as either DRIVER or PASSENGER.
     * The same account can later log in from the single login screen
     * and the backend will decide behaviour based on this role.
     */
    public User registerUser(String fullName,
            String email,
            String rawPassword,
            String phone,
            boolean isDriver) {

        // Use email as the unique username/identifier for login
        User existing = userDAO.getUserByEmail(email);
        if (existing != null) {
            throw new IllegalStateException("An account with this email already exists");
        }

        String passwordHash = PasswordHasher.hashPassword(rawPassword);
        Role role = isDriver ? Role.DRIVER : Role.PASSENGER;

        boolean verified = false;
        User user = new User(fullName, email, phone, email, passwordHash, role, verified);
        userDAO.save(user);

        if (isDriver) {
            createDriverRecord(user);
        } else {
            createPassengerRecord(user);
        }

        return user;
    }

    /**
     * helper method to register a user without full details(simpler version)
     */
    public User registerBasicUser(String fullName, String email, String rawPassword, String phone, boolean isDriver) {
        if (userDAO.existsByEmail(email)) {
            throw new IllegalStateException("Account with this email already exists.");
        }

        String passwordHash = PasswordHasher.hashPassword(rawPassword);
        Role role = isDriver ? Role.DRIVER : Role.PASSENGER;

        String username = email.split("@")[0];

        User user = new User(
                fullName,
                email,
                phone,
                username,
                passwordHash,
                role,
                false);

        userDAO.save(user);

        if (isDriver) {
            createDriverRecord(user);
        } else {
            createPassengerRecord(user);
        }

        return user;
    }

    private void createDriverRecord(User user) {
        // Only create if not exists
        if (driverDAO.getDriverByUserId(user.getId()) == null) {
            Driver driver = new Driver(
                    user.getId(),
                    user.getFullName(),
                    user.getPhone() != null ? user.getPhone() : "000-000-0000",
                    "Bike", // Default
                    "NOT-SET", // Default
                    "OFFLINE");
            driverDAO.createDriver(driver);
        }
    }

    private void createPassengerRecord(User user) {
        if (passengerDAO.getPassengerByUserId(user.getId()) == null) {
            Passenger passenger = new Passenger(
                    user.getId(),
                    user.getFullName(),
                    user.getPhone() != null ? user.getPhone() : "000-000-0000");
            passengerDAO.createPassenger(passenger);
        }
    }

    public User authenticate(String email, String rawPassword) {
        User user = userDAO.getUserByEmail(email);
        if (user == null)
            return null;
        if (!PasswordHasher.verifyPassword(rawPassword, user.getPasswordHash())) {
            return null;
        }
        if (!user.getVerified()) {
            throw new IllegalStateException("Please verify your account first");
        }
        return user;
    }

    public User findByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }
}