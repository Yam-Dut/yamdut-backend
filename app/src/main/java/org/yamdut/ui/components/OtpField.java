package org.yamdut.ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class OtpField extends JPanel {
    private JTextField[] digitFields;
    private int otpLength;
    
    public OtpField(int length) {
        this.otpLength = length;
        initUI();
    }
    
    private void initUI() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        setBackground(null);
        
        digitFields = new JTextField[otpLength];
        
        for (int i = 0; i < otpLength; i++) {
            digitFields[i] = createDigitField();
            add(digitFields[i]);
            
            final int index = i;
            
            // Auto-focus next field on input
            digitFields[i].addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (Character.isDigit(c)) {
                        if (index < otpLength - 1) {
                            digitFields[index + 1].requestFocus();
                        }
                    } else {
                        e.consume(); // Ignore non-digit
                    }
                }
                
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        if (digitFields[index].getText().isEmpty() && index > 0) {
                            digitFields[index - 1].requestFocus();
                            digitFields[index - 1].setText("");
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_LEFT && index > 0) {
                        digitFields[index - 1].requestFocus();
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && index < otpLength - 1) {
                        digitFields[index + 1].requestFocus();
                    }
                }
            });
        }
    }
    
    private JTextField createDigitField() {
        JTextField field = new JTextField(1);
        field.setFont(new Font("Segoe UI", Font.BOLD, 24));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setPreferredSize(new Dimension(50, 60));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(0, 0, 0, 0)
        ));
        field.setBackground(Color.WHITE);
        return field;
    }
    
    public String getOtp() {
        StringBuilder otp = new StringBuilder();
        for (JTextField field : digitFields) {
            otp.append(field.getText());
        }
        return otp.toString();
    }
    
    public void clear() {
        for (JTextField field : digitFields) {
            field.setText("");
        }
        digitFields[0].requestFocus();
    }
    
    public void setFocus() {
        digitFields[0].requestFocus();
    }
}