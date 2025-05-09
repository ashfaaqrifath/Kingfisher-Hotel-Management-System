package hotel.management.system;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.sql.SQLException;
import java.util.HashMap;
import javax.swing.JOptionPane;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.*;
import java.awt.Font;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.BaseColor; 
import com.itextpdf.text.html.WebColors;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.image.BufferedImage;
import java.awt.Color;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.jfree.chart.title.TextTitle;
import java.awt.GridLayout;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;



public class SearchEvent extends JFrame implements ActionListener {
    
    private static boolean isReminderShown = false;
    private AddEvent addEventReference;
    
    JTextField eventnofield, descripfield, durafield;
    JDateChooser dateChooser;
    JSpinner hourSpinner, minuteSpinner;
    JComboBox<String> ampmComboBox, durationHourComboBox, durationMinuteComboBox;
    JButton search, clear, edit, delete, report,back;
    JTable eventTable;
    DefaultTableModel tableModel;   

    public SearchEvent() {
        
        setTitle("Search Event");
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel heading = new JLabel("Search Event");
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setText(heading.getText().toUpperCase());
        heading.setBounds(330, 30, 340, 30);
        heading.setHorizontalAlignment(JLabel.CENTER);
        add(heading);
       
        JLabel lbleventno = new JLabel("Event Number:");
        lbleventno.setFont(new Font("Arial", Font.PLAIN, 17));
        lbleventno.setBounds(52, 100, 120, 20);
        add(lbleventno);
        eventnofield = new JTextField();
        eventnofield.setBounds(200, 100, 180, 30);
        add(eventnofield);

        JLabel lbldescription = new JLabel("Description:");
        lbldescription.setFont(new Font("Arial", Font.PLAIN, 17));
        lbldescription.setBounds(52, 170, 120, 20);
        add(lbldescription);
        descripfield = new JTextField();
        descripfield.setBounds(200, 170, 180, 30);
        add(descripfield);
    
        JLabel lblDate = new JLabel("Date:");
        lblDate.setFont(new Font("Arial", Font.PLAIN, 17));
        lblDate.setBounds(52, 240, 120, 20);
        add(lblDate);
        dateChooser = new JDateChooser();
        dateChooser.setBounds(200, 240, 180, 30);
        dateChooser.setMinSelectableDate(new java.util.Date());
        add(dateChooser);
     
        JLabel lblTime = new JLabel("Time:");
        lblTime.setFont(new Font("Arial", Font.PLAIN, 17));
        lblTime.setBounds(52, 310, 120, 20);
        add(lblTime);
        SpinnerModel hourModel = new SpinnerNumberModel(12, 1, 12, 1);
        hourSpinner = new JSpinner(hourModel);
        hourSpinner.setBounds(200, 310, 60, 32);
        add(hourSpinner);
        SpinnerModel minuteModel = new SpinnerNumberModel(0, 0, 59, 1);
        minuteSpinner = new JSpinner(minuteModel);
        minuteSpinner.setBounds(260, 310, 60, 32);
        add(minuteSpinner);
        String[] ampmOptions = {"AM", "PM"};
        ampmComboBox = new JComboBox<>(ampmOptions);
        ampmComboBox.setBounds(320, 310, 60, 32);
        add(ampmComboBox);
   
                    JLabel lblduration = new JLabel("Duration:");
                    lblduration.setFont(new Font("Arial", Font.PLAIN, 17));
                    lblduration.setBounds(52, 380, 120, 20);
                    add(lblduration);

                    String[] hours = new String[13]; // 0–12
                    for (int i = 0; i <= 12; i++) hours[i] = String.valueOf(i);
                    String[] minutes = {"00", "15", "30", "45"};

                    durationHourComboBox = new JComboBox<>(hours);
                    durationHourComboBox.setBounds(200, 380, 60, 30);
                    add(durationHourComboBox);
                        // Label for hours
                    JLabel hrLabel = new JLabel("hr");
                    hrLabel.setBounds(265, 380, 30, 30);
                    hrLabel.setFont(new Font("Arial", Font.PLAIN, 15));
                    add(hrLabel);

                    durationMinuteComboBox = new JComboBox<>(minutes);
                    durationMinuteComboBox.setBounds(290, 380, 60, 30);
                    add(durationMinuteComboBox);
                    JLabel minLabel = new JLabel("min");
                    minLabel.setBounds(355, 380, 40, 30);
                    minLabel.setFont(new Font("Arial", Font.PLAIN, 15));
                    add(minLabel);

        //Buttons
        search = new JButton("SEARCH"); 
        search.setBounds(45, 520, 130, 30);
        search.setBackground(Color.BLUE);
        search.setForeground(Color.WHITE);
        search.addActionListener(this);
        add(search);
        clear = new JButton("CLEAR");
        clear.setBounds(645, 520, 130, 30);
        clear.setBackground(Color.GRAY);
        clear.setForeground(Color.WHITE);
        clear.addActionListener(this);
        add(clear);
        edit = new JButton("UPDATE");
        edit.setBackground(new Color(255, 165, 0));
        edit.setBounds(195, 520, 130, 30);
        edit.setForeground(Color.WHITE);
        edit.addActionListener(this);
        add(edit);        
        delete = new JButton("DELETE");
        delete.setBounds(345, 520, 130, 30);
        delete.setBackground(Color.RED);
        delete.setForeground(Color.WHITE);
        delete.addActionListener(this);
        add(delete);        
        report = new JButton("REPORT");
        report.setBounds(495, 520, 130, 30);
        report.setBackground(new Color(102, 0, 153));
        report.setForeground(Color.WHITE);
        report.addActionListener(this);
        add(report);        
        back = new JButton("BACK");
        back.setBounds(795, 520, 130, 30);
        back.setBackground(Color.BLACK);
        back.setForeground(Color.WHITE);
        back.addActionListener(this);
        add(back);

        String[] columnNames = {"Event Number", "Description", "Date", "Time", "Duration"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        eventTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(eventTable);
        
        eventTable.getColumnModel().getColumn(0).setPreferredWidth(100); 
        eventTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        eventTable.getColumnModel().getColumn(2).setPreferredWidth(120); 
        eventTable.getColumnModel().getColumn(3).setPreferredWidth(120); 
        eventTable.getColumnModel().getColumn(4).setPreferredWidth(120); 
        
        tableScrollPane.setBounds(410, 90, 522, 400);
        add(tableScrollPane);

        loadAllEvents();

        setBounds(265, 115, 1000, 650);
        setVisible(true);
                        
                        showTodayReminder();
                                
    }
                        
                        private void showTodayReminder() {
                            try {
                                        
                                Conn conn = new Conn();
                                
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                String today = dateFormat.format(new java.util.Date());

                                
                                String query = "SELECT * FROM event WHERE date = '" + today + "'";
                                ResultSet rs = conn.s.executeQuery(query);
                                        
                                 
                                if (rs.next()) {
                                    
                                    String eventNum = rs.getString("eventnumber");
                                    String desc = rs.getString("description");
                                    String eventTimeStr = rs.getString("time");
                                    String duration = rs.getString("duration");

                                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
                                    LocalTime eventTime = LocalTime.parse(eventTimeStr, timeFormatter);
                                    LocalTime currentTime = LocalTime.now();// Get the current system time

                                    if (currentTime.isBefore(eventTime)) {
                                        JDialog popup = new JDialog(this, "🔔 Today's Event Reminder", true);
                                        popup.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15)); 
                                        popup.setLayout(new GridLayout(5, 1, 20, 10));
                                        popup.setSize(250, 250);
                                        popup.setLocationRelativeTo(this);
                                        popup.setAlwaysOnTop(true);

                                        JLabel label1 = new JLabel("📅 You have an event today!", JLabel.CENTER);
                                        label1.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
                                        popup.add(label1);
                                        popup.add(new JLabel("Event No: " + eventNum, JLabel.CENTER));
                                        popup.add(new JLabel("Description: " + desc, JLabel.CENTER));
                                        popup.add(new JLabel("Time: " + eventTimeStr, JLabel.CENTER));
                                        popup.add(new JLabel("Duration: " + duration, JLabel.CENTER));
                                                
                                        popup.setVisible(true);
                                    }
                                }
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


    private void loadAllEvents() {
        try {
            
                    // Create a new database connection
            Conn conn = new Conn(); 
            String query = "SELECT * FROM event"; // SQL query to select all events
            ResultSet rs = conn.s.executeQuery(query);// Execute the query and store the result in a ResultSet
            tableModel.setRowCount(0); // Clear the existing rows in the table

                    // Loop through each row in the result set
            while (rs.next()) {
                
                    // Create a row object containing data for each column in the event table
                Object[] row = {
                    rs.getInt("eventnumber"),
                    rs.getString("description"),
                    rs.getString("date"),
                    rs.getString("time"),
                    rs.getString("duration")
                };
                tableModel.addRow(row);// Add the row to the table model
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
        // Handle button click events
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == search) {  // Call searchEvent()
            searchEvent();
        }
        else if (ae.getSource() == clear) {
            clearFields();
        }
        else if (ae.getSource() == edit) {
            updateEvent();
        }
        else if (ae.getSource() == delete) {
            deleteEvent();
        }
        else if (ae.getSource() == report) {
            generateReport();
        }
        else if (ae.getSource() == back) {
            setVisible(false);
            new AddEvent();
        }
    }

    private void searchEvent() {
        
            // Get the text from the event number input field and remove leading/trailing spaces
        String eventnumber = eventnofield.getText().trim();
        Date selectedDate = dateChooser.getDate();// Get the selected date from the date chooser

        if (!eventnumber.isEmpty()) {
                // Search by event number validate with positive integer 
            if (!eventnumber.matches("\\d+") || Integer.parseInt(eventnumber) <= 0) {
                JOptionPane.showMessageDialog(null, "Enter a valid positive integer for Event Number!", "Input Error", JOptionPane.ERROR_MESSAGE);
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to search event ( Event no: " + eventnumber + ")", "Failed");
                return;
            }

            try {
                Conn conn = new Conn();
                String query = "SELECT * FROM event WHERE eventnumber = ?";
                PreparedStatement pst = conn.c.prepareStatement(query);
                pst.setInt(1, Integer.parseInt(eventnumber));// Set the event number parameter in the query
                ResultSet rs = pst.executeQuery();
                tableModel.setRowCount(0);// Clear any existing rows from the table before displaying results

                if (rs.next()) {
                        // Populate fields from the result set.
                    populateFieldsFromResultSet(rs);
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to search event ( Event no: " + eventnumber + ")", "Success");
                } else {
                    JOptionPane.showMessageDialog(null, "Event Not Found!", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to search event ( Event no: " + eventnumber + ")", "Not Found");
                    clearFields();
                }
            } catch (SQLException e) {
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to search event ( Event no: " + eventnumber + ")", "Failed");
                e.printStackTrace();
            }
            
            
        // Search by date   
        } else if (selectedDate != null) {
           
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// Format the selected date to match the DB date format
            String formattedDate = sdf.format(selectedDate);

            try {
                Conn conn = new Conn();
                String query = "SELECT * FROM event WHERE date = ?";
                PreparedStatement pst = conn.c.prepareStatement(query);
                pst.setString(1, formattedDate);
                ResultSet rs = pst.executeQuery();
                tableModel.setRowCount(0);

                if (rs.next()) {
                    // Populate fields from the result set, including the JComboBox for duration
                    populateFieldsFromResultSet(rs);
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to search event ( Event no: " + eventnumber + ")", "Success");
                } else {
                    JOptionPane.showMessageDialog(null, "No events found for the selected date.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to search event ( Event no: " + eventnumber + ")", "Not Found");
                    clearFields();
                }
                                     
            } catch (SQLException e) {
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to search event ( Event no: " + eventnumber + ")", "Failed");
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please enter Event Number or select a Date to search.", "Input Required", JOptionPane.WARNING_MESSAGE);
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to search event ( Event no: " + eventnumber + ")", "Failed");
        }
        
    }
    
 
    private void populateFieldsFromResultSet(ResultSet rs) throws SQLException {
        
        eventnofield.setText(String.valueOf(rs.getInt("eventnumber")));// Set the event number in the event number text field
        descripfield.setText(rs.getString("description"));
        dateChooser.setDate(rs.getDate("date"));
        dateChooser.setMinSelectableDate(new java.util.Date());// Set the minimum selectable date to today (to prevent selecting past dates)

            // Retrieve the time string from the result set (e.g., "09:30 AM")
        String timeValue = rs.getString("time");
        String[] timeParts = timeValue.split("[: ]");
        int hours = Integer.parseInt(timeParts[0]);// Extract hour part
        int minutes = Integer.parseInt(timeParts[1]);// Extract minute part
        String ampm = timeParts[2];
        
            // Set values in hour and minute spinners and AM/PM combo box
        hourSpinner.setValue(hours);
        minuteSpinner.setValue(minutes);
        ampmComboBox.setSelectedItem(ampm);
        
            // Get the duration string from the database
        String durafield = rs.getString("duration"); 
        String[] parts = durafield.split(" ");// Split the duration string into parts
        
            // Check if duration is in the format "1 hr 30 min"
        if (parts.length == 4) {
            durationHourComboBox.setSelectedItem(parts[0]); 
            durationMinuteComboBox.setSelectedItem(parts[2]); 
        }
             // Handle formats like "2 hr" or "45 min"
        else if (parts.length == 2) {
            
                // If it's only hours (e.g., "2 hr"), set hour and default minute to "00"
            if (parts[1].equalsIgnoreCase("hr")) {
                
                durationHourComboBox.setSelectedItem(parts[0]);
                durationMinuteComboBox.setSelectedItem("00");
            } else if (parts[1].equalsIgnoreCase("min")) {
                durationHourComboBox.setSelectedItem("0");
                durationMinuteComboBox.setSelectedItem(parts[0]);
            }
        }

        Object[] row = {
            rs.getInt("eventnumber"),
            rs.getString("description"),
            rs.getString("date"),
            timeValue,
            rs.getString("duration")
        };
        tableModel.addRow(row);
    }

  
    private void updateEvent() {
        String eventnumber = eventnofield.getText().trim();
        String description = descripfield.getText().trim();
        Date selectedDate = dateChooser.getDate(); // Retrieve selected date
        int hours = (int) hourSpinner.getValue();
        int minutes = (int) minuteSpinner.getValue();
        
        String ampm = ampmComboBox.getSelectedItem().toString();
        String selectedHour = (String) durationHourComboBox.getSelectedItem();
        String selectedMinute = (String) durationMinuteComboBox.getSelectedItem();

        if (selectedHour == null || selectedMinute == null || selectedHour.isEmpty() || selectedMinute.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please select both hour and minute for duration!", "Input Error", JOptionPane.ERROR_MESSAGE);
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to update event ( Event no: " + eventnumber + ")", "Failed");
            return;
        }

        String durHour = (String) durationHourComboBox.getSelectedItem();
        String durMinute = (String) durationMinuteComboBox.getSelectedItem();
        if (durHour.equals("0") && durMinute.equals("00")) {
                            JOptionPane.showMessageDialog(null, "Duration cannot be 0!", "Input Error", JOptionPane.ERROR_MESSAGE);
                            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to update event ( Event no: " + eventnumber + ")", "Failed");
                            return;
                        }
        String duration = durHour + " hr " + durMinute + " min";

        // Validate inputs
        if (eventnumber.isEmpty() || description.isEmpty() || selectedDate == null || duration.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill all fields before updating!", "Update Error", JOptionPane.ERROR_MESSAGE);
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to update event ( Event no: " + eventnumber + ")", "Failed");
            return;
        }
        // Convert Date to String
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(selectedDate);
       
        // Check if the date already exists for a different event number
        try {
            Conn conn = new Conn();
            String checkDateQuery = "SELECT * FROM event WHERE date = ? AND eventnumber != ?";
            PreparedStatement pstCheck = conn.c.prepareStatement(checkDateQuery);
            pstCheck.setString(1, date);  // Date to check
            pstCheck.setInt(2, Integer.parseInt(eventnumber));  // Ensure the event number is not the same as the one being updated
            ResultSet rs = pstCheck.executeQuery();

            // If the date is already reserved for another event, show an error message and return
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "The selected date is already reserved for another event. Please choose another date.", "Date Reserved", JOptionPane.ERROR_MESSAGE);
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to update event ( Event no: " + eventnumber + ")", "Already Reserved");
            }

            // Format the time to AM/PM format without converting to 24-hour format
            String time = String.format("%02d:%02d %s", hours, minutes, ampm);

            // Prepare the SQL update query
            String updateQuery = "UPDATE event SET description = ?, date = ?, time = ?, duration = ? WHERE eventnumber = ?";
            PreparedStatement pstUpdate = conn.c.prepareStatement(updateQuery);
            pstUpdate.setString(1, description);  // Set the event description
            pstUpdate.setString(2, date);  // Set the event date
            pstUpdate.setString(3, time);  // Set the event time (AM/PM format)
            pstUpdate.setString(4, duration);  // Set the event duration
            pstUpdate.setInt(5, Integer.parseInt(eventnumber));  // Set the event number

            // Execute the update query
            int rowsAffected = pstUpdate.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Event Updated Successfully!", "Update Success", JOptionPane.INFORMATION_MESSAGE);
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to update event ( Event no: " + eventnumber + ")", "Success");
                loadAllEvents();  // Refresh the table to show updated event details
            } else {
                JOptionPane.showMessageDialog(null, "Event Update Failed!", "Update Error", JOptionPane.ERROR_MESSAGE);
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to update event ( Event no: " + eventnumber + ")", "Failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while updating event.", "Update Error", JOptionPane.ERROR_MESSAGE);
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to update event ( Event no: " + eventnumber + ")", "Failed");
        }
    }
    
    
    private void deleteEvent() {
        String eventnumber = eventnofield.getText().trim();

        // Validate input
        if (!eventnumber.matches("\\d+") || Integer.parseInt(eventnumber) <= 0) {
            JOptionPane.showMessageDialog(null, "Enter a valid positive integer for Event Number!", "Input Error", JOptionPane.ERROR_MESSAGE);
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to delete event ( Event no: " + eventnumber + ")", "Failed");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this event?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            Conn conn = new Conn();
            String query = "DELETE FROM event WHERE eventnumber = ?";
            PreparedStatement pst = conn.c.prepareStatement(query);
            pst.setInt(1, Integer.parseInt(eventnumber));
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Event Deleted Successfully!", "Delete Success", JOptionPane.INFORMATION_MESSAGE);
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to delete event ( Event no: " + eventnumber + ")", "Success");
                clearFields(); // Also clears the table and reloads all events
            } else {
                JOptionPane.showMessageDialog(null, "Event Not Found!", "Delete Failed", JOptionPane.ERROR_MESSAGE);
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to delete event ( Event no: " + eventnumber + ")", "Not Found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while deleting the event.", "Delete Error", JOptionPane.ERROR_MESSAGE);
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to delete event ( Event no: " + eventnumber + ")", "Failed");
        }
    }
  
    private void clearFields() {
        eventnofield.setText("");
        descripfield.setText("");
        dateChooser.setDate(null);
        hourSpinner.setValue(12);
        minuteSpinner.setValue(0);
        ampmComboBox.setSelectedItem("AM");
        durationHourComboBox.setSelectedIndex(0); // or set to "0 hr" or any default value
        durationMinuteComboBox.setSelectedIndex(0); // or set to "0 min" or any default value

        tableModel.setRowCount(0);
        loadAllEvents();
    }

    public static void main(String[] args) {
        new SearchEvent();
    }
    
      
    private void generateReport() {
        try {

            String timestamp = java.time.LocalDateTime.now().toString().replace(":", "-").replace(".", "-");
            
            File reportsDir = new File("Reports/Events");
            if (!reportsDir.exists()) reportsDir.mkdirs();
            
            String pdfFilePath = "Reports/Events/" + timestamp + ".pdf";
            
            Conn conn = new Conn();
            String query = "SELECT * FROM event";
            ResultSet rs = conn.s.executeQuery(query);
            
                // Create PDF document
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));      
            document.open();

            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,18,com.itextpdf.text.Font.BOLD,new BaseColor(0, 0, 139));
            com.itextpdf.text.Font dateFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.ITALIC, BaseColor.DARK_GRAY);
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
            com.itextpdf.text.Font subHeaderFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD, BaseColor.BLACK);
            com.itextpdf.text.Font dataFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
            com.itextpdf.text.Font footerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.ITALIC, BaseColor.DARK_GRAY);
            
            com.itextpdf.text.Image badge = com.itextpdf.text.Image.getInstance(getClass().getResource("/icons/hicon.PNG"));
            badge.scaleAbsolute(80, 70);
            badge.setAbsolutePosition(70, 750);
            document.add(badge);
            
            Paragraph title = new Paragraph("KINGFISHER RESORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            Paragraph subtitle = new Paragraph("Event Management", subHeaderFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);

            Paragraph gdate = new Paragraph("Generated on: " + java.time.LocalDate.now(), dateFont);
            gdate.setAlignment(Element.ALIGN_CENTER);
            document.add(gdate);
            
            document.add(new Paragraph("\n"));
            
            LineSeparator line2 = new LineSeparator();
            line2.setPercentage(100);
            document.add(line2);
            
            
            
            Paragraph allEmployees = new Paragraph("Event Report", subHeaderFont);
            allEmployees.setAlignment(Element.ALIGN_CENTER);
            document.add(allEmployees);
            
            document.add(new Paragraph("\n"));

                // Data structures to organize report content
            Map<String, Integer> monthlyCountMap = new TreeMap<>(); // Total events per month
            Map<String, Map<String, List<Integer>>> monthlyEventMap = new TreeMap<>(); // Month -> Date -> Event Numbers
            Map<String, Integer> dayCount = new HashMap<>(); // Date -> Count of events
            List<String[]> allData = new ArrayList<>(); // Store full event rows for the table

            int totalDuration = 0, eventCount = 0;
            int maxDuration = Integer.MIN_VALUE, minDuration = Integer.MAX_VALUE;
            String longestEvent = "", shortestEvent = "";

            while (rs.next()) {
                int eventNumber = rs.getInt("eventnumber");
                String description = rs.getString("description");
                String date = rs.getString("date");
                String time = rs.getString("time");
                String durationStr = rs.getString("duration");

                int hours = 0;
                try {
                    hours = Integer.parseInt(durationStr.split(" ")[0]);// Extract the hour value
                } catch (NumberFormatException e) {
                    continue;// Skip invalid data
                }

                totalDuration += hours;
                eventCount++;
                    // Store event row for later table
                allData.add(new String[]{String.valueOf(eventNumber), description, date, time, durationStr});
                
                    // Update longest/shortest event info
                if (hours > maxDuration) {
                    maxDuration = hours;
                    longestEvent = "Event Number" + eventNumber + " on " + date + " at " + time + " (" + durationStr + ")";
                }
                if (hours < minDuration) {
                    minDuration = hours;
                    shortestEvent = "Event Number" + eventNumber + " on " + date + " at " + time + " (" + durationStr + ")";
                }
                
                    // Count events per day
                dayCount.put(date, dayCount.getOrDefault(date, 0) + 1);
                    //per month
                String month = date.substring(0, 7); // yyyy-MM
                monthlyCountMap.put(month, monthlyCountMap.getOrDefault(month, 0) + 1);
                    
                    // Organize event numbers under each month-date
                monthlyEventMap.putIfAbsent(month, new TreeMap<>());
                Map<String, List<Integer>> dateEvents = monthlyEventMap.get(month);
                dateEvents.putIfAbsent(date, new ArrayList<>());
                dateEvents.get(date).add(eventNumber);
            }

            // ==== CHART GENERATION ====
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (Map.Entry<String, Integer> entry : monthlyCountMap.entrySet()) {
                dataset.addValue(entry.getValue(), "Events", entry.getKey());
            }

            JFreeChart chart = ChartFactory.createBarChart(
            "", // Remove main title
            "Month",
            "Number of Events",
            dataset,
            PlotOrientation.VERTICAL,
            false, true, false);

                // Add custom title to the chart
            TextTitle chartTitle = new TextTitle("Monthly Event Distribution", 
                    new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14)); // Font size reduced to 14
            chart.setTitle(chartTitle);

                // Set white background and render to image
            chart.setBackgroundPaint(Color.white);
            BufferedImage chartImage = chart.createBufferedImage(560, 300);
            Image image = Image.getInstance(writer, chartImage, 1.0f);
            image.setAlignment(Element.ALIGN_RIGHT);
            image.setSpacingAfter(10f);
            document.add(image); // Chart is added at the top

                // PDF Title
            Paragraph title1 = new Paragraph("Event Report");
            title1.setAlignment(Element.ALIGN_CENTER);
            title1.setSpacingAfter(20f);
            document.add(title1);

                // Determine the most active day (with most events)
            String mostActiveDay = dayCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(e -> e.getKey() + " (" + e.getValue() + " events)")
                    .orElse("N/A");

            int avgDuration = eventCount > 0 ? totalDuration / eventCount : 0;


            // Summary infographic cards
            PdfPTable summary = new PdfPTable(2);
            summary.setWidths(new int[]{1, 3});
            summary.setWidthPercentage(100);
            summary.setSpacingAfter(20f);

            addCardWithIcon(summary, "📅 Total Events", eventCount + " events", BaseColor.LIGHT_GRAY, BaseColor.BLACK); // Light Gray
            addCardWithIcon(summary, "⏱️ Average Duration", avgDuration + " hrs", WebColors.getRGBColor("#404040"), BaseColor.WHITE); // Dark Gray
            addCardWithIcon(summary, "🔝 Longest Event", longestEvent, BaseColor.LIGHT_GRAY, BaseColor.BLACK); // Light Steel Blue
            addCardWithIcon(summary, "🔽 Shortest Event", shortestEvent, WebColors.getRGBColor("#404040"), BaseColor.WHITE); // Khaki
            addCardWithIcon(summary, "⭐ Most Active Day", mostActiveDay, BaseColor.LIGHT_GRAY, BaseColor.BLACK); // Light Pink


            document.add(summary);

            // Monthly Distribution Table
            Paragraph monthlyTitle = new Paragraph("Monthly Event Distribution Table");
            monthlyTitle.setAlignment(Element.ALIGN_CENTER);
            monthlyTitle.setSpacingAfter(20f);
            document.add(monthlyTitle);

            PdfPTable distTable = new PdfPTable(4);
            distTable.setWidthPercentage(100);
            distTable.setSpacingAfter(10f);
            String[] distHeaders = { "Month", "Total Events", "Date(s)", "Event Number(s)" };
            for (String header : distHeaders) {
                PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
                headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPadding(7);
                distTable.addCell(headerCell);
            }

            for (Map.Entry<String, Map<String, List<Integer>>> monthEntry : monthlyEventMap.entrySet()) {
                String month = monthEntry.getKey();
                Map<String, List<Integer>> dateMap = monthEntry.getValue();
                int monthTotalEvents = dateMap.values().stream().mapToInt(List::size).sum();

                boolean isFirstRow = true;
                for (Map.Entry<String, List<Integer>> dateEntry : dateMap.entrySet()) {
                    String date = dateEntry.getKey();
                    List<Integer> eventList = dateEntry.getValue();
                    String eventNumbers = eventList.stream()
                            .map(String::valueOf)
                            .collect(java.util.stream.Collectors.joining(", "));

                    if (isFirstRow) {
                        distTable.addCell(new PdfPCell(new Phrase(month)));
                        distTable.addCell(new PdfPCell(new Phrase(String.valueOf(monthTotalEvents))));
                        isFirstRow = false;
                    } else {
                        distTable.addCell("");
                        distTable.addCell("");
                    }
                    distTable.addCell(date);
                    distTable.addCell(eventNumbers);
                }
            }

            document.add(distTable);

            // All Events Details Table
            Paragraph eventsTitle = new Paragraph("Event Table");
            eventsTitle.setAlignment(Element.ALIGN_CENTER);
            eventsTitle.setSpacingAfter(20f);
            document.add(eventsTitle);

            PdfPTable eventTable = new PdfPTable(5);
            eventTable.setWidthPercentage(100);
            eventTable.setSpacingBefore(10f);
            String[] headers = { "Event Number", "Description", "Date", "Time", "Duration" };
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.DARK_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(7);
                eventTable.addCell(cell);
            }

            for (String[] row : allData) {
                for (String value : row) {
                    eventTable.addCell(value);
                }
            }
            document.add(eventTable);
            
            document.add(new Paragraph("\n\n"));
            
            
            Paragraph responsibilityParagraph = new Paragraph(
            "This report has been generated by Kingfisher Resort for internal use to review and analyze event-related data and performance metrics. " +
            "The company takes full responsibility for the accuracy and completeness of the information presented herein, which is sourced from our event management records. " +
            "Any discrepancies or updates will be reflected in future reports.",dataFont);
            responsibilityParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(responsibilityParagraph);
            
            document.add(new Paragraph("\n"));
            
            LineSeparator line4 = new LineSeparator();
            line4.setPercentage(100);
            document.add(line4);
            
            document.add(new Paragraph("\n"));

            Paragraph footer = new Paragraph("End of Report", subHeaderFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            
            document.close();
            Desktop.getDesktop().open(new File(pdfFilePath));
            JOptionPane.showMessageDialog(null, "PDF report generated successfully!");
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to genarate event report", "Success");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error generating report!");
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to genarate event report", "Failed");
        }
    }
    
    // Helper method to add infographic-style cards with icons in a "container" format in report
    private void addCardWithIcon(PdfPTable table, String title, String value, BaseColor bgColor, BaseColor fontColor) {
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD, fontColor);

        PdfPCell iconCell = new PdfPCell(new Phrase(title, titleFont));
        iconCell.setBackgroundColor(bgColor);
        iconCell.setPadding(10);
        iconCell.setBorder(Rectangle.BOX);
        iconCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        iconCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(iconCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value));
        valueCell.setPadding(10);
        valueCell.setBackgroundColor(BaseColor.WHITE);
        valueCell.setBorder(Rectangle.BOX);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(valueCell);
    }
    
}
