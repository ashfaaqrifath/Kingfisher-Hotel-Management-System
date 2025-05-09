package hotel.management.system;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.text.NumberFormatter;
import java.text.DecimalFormat;

public class AddEvent extends JFrame implements ActionListener {

    JButton add, back, search, clear;
    JDateChooser dateChooser;
    JSpinner hourSpinner, minuteSpinner;
    JComboBox<String> amPmComboBox;
    JTextField eventnofield, descripfield;
    JComboBox<String> durationHourComboBox, durationMinuteComboBox;

    public AddEvent() {

        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel heading = new JLabel("Add Events");
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setText(heading.getText().toUpperCase());
        heading.setBounds(400, 30, 200, 30);
        heading.setHorizontalAlignment(JLabel.CENTER);
        add(heading);

        JLabel lbleventno = new JLabel("Event Number");
        lbleventno.setFont(new Font("Arial", Font.PLAIN, 17));
        lbleventno.setBounds(52, 120, 120, 20);
        add(lbleventno);

        eventnofield = new JTextField();
        eventnofield.setBounds(200, 120, 180, 30);
        add(eventnofield);
        setLastEventNumber();

        JLabel lbldescription = new JLabel("Description");
        lbldescription.setFont(new Font("Arial", Font.PLAIN, 17));
        lbldescription.setBounds(52, 190, 120, 20);
        add(lbldescription);

        descripfield = new JTextField();
        descripfield.setBounds(200, 190, 180, 30);
        add(descripfield);

        JLabel lblDate = new JLabel("Date");
        lblDate.setFont(new Font("Arial", Font.PLAIN, 17));
        lblDate.setBounds(52, 260, 120, 20);
        add(lblDate);

        dateChooser = new JDateChooser();
        dateChooser.setBounds(200, 260, 180, 30);
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setMinSelectableDate(new java.util.Date());
        add(dateChooser);

        JLabel lbltime = new JLabel("Time");
        lbltime.setFont(new Font("Arial", Font.PLAIN, 17));
        lbltime.setBounds(52, 330, 120, 20);
        add(lbltime);

        SpinnerModel hourModel = new SpinnerNumberModel(12, 1, 12, 1);
        hourSpinner = new JSpinner(hourModel);
        hourSpinner.setBounds(200, 330, 60, 32);
        add(hourSpinner);

        SpinnerModel minuteModel = new SpinnerNumberModel(0, 0, 59, 1);
        minuteSpinner = new JSpinner(minuteModel);
        minuteSpinner.setBounds(260, 330, 60, 32);

        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) minuteSpinner.getEditor();
        NumberFormatter formatter = (NumberFormatter) editor.getTextField().getFormatter();
        formatter.setFormat(new DecimalFormat("00"));
        add(minuteSpinner);

        String[] ampmOptions = {"AM", "PM"};
        amPmComboBox = new JComboBox<>(ampmOptions);
        amPmComboBox.setBounds(320, 330, 60, 32);
        add(amPmComboBox);

        JLabel lblduration = new JLabel("Duration");
        lblduration.setFont(new Font("Arial", Font.PLAIN, 17));
        lblduration.setBounds(52, 400, 120, 20);
        add(lblduration);

        String[] hours = new String[13];
        for (int i = 0; i <= 12; i++) hours[i] = String.valueOf(i);

        String[] minutes = {"00", "15", "30", "45"};

        durationHourComboBox = new JComboBox<>(hours);
        durationHourComboBox.setBounds(200, 400, 60, 30);
        add(durationHourComboBox);

        JLabel hrLabel = new JLabel("hr");
        hrLabel.setBounds(265, 400, 30, 30);
        hrLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        add(hrLabel);

        durationMinuteComboBox = new JComboBox<>(minutes);
        durationMinuteComboBox.setBounds(290, 400, 60, 30);
        add(durationMinuteComboBox);

        JLabel minLabel = new JLabel("min");
        minLabel.setBounds(355, 400, 40, 30);
        minLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        add(minLabel);

        add = new JButton("ADD");
        add.setBounds(170, 530, 150, 30);
        add.setBackground(new Color(0, 153, 76));
        add.setForeground(Color.WHITE);
        add.addActionListener(this);
        add(add);
        
        clear = new JButton("CLEAR");
        clear.setBounds(510, 530, 150, 30);
        clear.setBackground(Color.gray);
        clear.setForeground(Color.WHITE);
        clear.addActionListener(this);
        add(clear);

        search = new JButton("SEARCH");
        search.setBounds(340, 530, 150, 30);
        search.setBackground(Color.BLUE);
        search.setForeground(Color.WHITE);
        search.addActionListener(this);
        add(search);

        back = new JButton("BACK");
        back.setBounds(680, 530, 150, 30);
        back.setBackground(Color.BLACK);
        back.setForeground(Color.WHITE);
        back.addActionListener(this);
        add(back);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/date.jpg"));
        JLabel image = new JLabel(i1);
        image.setBounds(495, 130, 430, 360);
        add(image);

        setBounds(265, 115, 1000, 650);
        setVisible(true);
    }

    private void setLastEventNumber() {
        try {

            Conn conn = new Conn();
            String query = "SELECT eventnumber FROM event ORDER BY eventnumber DESC LIMIT 1";
            ResultSet rs = conn.s.executeQuery(query);
            if (rs.next()) {

                int lastEventNumber = rs.getInt("eventnumber");

                eventnofield.setText(String.valueOf(lastEventNumber + 1));
            } else {
                eventnofield.setText("1");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == add) {

            String eventnumber = eventnofield.getText().trim();
            String description = descripfield.getText().trim();

            if (!eventnumber.matches("\\d+") || Integer.parseInt(eventnumber) <= 0) {
                JOptionPane.showMessageDialog(null, "Event Number must be a positive integer!", "Input Error", JOptionPane.ERROR_MESSAGE);
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add event ( Event no: " + eventnumber + ")", "Failed");
                return;
            }

            java.util.Date selectedDate = dateChooser.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(null, "Please select a date!", "Input Error", JOptionPane.ERROR_MESSAGE);
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add event ( Event no: " + eventnumber + ")", "Failed");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(selectedDate);

            try {
                Conn conn = new Conn();

                String checkDateQuery = "SELECT COUNT(*) FROM event WHERE date = '" + date + "'";
                ResultSet rsDate = conn.s.executeQuery(checkDateQuery);
                rsDate.next();

                if (rsDate.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(null, "This date is already reserved! Choose another date.", "Date Conflict", JOptionPane.ERROR_MESSAGE);
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add event ( Event no: " + eventnumber + ")", "Already Reserved");
                    return;
                }

                String checkQuery = "SELECT COUNT(*) FROM event WHERE eventnumber = " + eventnumber;
                ResultSet rs = conn.s.executeQuery(checkQuery);
                rs.next();

                if (rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(null, "Event Number already exists! Choose a different number.", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add event ( Event no: " + eventnumber + ")", "Failed");
                    return;
                }

                String selectedHour = String.format("%02d", (int) hourSpinner.getValue());
                String selectedMinute = String.format("%02d", (int) minuteSpinner.getValue());
                String selectedAmPm = (String) amPmComboBox.getSelectedItem();
                String time = selectedHour + ":" + selectedMinute + " " + selectedAmPm;

                String durHour = (String) durationHourComboBox.getSelectedItem();
                String durMinute = (String) durationMinuteComboBox.getSelectedItem();

                if (durHour.equals("0") && durMinute.equals("00")) {
                    JOptionPane.showMessageDialog(null, "Duration cannot be 0!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add event ( Event no: " + eventnumber + ")", "Failed");
                    return;
                }

                String duration = durHour + " hr " + durMinute + " min";

                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields must be filled!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add event ( Event no: " + eventnumber + ")", "Failed");
                    return;
                }

                String insertQuery = "INSERT INTO event VALUES(" + eventnumber + ", '" + description + "', '" + date + "', '" + time + "', '" + duration + "')";
                conn.s.executeUpdate(insertQuery);

                JOptionPane.showMessageDialog(null, "Event Successfully Added");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add event ( Event no: " + eventnumber + ")", "Success");

                descripfield.setText(""); 
                dateChooser.setDate(null); 
                hourSpinner.setValue(12); 
                minuteSpinner.setValue(0);
                amPmComboBox.setSelectedIndex(0);
                durationHourComboBox.setSelectedIndex(0); 
                durationMinuteComboBox.setSelectedIndex(0);
                setLastEventNumber(); 

            } catch (HeadlessException | SQLException e) {
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add event ( Event no: " + eventnumber + ")", "Failed");
                e.printStackTrace();
            }
        }

        else if (ae.getSource() == search) {
            setVisible(false);
            new SearchEvent();
        }

        else if (ae.getSource() == back) {
            setVisible(false);
        }

        else if (ae.getSource() == clear) {
 
            descripfield.setText("");
            dateChooser.setDate(null);
            hourSpinner.setValue(12);
            minuteSpinner.setValue(0);
            amPmComboBox.setSelectedIndex(0);
            durationHourComboBox.setSelectedIndex(0);
            durationMinuteComboBox.setSelectedIndex(0);
            setLastEventNumber(); 
        }
    }


    public static void main(String[] args) {
        new AddEvent();
    }
}
