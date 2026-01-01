package org.yamdut.controller;

import org.yamdut.view.dashboard.AdminDashboard;
import org.yamdut.dao.UserDAO;
import org.yamdut.dao.UserDAOImpl;
import org.yamdut.model.User;
import org.yamdut.service.AdminStatsService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Controller wiring AdminDashboard buttons to data operations.
 */
public class AdminDashboardController {
    private final AdminDashboard view;
    private final UserDAO userDAO;
    private final AdminStatsService statsService;
    private Timer refreshTimer;

    public AdminDashboardController(AdminDashboard view) {
        this.view = view;
        this.userDAO = new UserDAOImpl();
        this.statsService = new AdminStatsService();

        initializeListeners();
        refreshDashboardData();
        loadAllUsers();
        startAutoRefresh();
    }

    private void initializeListeners() {
        view.getManageUsersButton().addActionListener(e -> showUserManagementDialog());
        view.getManageDriversButton().addActionListener(e -> showStubDialog("Manage Drivers"));
        view.getViewReportsButton().addActionListener(e -> showStubDialog("View Reports"));
        view.getSystemSettingsButton().addActionListener(e -> showStubDialog("System Settings"));
        view.getRefreshButton().addActionListener(e -> {
            refreshDashboardData();
            loadAllUsers();
        });
    }

    private void refreshDashboardData() {
        SwingUtilities.invokeLater(() -> {
            int totalUsers = statsService.getTotalUsers();
            int activeDrivers = statsService.getActiveDrivers();
            int todaysRides = statsService.getTodaysTrips();
            double revenue = statsService.getTodaysRevenue();

            view.updateStats(totalUsers, activeDrivers, todaysRides, revenue);
            view.refreshData();
            updateActivityLog();
        });
    }

    private void loadAllUsers() {
        DefaultTableModel model = view.getUsersTableModel();
        if (model == null)
            return;

        model.setRowCount(0);
        List<User> users = userDAO.getAllUsers();
        for (User u : users) {
            model.addRow(new Object[] {
                    u.getId(),
                    u.getFullName(),
                    u.getEmail(),
                    u.getUsername(),
                    u.getRole(),
                    u.getVerified(),
                    u.getCreatedAt()
            });
        }
    }

    private void startAutoRefresh() {
        stopAutoRefresh();
        refreshTimer = new Timer(30000, e -> {
            refreshDashboardData();
            loadAllUsers();
        });
        refreshTimer.start();
    }

    private void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }

    private void showUserManagementDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), "Manage Users", true);
        dialog.setSize(900, 500);
        dialog.setLocationRelativeTo(view);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<User> users = userDAO.getAllUsers();
        String[] columns = { "ID", "Full Name", "Email", "Username", "Role", "Verified", "Created" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (User u : users) {
            model.addRow(new Object[] {
                    u.getId(), u.getFullName(), u.getEmail(),
                    u.getUsername(), u.getRole(), u.getVerified(), u.getCreatedAt()
            });
        }
        JTable table = new JTable(model);
        table.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        buttons.add(closeBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showStubDialog(String feature) {
        JOptionPane.showMessageDialog(view,
                feature + " is not yet implemented.",
                feature,
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateActivityLog() {
        JTextArea area = view.getActivityTextArea();
        if (area != null) {
            area.setText("Dashboard refreshed at " + new java.util.Date());
        }
    }
}
