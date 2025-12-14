package org.example.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;

public class AdminDashboardView extends BaseView {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton logoutButton;

    public AdminDashboardView() {
        super("YamDut - Admin Dashboard");
        setSize(1300, 850);
        setLocationRelativeTo(null);
        initializeComponents();
    }

    @Override
    public void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_SILVER);

        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BACKGROUND_SILVER);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        contentPanel.add(createOverviewPanel(), "overview");
        contentPanel.add(createActiveRidesPanel(), "rides");
        contentPanel.add(createDriversPanel(), "drivers");
        contentPanel.add(createUsersPanel(), "users");
        contentPanel.add(createRevenuePanel(), "revenue");

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_DARK);
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));

        JLabel titleLabel = new JLabel("ADMIN PANEL");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(titleLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        sidebar.add(createMenuButton("Overview", "overview"));
        sidebar.add(createMenuButton("Active Rides", "rides"));
        sidebar.add(createMenuButton("Drivers", "drivers"));
        sidebar.add(createMenuButton("Users", "users"));
        sidebar.add(createMenuButton("Revenue", "revenue"));

        sidebar.add(Box.createVerticalGlue());

        logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(210, 45));
        logoutButton.setBackground(ACCENT_RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 13));
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sidebar.add(logoutButton);

        return sidebar;
    }

    private JButton createMenuButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(220, 50));
        button.setBackground(SIDEBAR_DARK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        return button;
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 25));
        panel.setBackground(BACKGROUND_SILVER);

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(TEXT_DARK);
        panel.add(title, BorderLayout.NORTH);

        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 25, 25));
        statsGrid.setBackground(BACKGROUND_SILVER);

        statsGrid.add(createStatCard("Active Rides", "47"));
        statsGrid.add(createStatCard("Total Drivers", "5"));
        statsGrid.add(createStatCard("Total Users", "5"));
        statsGrid.add(createStatCard("Today's Revenue", "Rs 12,847"));

        panel.add(statsGrid, BorderLayout.CENTER);
        panel.add(createRecentActivityPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createStatCard(String label, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(PANEL_WHITE);
        card.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 40));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        labelLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(labelLabel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createRecentActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_WHITE);
        panel.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));

        JLabel title = new JLabel("Recent Activity");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(PANEL_WHITE);

        list.add(activity("New ride started", "User 1"));
        list.add(activity("Driver completed a ride", "Driver 1"));
        list.add(activity("New user registered", "User 2"));
        list.add(activity("Ride cancelled", "User 3"));
        list.add(activity("Payment received", "User 4"));

        panel.add(list, BorderLayout.CENTER);
        return panel;
    }

    private JPanel activity(String event, String person) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(PANEL_WHITE);
        item.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_GRAY));

        JLabel eventLabel = new JLabel(event);
        eventLabel.setFont(new Font("Arial", Font.BOLD, 15));

        JLabel detailLabel = new JLabel(person);
        detailLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        item.add(eventLabel, BorderLayout.NORTH);
        item.add(detailLabel, BorderLayout.SOUTH);

        return item;
    }

    private JPanel createActiveRidesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 25));
        panel.setBackground(BACKGROUND_SILVER);

        JLabel title = new JLabel("Active Rides");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"Ride ID", "Driver", "Passenger", "From", "To", "Status", "Time"};
        Object[][] data = {
                {"#1047", "Driver 1", "User 1", "Basantapur", "Baneshwor", "In Progress", "10:24"},
                {"#1046", "Driver 2", "User 2", "Thamel", "Airport", "In Progress", "08:15"},
                {"#1045", "Driver 3", "User 3", "Patan", "Bhaktapur", "In Progress", "12:43"},
                {"#1044", "Driver 4", "User 4", "Koteshwor", "Chabahil", "In Progress", "05:20"},
                {"#1043", "Driver 5", "User 5", "Balaju", "Thankot", "In Progress", "15:08"}
        };

        JTable table = createStyledTable(data, columns);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDriversPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_SILVER);

        JLabel title = new JLabel("All Drivers");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"Driver ID", "Name", "Phone", "Rating", "Total Rides", "Status"};
        Object[][] data = {
                {"#245", "Driver 1", "+977-9841234567", "4.8", "1247", "Active"},
                {"#243", "Driver 2", "+977-9851234567", "4.9", "2891", "Active"},
                {"#241", "Driver 3", "+977-9861234567", "4.7", "892", "Active"},
                {"#239", "Driver 4", "+977-9871234567", "4.6", "654", "Offline"},
                {"#237", "Driver 5", "+977-9881234567", "4.9", "3421", "Active"}
        };

        JTable table = createStyledTable(data, columns);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_SILVER);

        JLabel title = new JLabel("Registered Users");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"User ID", "Name", "Email", "Phone", "Rides", "Joined"};
        Object[][] data = {
                {"#U892", "User 1", "user1@example.com", "+977-9840000000", "10", "2024-01-10"},
                {"#U891", "User 2", "user2@example.com", "+977-9840000001", "15", "2024-01-11"},
                {"#U890", "User 3", "user3@example.com", "+977-9840000002", "20", "2024-01-12"},
                {"#U889", "User 4", "user4@example.com", "+977-9840000003", "25", "2024-01-13"},
                {"#U888", "User 5", "user5@example.com", "+977-9840000004", "30", "2024-01-14"}
        };

        JTable table = createStyledTable(data, columns);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRevenuePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_SILVER);

        JLabel title = new JLabel("Revenue Analytics");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"Transaction ID", "User", "Amount", "Mode", "Date", "Status"};
        Object[][] data = {
                {"TXN-5041", "User 1", "Rs 450", "Card", "2024-12-07 14:30", "Completed"},
                {"TXN-5040", "User 2", "Rs 850", "Cash", "2024-12-07 14:15", "Completed"},
                {"TXN-5039", "User 3", "Rs 620", "Digital", "2024-12-07 13:45", "Completed"},
                {"TXN-5038", "User 4", "Rs 340", "Card", "2024-12-07 13:20", "Completed"},
                {"TXN-5037", "User 5", "Rs 1080", "Cash", "2024-12-07 12:50", "Completed"}
        };

        JTable table = createStyledTable(data, columns);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JTable createStyledTable(Object[][] data, String[] columns) {
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 15));
        table.setBackground(PANEL_WHITE);
        table.setGridColor(BORDER_GRAY);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(center);

        table.getTableHeader().setBackground(LIGHT_GRAY);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));

        return table;
    }

    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    public void refreshData() {
        cardLayout.show(contentPanel, "overview");
    }

    public void updateStatCard(String cardName, String value) {
    }
}
