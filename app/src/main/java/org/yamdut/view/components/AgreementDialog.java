package org.yamdut.view.components;

import javax.swing.*;
import java.awt.*;
import org.yamdut.utils.Theme;

public class AgreementDialog extends JDialog {

    private boolean accepted = false;

    public AgreementDialog(Window parent) {
        super(parent, "Terms & Conditions", ModalityType.APPLICATION_MODAL);
        initUI();
    }

    private void initUI() {
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BACKGROUND_PRIMARY);

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(Theme.COLOR_PRIMARY);
        JLabel titleLabel = new JLabel("YamDut Terms of Service");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JTextArea termsArea = new JTextArea();
        termsArea.setEditable(false);
        termsArea.setLineWrap(true);
        termsArea.setWrapStyleWord(true);
        termsArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        termsArea.setBackground(Theme.BACKGROUND_PRIMARY);
        termsArea.setForeground(Theme.TEXT_PRIMARY);

        termsArea.setText(loadTermsText());

        JScrollPane scrollPane = new JScrollPane(termsArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        scrollPane.setBackground(Theme.BACKGROUND_PRIMARY);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND_PRIMARY);

        add(scrollPane, BorderLayout.CENTER);

        // Footer Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER_COLOR));

        JButton cancelBtn = new JButton("Decline");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelBtn.setForeground(Theme.TEXT_SECONDARY);
        cancelBtn.setBackground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> {
            accepted = false;
            dispose();
        });

        JButton acceptBtn = new JButton("Accept & Continue");
        acceptBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        acceptBtn.setForeground(Color.WHITE);
        acceptBtn.setBackground(Theme.COLOR_PRIMARY);
        acceptBtn.setFocusPainted(false);
        acceptBtn.addActionListener(e -> {
            accepted = true;
            dispose();
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(acceptBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public boolean isAccepted() {
        return accepted;
    }

    private String loadTermsText() {
        return """
        YamDut Terms & Conditions
        Last Updated: January 2026

        1. Acceptance of Terms
        By using YamDut, you agree to be bound by these terms. If you do not agree to these terms, please do not use our services.

        2. Account Registration
        You must provide accurate and complete information when creating an account.
        KYC verification is not there so be careful with your information.

        3. User Conduct
        Users agree not to use the service for any unlawful purposes. Any harassment or fraudulent activity will result in immediate termination of the account.

        4. Privacy Policy
        We collect data such as location, name, and contact information to provide ride-sharing services. By using YamDut, you consent to our collection and use of this information.
        Passwords are not stored in plain text and are encrypted for security. We used SHA-256 for password encryption.

        5. Payments and Fees
        Payments for rides are processed through our secure payment gateway. Users agree to pay all applicable fees for the services requested.

        6. Limitation of Liability
        YamDut provides a platform for connecting riders and drivers. We are not responsible for the conduct of users outside the scope of our application.

        7. Changes to Terms
        We reserve the right to modify these terms at any time. Continued use of the service constitutes acceptance of the updated terms.

        ---
        Please contact jon.snow.ghost78@gmail.com for any questions regarding these terms.
        """;
    }
}
