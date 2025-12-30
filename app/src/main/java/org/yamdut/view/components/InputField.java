package org.yamdut.view.components;

import org.yamdut.utils.Theme;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class InputField extends JPanel {
    private JTextField textField;
    private JPasswordField passwordField;
    private JLabel label;
    private boolean isPassword;
    
    public InputField(String placeholder) {
        this(placeholder, false);
    }
    
    public InputField(String placeholder, boolean isPassword) {
        this.isPassword = isPassword;
        initComponents(placeholder);
        setupStyles();
    }
    
    private void initComponents(String placeholder) {
        setLayout(new BorderLayout(0, 5));
        setOpaque(false);
        
        // Label
        label = new JLabel(placeholder);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(Theme.TEXT_SECONDARY);
        
        // Input Field
        if (isPassword) {
            passwordField = new JPasswordField();
        } else {
            textField = new JTextField();
        }
        
        add(label, BorderLayout.NORTH);
        if (isPassword) {
            add(passwordField, BorderLayout.CENTER);
        } else {
            add(textField, BorderLayout.CENTER);
        }
    }
    
    private void setupStyles() {
        Border lineBorder = new LineBorder(Theme.BORDER_COLOR, 1);
        Border paddingBorder = new EmptyBorder(10, 15, 10, 15);
        Border compoundBorder = new CompoundBorder(lineBorder, paddingBorder);
        
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        
        if (textField != null) {
            textField.setBorder(compoundBorder);
            textField.setFont(fieldFont);
            textField.setBackground(Theme.BACKGROUND_SECONDARY);
            textField.setForeground(Theme.TEXT_PRIMARY);
            textField.setCaretColor(Theme.TEXT_PRIMARY);
        }
        
        if (passwordField != null) {
            passwordField.setBorder(compoundBorder);
            passwordField.setFont(fieldFont);
            passwordField.setBackground(Theme.BACKGROUND_SECONDARY);
            passwordField.setForeground(Theme.TEXT_PRIMARY);
            passwordField.setCaretColor(Theme.TEXT_PRIMARY);
        }
    }
    
    // Getters
    public JTextField getTextField() {
        return textField;
    }
    
    public JPasswordField getPasswordField() {
        return passwordField;
    }
    
    public String getText() {
        if (textField != null) {
            return textField.getText();
        } else if (passwordField != null) {
            return new String(passwordField.getPassword());
        }
        return "";
    }
    
    public void setText(String text) {
        if (textField != null) {
            textField.setText(text);
        }
    }
    
    public void clear() {
        if (textField != null) {
            textField.setText("");
        } else if (passwordField != null) {
            passwordField.setText("");
        }
    }
}
