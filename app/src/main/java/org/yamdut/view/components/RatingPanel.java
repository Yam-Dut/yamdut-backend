package org.yamdut.view.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.yamdut.utils.Theme;

public class RatingPanel extends JPanel {
    private JLabel[] stars;
    private int rating = 0;
    private JLabel ratingLabel;
    private JTextArea commentArea;
    
    public RatingPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(Theme.BACKGROUND_CARD);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(16, 16, 16, 16)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Rate Your Driver");
        titleLabel.setFont(Theme.getHeadingFont());
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        
        // Stars panel
        JPanel starsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        starsPanel.setOpaque(false);
        
        stars = new JLabel[5];
        for (int i = 0; i < 5; i++) {
            final int index = i;
            stars[i] = new JLabel("☆");
            stars[i].setFont(new Font("Segoe UI", Font.PLAIN, 32));
            stars[i].setForeground(Theme.TEXT_SECONDARY);
            stars[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            stars[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setRating(index + 1);
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    highlightStars(index + 1);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    updateStarDisplay();
                }
            });
            starsPanel.add(stars[i]);
        }
        
        // Rating text
        ratingLabel = new JLabel("Select rating");
        ratingLabel.setFont(Theme.getBodyFont());
        ratingLabel.setForeground(Theme.TEXT_SECONDARY);
        
        // Comment area
        commentArea = new JTextArea(3, 20);
        commentArea.setFont(Theme.getBodyFont());
        commentArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            new EmptyBorder(8, 8, 8, 8)
        ));
        commentArea.setBackground(Color.WHITE);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        // Note: JTextArea doesn't support placeholder, using tooltip instead
        commentArea.setToolTipText("Add a comment (optional)");
        
        JScrollPane commentScroll = new JScrollPane(commentArea);
        commentScroll.setBorder(null);
        commentScroll.setPreferredSize(new Dimension(0, 80));
        
        // Assemble
        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(starsPanel, BorderLayout.CENTER);
        topPanel.add(ratingLabel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(commentScroll, BorderLayout.CENTER);
    }
    
    private void setRating(int newRating) {
        rating = newRating;
        updateStarDisplay();
        updateRatingLabel();
    }
    
    private void highlightStars(int count) {
        for (int i = 0; i < 5; i++) {
            if (i < count) {
                stars[i].setText("★");
                stars[i].setForeground(Theme.COLOR_ACCENT);
            } else {
                stars[i].setText("☆");
                stars[i].setForeground(Theme.TEXT_SECONDARY);
            }
        }
    }
    
    private void updateStarDisplay() {
        highlightStars(rating);
    }
    
    private void updateRatingLabel() {
        if (rating == 0) {
            ratingLabel.setText("Select rating");
            ratingLabel.setForeground(Theme.TEXT_SECONDARY);
        } else {
            String[] labels = {"", "Poor", "Fair", "Good", "Very Good", "Excellent"};
            ratingLabel.setText(labels[rating] + " (" + rating + "/5)");
            ratingLabel.setForeground(Theme.COLOR_ACCENT);
        }
    }
    
    public int getRating() {
        return rating;
    }
    
    public String getComment() {
        return commentArea.getText().trim();
    }
    
    public void reset() {
        rating = 0;
        commentArea.setText("");
        updateStarDisplay();
        updateRatingLabel();
    }
}

