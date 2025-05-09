package hotel.management.system;

import java.awt.*;
import javax.swing.*;
import java.sql.*;
import java.awt.event.*;

public class UpdateRoom extends JFrame implements ActionListener {

    Choice croom;
    JTextField tfprice;
    JButton check, update, delete, back, suggest;
    JComboBox<String> availablecombo, cleancombo;

    UpdateRoom() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel text = new JLabel("UPDATE ROOM STATUS");
        text.setFont(new Font("Arial", Font.BOLD, 30));
        text.setBounds(340, 30, 400, 30);
        add(text);

        JLabel lblroom = new JLabel("Room Number");
        lblroom.setFont(new Font("Arial", Font.PLAIN, 17));
        lblroom.setBounds(52, 160, 120, 20);
        add(lblroom);

        croom = new Choice();
        croom.setBounds(200, 160, 180, 40);
        add(croom);

        try {
            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("SELECT roomnumber FROM room");
            while (rs.next()) {
                croom.add(rs.getString("roomnumber"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel lblavailable = new JLabel("Availability");
        lblavailable.setFont(new Font("Arial", Font.PLAIN, 17));
        lblavailable.setBounds(52, 230, 120, 20);
        add(lblavailable);

        String availableOptions[] = {"Available", "Not yet"};
        availablecombo = new JComboBox<>(availableOptions);
        availablecombo.setBounds(200, 230, 180, 30);
        availablecombo.setBackground(Color.WHITE);
        add(availablecombo);

        JLabel lblstatus = new JLabel("Cleaning Status");
        lblstatus.setFont(new Font("Arial", Font.PLAIN, 17));
        lblstatus.setBounds(52, 300, 120, 20);
        add(lblstatus);

        String cleanOptions[] = {"Cleaned", "Dirty"};
        cleancombo = new JComboBox<>(cleanOptions);
        cleancombo.setBounds(200, 300, 180, 30);
        cleancombo.setBackground(Color.WHITE);
        add(cleancombo);

        JLabel lblprice = new JLabel("Price");
        lblprice.setFont(new Font("Arial", Font.PLAIN, 17));
        lblprice.setBounds(52, 370, 120, 20);
        add(lblprice);

        tfprice = new JTextField();
        tfprice.setBounds(200, 370, 180, 30);
        add(tfprice);

        check = new JButton("SEARCH");
        check.setBounds(120, 520, 130, 30);
        check.setBackground(Color.BLUE);
        check.setForeground(Color.WHITE);
        check.addActionListener(this);
        add(check);

        update = new JButton("UPDATE");
        update.setBackground(new Color(255, 165, 0));
        update.setForeground(Color.WHITE);
        update.setBounds(420, 520, 130, 30);
        update.addActionListener(this);
        add(update);

        delete = new JButton("DELETE");
        delete.setBounds(570, 520, 130, 30);
        delete.setBackground(Color.RED);
        delete.setForeground(Color.WHITE);
        delete.addActionListener(this);
        add(delete);

        back = new JButton("BACK");
        back.setBackground(Color.BLACK);
        back.setForeground(Color.WHITE);
        back.setBounds(720, 520, 130, 30);
        back.addActionListener(this);
        add(back);

        suggest = new JButton("ASK PRICE");
        suggest.setBounds(270, 520, 130, 30);
        suggest.setBackground(new Color(34, 139, 34));
        suggest.setForeground(Color.WHITE);
        suggest.addActionListener(this);
        add(suggest);

        ImageIcon il = new ImageIcon(ClassLoader.getSystemResource("icons/seventh.jpg"));
        Image i2 = il.getImage().getScaledInstance(500, 300, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(475, 130, 450, 300);
        add(image);

        setBounds(265, 115, 1000, 650);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        String room = croom.getSelectedItem();

        if (ae.getSource() == check) {
            String query = "SELECT * FROM room WHERE roomnumber = '" + room + "'";
            try {
                Conn c = new Conn();
                ResultSet rs = c.s.executeQuery(query);
                if (rs.next()) {
                    availablecombo.setSelectedItem(rs.getString("availability"));
                    cleancombo.setSelectedItem(rs.getString("cleaning_status"));
                    tfprice.setText(rs.getString("price"));
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Attempted to search room ( Room no: " + room + ")", "Success");
                } else {
                    JOptionPane.showMessageDialog(null, "Room not found!");
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Attempted to search room ( Room no: " + room + ")", "Not Found");
                }
            } catch (Exception e) {
                e.printStackTrace();
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Attempted to search room ( Room no: " + room + ")", "Failed");
            }

        } else if (ae.getSource() == update) {
            String available = (String) availablecombo.getSelectedItem();
            String status = (String) cleancombo.getSelectedItem();
            String price = tfprice.getText();
            
            if(price.equals("")){
             JOptionPane.showMessageDialog(null, "Price Should Not Be Empty!");
             ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to update room", "Failed");
             return;
         }

            try {
                Conn c = new Conn();
                String updateQuery = "UPDATE room SET availability = '" + available + "', cleaning_status = '" + status + "', price = '" + price + "' WHERE roomnumber = '" + room + "'";
                c.s.executeUpdate(updateQuery);

                JOptionPane.showMessageDialog(null, "Room Details Updated Successfully");

                availablecombo.setSelectedIndex(0);
                cleancombo.setSelectedIndex(0);
                tfprice.setText("");

                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Attempted to update room ( Room no: " + room + ")", "Success");

            } catch (Exception e) {
                e.printStackTrace();
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Attempted to update room ( Room no: " + room + ")", "Failed");
            }

        } else if (ae.getSource() == delete) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Room " + room + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Conn c = new Conn();
                    String deleteQuery = "DELETE FROM room WHERE roomnumber = '" + room + "'";
                    c.s.executeUpdate(deleteQuery);

                    JOptionPane.showMessageDialog(null, "Room Deleted Successfully");

                    availablecombo.setSelectedIndex(0);
                    cleancombo.setSelectedIndex(0);
                    tfprice.setText("");

                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Attempted to delete room ( Room no: " + room + ")", "Success");

                } catch (Exception e) {
                    e.printStackTrace();
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Attempted to delete room ( Room no: " + room + ")", "Failed");
                }
            }

        } else if (ae.getSource() == back) {
            setVisible(false);
            new SearchRoom();
        }else if (ae.getSource() == suggest) {
    try {
        Conn c = new Conn();

        String query = "SELECT bed_type FROM room WHERE roomnumber = '" + room + "'";
        ResultSet rs = c.s.executeQuery(query);

        if (rs.next()) {
            String bedType = rs.getString("bed_type");

            // Set base price
            int basePrice;
            if (bedType.equalsIgnoreCase("Single Bed")) {
                basePrice = 25000;
            } else if (bedType.equalsIgnoreCase("Double Bed")) {
                basePrice = 35000;
            } else {
                basePrice = 30000;
            }

            // Get current month
            int month = java.time.LocalDate.now().getMonthValue();

            double multiplier;
            if (month >= 12 || month <= 3) {
                multiplier = 1.3;
            } else if (month >= 4 && month <= 8) {
                multiplier = 0.8;
            } else {
                multiplier = 1.0;
            }

            int suggestedPrice = (int) (basePrice * multiplier);
            tfprice.setText(String.valueOf(suggestedPrice));
               ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Suggested price for room " + room, "Success");
        } else {
            JOptionPane.showMessageDialog(null, "Room not found!");
        }

    } catch (Exception e) {
        e.printStackTrace();
        ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Attempted to suggest price for room " + room, "Failed");
    }
}

    }

    public static void main(String[] args) {
        new UpdateRoom();
    }
}
