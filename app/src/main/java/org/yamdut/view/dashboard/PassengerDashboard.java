package org.yamdut.view.dashboard;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.yamdut.utils.Theme;
import org.yamdut.model.Role;
import org.yamdut.view.map.MapPanel;

public class PassengerDashboard extends BaseDashboard {
    private JTextField pickupField;
    private JTextField destinationField;
    private JButton bookRideButton;

    private DefaultListModel<String> driverListModel;
    private JList<String> driverList;

    private MapPanel mapPanel;

    public PassengerDashboard() {
        super();
        setWelcomeMessage("Welcome, Passenger");
        initContent(); 
    }

    @Override
    protected void initContent() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Theme.BACKGROUND_PRIMARY);


        //Left control panel

        JPanel controlPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        controlPanel.setBackground(Theme.BACKGROUND_PRIMARY);

        pickupField = new JTextField();
        destinationField = new JTextField();

        bookRideButton = new JButton("Book Ride");

        controlPanel.add(new JLabel("Pickup Location"));
        controlPanel.add(pickupField);

        controlPanel.add(new JLabel("Destination"));
        controlPanel.add(destinationField);

        controlPanel.add(new JLabel());
        controlPanel.add(bookRideButton);


        //driver list
        
        driverListModel = new DefaultListModel<>();
        driverList = new JList<>(driverListModel);
        JScrollPane driverScroll = new JScrollPane(driverList);
        driverScroll.setBorder(BorderFactory.createTitledBorder("Available Drivers"));

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(Theme.BACKGROUND_PRIMARY);
        leftPanel.add(controlPanel, BorderLayout.NORTH);
        leftPanel.add(driverScroll, BorderLayout.CENTER);


        //map panel (core)

        mapPanel = new MapPanel(Role.PASSENGER);
    
        //layout

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(mapPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }


    public JTextField getPickupField() {
        return pickupField;
    }

    public JTextField getDestinationField() {
        return destinationField;
    }

    public JButton getBookRideButton() {
        return bookRideButton;
    }

    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public DefaultListModel<String> getDriverListModel() {
        return driverListModel;
    }
    public JList<String> getDriverList() {
        return driverList;
    }
}


