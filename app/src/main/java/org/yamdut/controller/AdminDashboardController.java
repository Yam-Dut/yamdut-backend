package org.yamdut.controller;

import java.util.List;

import org.yamdut.backend.dao.*;
import org.yamdut.backend.dao.model.Driver;
import org.yamdut.backend.dao.model.Ride;
import org.yamdut.backend.dao.model.Transaction;
import org.yamdut.backend.dao.model.User;
import org.yamdut.view.AdminDashboardView;
import org.yamdut.view.LoginView;

public class AdminDashboardController {
    private AdminDashboardView view;
    private DriverDAO driverDAO;
    private RideDAO rideDAO;
    private UserDAO userDAO;
    private TransactionDAO transactionDAO;

    public AdminDashboardController(AdminDashboardView view) {
        this.view = view;
        this.driverDAO = new DriverDAOImpl();
        this.rideDAO = new RideDAOImpl();
        this.userDAO = new UserDAOImpl();
        this.transactionDAO = new TransactionDAOImpl();

        view.addLogoutListener(e -> handleLogout());

        loadDashboardData();
    }

    private void loadDashboardData() {
        try {
            List<Driver> drivers = driverDAO.getAllDrivers();
            List<Ride> rides = rideDAO.getAllRides();
            List<User> users = userDAO.getAllUsers();
            List<Transaction> transactions = transactionDAO.getAllTransactions();

            view.updateStatCard("activeRides", String.valueOf(rides.size()));
            view.updateStatCard("totalDrivers", String.valueOf(drivers.size()));
            view.updateStatCard("totalUsers", String.valueOf(users.size()));

            double totalRevenue = transactions.stream()
                    .filter(t -> "COMPLETED".equals(t.getStatus()))
                    .mapToDouble(Transaction::getAmount)
                    .sum();
            view.updateStatCard("revenue", "Rs " + String.format("%.2f", totalRevenue));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleLogout() {
        view.dispose();
        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }
}
