package org.yamdut.controller;

import org.yamdut.view.dashboard.AdminDashboard;
import org.yamdut.dao.UserDAO;
import org.yamdut.dao.UserDAOImpl;
import org.yamdut.dao.DriverDAO;
import org.yamdut.dao.DriverDAOImpl;
import org.yamdut.dao.TripDAO;
import org.yamdut.dao.TripDAOImpl;
import org.yamdut.model.User;
import org.yamdut.model.Driver;
import org.yamdut.model.Trip;
import org.yamdut.model.Role;
import org.yamdut.service.AdminStatsService;
import org.yamdut.helpers.PasswordHasher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Controller wiring AdminDashboard buttons to data operations.
 * NOTE: This is a functional scaffold; many deep features (reports, exports, advanced filters)
 * are stubbed to avoid blocking UI. Expand as needed.
 */
public class AdminDashboardController {
    private final AdminDashboard view;
    private final UserDAO userDAO;
    private final DriverDAO driverDAO;
    private final TripDAO tripDAO;
    private final AdminStatsService statsService;
    private Timer refreshTimer;

    public AdminDashboardController(AdminDashboard view) {
        this.view = view;
        this.userDAO = new UserDAOImpl();
        this.driverDAO = new DriverDAOImpl();
        this.tripDAO = new TripDAOImpl();
        this.statsService = new AdminStatsService();

        initializeListeners();
        refreshDashboardData();
        startAutoRefresh();
    }

    private void initializeListeners() {
        view.getManageUsersButton().addActionListener(e -> showUserManagementDialog());
        view.getManageDriversButton().addActionListener(e -> showEnhancedDriverDialog());
        view.getViewReportsButton().addActionListener(e -> showReportsDialog());
        view.getSystemSettingsButton().addActionListener(e -> showSystemSettingsDialog());
        view.getRefreshButton().addActionListener(e -> refreshDashboardData());
    }

    private void refreshDashboardData() {
        SwingUtilities.invokeLater(() -> {
            // Fetch latest stats and update the dashboard
            int totalUsers = statsService.getTotalUsers();
            int activeDrivers = statsService.getActiveDrivers();
            int todaysRides = statsService.getTodaysTrips();
            double revenue = statsService.getTodaysRevenue();
            
            view.updateStats(totalUsers, activeDrivers, todaysRides, revenue);
            view.refreshData();
            updateActivityLog();
        });
    }

    private void startAutoRefresh() {
        stopAutoRefresh();
        refreshTimer = new Timer(30000, e -> refreshDashboardData());
        refreshTimer.start();
    }

    private void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }

    /* --------------------------- User Management --------------------------- */
    private void showUserManagementDialog() {
        JDialog dialog = buildDialog("Manage Users", 1000, 600);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<User> users = userDAO.getAllUsers();
        String[] columns = {"ID", "Full Name", "Email", "Phone", "Username", "Role", "Created"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        for (User u : users) {
            model.addRow(new Object[]{
                    u.getId(), u.getFullName(), u.getEmail(), u.getPhone(),
                    u.getUsername(), u.getRole(), u.getCreatedAt()
            });
        }
        JTable table = new JTable(model);
        table.setRowHeight(22);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("Add User");
        JButton editBtn = new JButton("Edit User");
        JButton deleteBtn = new JButton("Delete User");
        JButton viewTripsBtn = new JButton("View User Trips");
        JButton closeBtn = new JButton("Close");

        addBtn.addActionListener(e -> addNewUser(dialog, model));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) editUser(dialog, model, (int) model.getValueAt(row, 0));
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) deleteUser(dialog, model, (int) model.getValueAt(row, 0));
        });
        viewTripsBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) viewUserTrips((int) model.getValueAt(row, 0));
        });
        closeBtn.addActionListener(e -> dialog.dispose());

        buttons.add(addBtn); buttons.add(editBtn); buttons.add(deleteBtn); buttons.add(viewTripsBtn); buttons.add(closeBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addNewUser(JDialog parent, DefaultTableModel model) {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JTextField phone = new JTextField();
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JComboBox<String> role = new JComboBox<>(new String[]{"ADMIN", "DRIVER", "USER"});
        form.add(new JLabel("Full Name")); form.add(name);
        form.add(new JLabel("Email")); form.add(email);
        form.add(new JLabel("Phone")); form.add(phone);
        form.add(new JLabel("Username")); form.add(username);
        form.add(new JLabel("Password")); form.add(password);
        form.add(new JLabel("Role")); form.add(role);

        int res = JOptionPane.showConfirmDialog(parent, form, "Add User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                User u = new User(
                        name.getText(),
                        email.getText(),
                        phone.getText(),
                        username.getText(),
                        PasswordHasher.hashPassword(new String(password.getPassword())),
                        Role.valueOf(((String) role.getSelectedItem())),
                        true
                );
                if (userDAO.createUser(u)) {
                    // re-fetch to get ID
                    model.addRow(new Object[]{u.getId(), u.getFullName(), u.getEmail(), u.getPhone(), u.getUsername(), u.getRole(), u.getCreatedAt()});
                } else {
                    showNotification("Error", "Failed to create user", NotificationType.ERROR);
                }
            } catch (Exception ex) {
                handleException(ex, "add user");
            }
        }
    }

    private void editUser(JDialog parent, DefaultTableModel model, int userId) {
        // Simplified: only role/phone editable
        User existing = userDAO.getUserById(userId);
        if (existing == null) {
            showNotification("Error", "User not found", NotificationType.ERROR);
            return;
        }
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField phone = new JTextField(existing.getPhone());
        JComboBox<String> role = new JComboBox<>(new String[]{"ADMIN", "DRIVER", "USER"});
        role.setSelectedItem(existing.getRole());
        form.add(new JLabel("Phone")); form.add(phone);
        form.add(new JLabel("Role")); form.add(role);

        int res = JOptionPane.showConfirmDialog(parent, form, "Edit User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            existing.setPhone(phone.getText());
            existing.setRole(Role.valueOf((String) role.getSelectedItem()));
            if (userDAO.updateUser(existing)) {
                refreshModelRow(model, userId, existing);
            } else {
                showNotification("Error", "Failed to update user", NotificationType.ERROR);
            }
        }
    }

    private void refreshModelRow(DefaultTableModel model, int userId, User updated) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if ((int) model.getValueAt(i, 0) == userId) {
                model.setValueAt(updated.getFullName(), i, 1);
                model.setValueAt(updated.getEmail(), i, 2);
                model.setValueAt(updated.getPhone(), i, 3);
                model.setValueAt(updated.getUsername(), i, 4);
                model.setValueAt(updated.getRole(), i, 5);
                return;
            }
        }
    }

    private void deleteUser(JDialog parent, DefaultTableModel model, int userId) {
        if (!confirmAction("Delete selected user?")) return;
        if (userDAO.deleteUser(userId)) {
            for (int i = 0; i < model.getRowCount(); i++) {
                if ((int) model.getValueAt(i, 0) == userId) {
                    model.removeRow(i);
                    break;
                }
            }
        } else {
            showNotification("Error", "Failed to delete user", NotificationType.ERROR);
        }
    }

    private void viewUserTrips(int userId) {
        List<Trip> trips = tripDAO.getTripsByDriver(userId); // reuse; adjust if rider-specific DAO exists
        StringBuilder sb = new StringBuilder("Trips for user " + userId + ":\n");
        for (Trip t : trips) {
            sb.append("#").append(t.getId()).append(" ").append(t.getPickupLocation())
                    .append(" -> ").append(t.getDropLocation())
                    .append(" | Rs ").append(t.getAdjustedFare()).append("\n");
        }
        JOptionPane.showMessageDialog(view, sb.toString(), "User Trips", JOptionPane.INFORMATION_MESSAGE);
    }

    /* --------------------------- Driver Management --------------------------- */
    private void showEnhancedDriverDialog() {
        JDialog dialog = buildDialog("Drivers", 900, 600);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField searchField = new JTextField();
        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.add(new JLabel("Search"), BorderLayout.WEST);
        top.add(searchField, BorderLayout.CENTER);

        String[] cols = {"ID", "Name", "Phone", "Rating", "Total Rides", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(22);
        JScrollPane scroll = new JScrollPane(table);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton add = new JButton("Add Driver");
        JButton edit = new JButton("Edit Driver");
        JButton status = new JButton("Change Status");
        JButton stats = new JButton("View Stats");
        JButton assign = new JButton("Assign Trip");
        JButton close = new JButton("Close");
        buttons.add(add); buttons.add(edit); buttons.add(status); buttons.add(stats); buttons.add(assign); buttons.add(close);

        add.addActionListener(e -> addNewDriver(dialog, model));
        edit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) editDriver(dialog, model, (int) model.getValueAt(row, 0));
        });
        status.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) changeDriverStatus(dialog, model, (int) model.getValueAt(row, 0));
        });
        stats.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) viewDriverStats((int) model.getValueAt(row, 0));
        });
        assign.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) assignTripToDriver((int) model.getValueAt(row, 0));
        });
        close.addActionListener(e -> dialog.dispose());

        searchField.getDocument().addDocumentListener((SimpleDocumentListener) e -> searchDrivers(model, searchField.getText()));

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        dialog.add(panel);
        loadDrivers(model, null);
        dialog.setVisible(true);
    }

    private void loadDrivers(DefaultTableModel model, String query) {
        model.setRowCount(0);
        List<Driver> drivers = driverDAO.getAllDrivers();
        for (Driver d : drivers) {
            if (query == null || d.getName().toLowerCase().contains(query.toLowerCase())) {
                model.addRow(new Object[]{d.getId(), d.getName(), d.getPhone(),
                        d.getRating(), d.getTotalRides(), d.getStatus()});
            }
        }
    }

    private void addNewDriver(JDialog parent, DefaultTableModel model) {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField name = new JTextField();
        JTextField phone = new JTextField();
        JTextField rating = new JTextField("4.5");
        JTextField rides = new JTextField("0");
        JComboBox<String> status = new JComboBox<>(new String[]{"online", "offline", "suspended"});
        form.add(new JLabel("Name")); form.add(name);
        form.add(new JLabel("Phone")); form.add(phone);
        form.add(new JLabel("Rating")); form.add(rating);
        form.add(new JLabel("Total Rides")); form.add(rides);
        form.add(new JLabel("Status")); form.add(status);

        int res = JOptionPane.showConfirmDialog(parent, form, "Add Driver", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                Driver d = new Driver(name.getText(), phone.getText(),
                        Double.parseDouble(rating.getText()), Integer.parseInt(rides.getText()), status.getSelectedItem().toString());
                if (driverDAO.createDriver(d)) {
                    loadDrivers(model, null);
                } else {
                    showNotification("Error", "Failed to add driver", NotificationType.ERROR);
                }
            } catch (Exception ex) {
                handleException(ex, "add driver");
            }
        }
    }

    private void editDriver(JDialog parent, DefaultTableModel model, int driverId) {
        Driver d = driverDAO.getDriverById(driverId);
        if (d == null) {
            showNotification("Error", "Driver not found", NotificationType.ERROR);
            return;
        }
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField phone = new JTextField(d.getPhone());
        JTextField rating = new JTextField(String.valueOf(d.getRating()));
        JTextField rides = new JTextField(String.valueOf(d.getTotalRides()));
        JComboBox<String> status = new JComboBox<>(new String[]{"online", "offline", "suspended"});
        status.setSelectedItem(d.getStatus());
        form.add(new JLabel("Phone")); form.add(phone);
        form.add(new JLabel("Rating")); form.add(rating);
        form.add(new JLabel("Total Rides")); form.add(rides);
        form.add(new JLabel("Status")); form.add(status);

        int res = JOptionPane.showConfirmDialog(parent, form, "Edit Driver", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            d.setPhone(phone.getText());
            try { d.setRating(Double.parseDouble(rating.getText())); } catch (NumberFormatException ignored) {}
            try { d.setTotalRides(Integer.parseInt(rides.getText())); } catch (NumberFormatException ignored) {}
            d.setStatus(status.getSelectedItem().toString());
            if (driverDAO.updateDriver(d)) {
                loadDrivers(model, null);
            } else {
                showNotification("Error", "Failed to update driver", NotificationType.ERROR);
            }
        }
    }

    private void changeDriverStatus(JDialog parent, DefaultTableModel model, int driverId) {
        Driver d = driverDAO.getDriverById(driverId);
        if (d == null) { showNotification("Error", "Driver not found", NotificationType.ERROR); return; }
        String[] statuses = {"online", "offline", "suspended"};
        String newStatus = (String) JOptionPane.showInputDialog(parent, "Select status", "Change Status",
                JOptionPane.PLAIN_MESSAGE, null, statuses, d.getStatus());
        if (newStatus != null) {
            d.setStatus(newStatus);
            if (driverDAO.updateDriver(d)) {
                loadDrivers(model, null);
            } else {
                showNotification("Error", "Failed to update status", NotificationType.ERROR);
            }
        }
    }

    private void viewDriverStats(int driverId) {
        Driver d = driverDAO.getDriverById(driverId);
        if (d == null) { showNotification("Error", "Driver not found", NotificationType.ERROR); return; }
        String msg = String.format("Driver: %s%nRating: %.2f%nTotal Rides: %d%nStatus: %s",
                d.getName(), d.getRating(), d.getTotalRides(), d.getStatus());
        JOptionPane.showMessageDialog(view, msg, "Driver Stats", JOptionPane.INFORMATION_MESSAGE);
    }

    private void assignTripToDriver(int driverId) {
        List<Trip> pending = tripDAO.getPendingTripsByDriver(driverId);
        if (pending.isEmpty()) {
            showNotification("Info", "No pending trips for this driver.", NotificationType.INFO);
            return;
        }
        Trip t = pending.get(0);
        JOptionPane.showMessageDialog(view, "Assigning trip #" + t.getId() + " to driver " + driverId, "Assign Trip", JOptionPane.INFORMATION_MESSAGE);
        // Implement actual assignment when TripDAO supports update
    }

    private void searchDrivers(DefaultTableModel model, String query) {
        loadDrivers(model, query);
    }

    /* --------------------------- Reports --------------------------- */
    private void showReportsDialog() {
        JOptionPane.showMessageDialog(view, "Reports dialog stub. Extend with real analytics/exports.", "Reports", JOptionPane.INFORMATION_MESSAGE);
    }

    /* --------------------------- System Settings --------------------------- */
    private void showSystemSettingsDialog() {
        JOptionPane.showMessageDialog(view, "System settings stub. Wire to persisted settings store.", "Settings", JOptionPane.INFORMATION_MESSAGE);
    }

    /* --------------------------- Activity Log --------------------------- */
    private void updateActivityLog() {
        JTextArea area = view.getActivityTextArea();
        area.setText("Recent activity feature not yet implemented.\n");
    }

    /* --------------------------- Helpers --------------------------- */
    private JDialog buildDialog(String title, int w, int h) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), title, true);
        dialog.setSize(w, h);
        dialog.setLocationRelativeTo(view);
        dialog.setResizable(true);
        return dialog;
    }

    private boolean confirmAction(String message) {
        int r = JOptionPane.showConfirmDialog(view, message, "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return r == JOptionPane.YES_OPTION;
    }

    private void handleException(Exception e, String operation) {
        e.printStackTrace();
        showNotification("Error", "Error during " + operation + ": " + e.getMessage(), NotificationType.ERROR);
    }

    private void showNotification(String title, String message, NotificationType type) {
        JOptionPane.showMessageDialog(view, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public enum NotificationType { SUCCESS, ERROR, WARNING, INFO }

    // Simple document listener adapter
    private interface SimpleDocumentListener extends javax.swing.event.DocumentListener {
        void update(javax.swing.event.DocumentEvent e);
        @Override default void insertUpdate(javax.swing.event.DocumentEvent e) { update(e); }
        @Override default void removeUpdate(javax.swing.event.DocumentEvent e) { update(e); }
        @Override default void changedUpdate(javax.swing.event.DocumentEvent e) { update(e); }
    }
}
