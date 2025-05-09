package hotel.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.toedter.calendar.JDateChooser;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class activityLog extends JFrame implements ActionListener {

    JTable activityTable;
    DefaultTableModel tableModel;
    JTextField nicField;
    JComboBox<String> roleBox;
    JDateChooser dateChooser;
    JButton searchBtn, viewAllBtn, reportBtn, btnback;

    public activityLog() {
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JLabel heading = new JLabel("ACTIVITY LOG");
        heading.setBounds(300, 10, 600, 50);
        heading.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 30));
        heading.setForeground(new Color(30, 30, 40));
        heading.setHorizontalAlignment(JLabel.CENTER);
        add(heading);

        JLabel nicLabel = new JLabel("Enter NIC:");
        nicLabel.setBounds(40, 140, 120, 30);
        nicLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 17));
        add(nicLabel);

        nicField = new JTextField();
        nicField.setBounds(130, 140, 150, 30);
        add(nicField);

        JLabel roleLabel = new JLabel("Job Role:");
        roleLabel.setBounds(40, 360, 120, 30);
        roleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 17));
        add(roleLabel);

        roleBox = new JComboBox<>(new String[]{"All", "Admin", "Receptionist"});
        roleBox.setBounds(130, 360, 150, 30);
        add(roleBox);

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setBounds(40, 250, 120, 30);
        dateLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 17));
        add(dateLabel);

        dateChooser = new JDateChooser();
        dateChooser.setBounds(130, 250, 150, 30);
        add(dateChooser);
        
        
        searchBtn = new JButton("SEARCH");
        searchBtn.setBounds(290, 500, 150, 30);
        searchBtn.setBackground(Color.BLUE);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.addActionListener(this);
        add(searchBtn);
        
        viewAllBtn = new JButton("VIEW ALL");
        viewAllBtn.setBackground(new Color(0, 128, 128));
        viewAllBtn.setForeground(Color.WHITE);
        viewAllBtn.setBounds(460, 500, 150, 30);
        viewAllBtn.addActionListener(this);
        add(viewAllBtn);

        reportBtn = new JButton("REPORT");
        reportBtn.setBackground(new Color(102, 0, 153));
        reportBtn.setForeground(Color.WHITE);
        reportBtn.setBounds(630, 500, 150, 30); 
        reportBtn.addActionListener(this);
        add(reportBtn);
        
        btnback = new JButton("BACK");
        btnback.setBackground(Color.BLACK);
        btnback.setForeground(Color.WHITE);
        btnback.setBounds(800, 500, 150, 30);
        btnback.addActionListener(this);
        add(btnback);

        String[] columnNames = {"NIC", "Name", "Job Role", "Activity", "Result", "Date|Time"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        activityTable = new JTable(tableModel);
        
        activityTable.getColumnModel().getColumn(0).setPreferredWidth(100); // NIC
        activityTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        activityTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Job Role
        activityTable.getColumnModel().getColumn(3).setPreferredWidth(450); // Activity
        activityTable.getColumnModel().getColumn(4).setPreferredWidth(80); // Result
        activityTable.getColumnModel().getColumn(5).setPreferredWidth(150); // Time
        
        JScrollPane tableScrollPane = new JScrollPane(activityTable);
        tableScrollPane.setBounds(300, 90, 850, 350);
        add(tableScrollPane);

        loadActivityLogData();
        
        setBounds(165, 150, 1200, 610);
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent ae) {
    if (ae.getSource() == searchBtn) {
        searchActivity();
    } else if (ae.getSource() == viewAllBtn) {
        loadActivityLogData();
    } else if (ae.getSource() == reportBtn) {
        generatePDFReport();
    } else if (ae.getSource() == btnback) {
        setVisible(false);
    }
}
    

    public void loadActivityLogData() {
        tableModel.setRowCount(0);
        try {
            Conn conn = new Conn();
            ResultSet rs = conn.s.executeQuery("SELECT * FROM activitylog");
            while (rs.next()) {
                addRowToTable(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchActivity() {
        tableModel.setRowCount(0);
        try {
            Conn conn = new Conn();
            StringBuilder query = new StringBuilder("SELECT * FROM activitylog WHERE 1=1");

            String nic = nicField.getText().trim();
            if (!nic.isEmpty()) {
                query.append(" AND nic = '").append(nic).append("'");
            }

            String role = roleBox.getSelectedItem().toString();
            if (!role.equals("All")) {
                query.append(" AND jobrole = '").append(role).append("'");
            }

            Date selectedDate = dateChooser.getDate();
            if (selectedDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateStr = sdf.format(selectedDate);
                query.append(" AND DATE(activity_time) = '").append(dateStr).append("'");
            }

            ResultSet rs = conn.s.executeQuery(query.toString());
            while (rs.next()) {
                addRowToTable(rs);
            }
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to search in activity log", "Success");

        } catch (Exception e) {
            e.printStackTrace();
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to search in activity log", "Failed");
        }
    }

    private void addRowToTable(ResultSet rs) throws SQLException {
        String nic = rs.getString("nic");
        String name = rs.getString("name");
        String jobrole = rs.getString("jobrole");
        String activity = rs.getString("activity");
        String result = rs.getString("result");
        Timestamp time = rs.getTimestamp("activity_time");
        Object[] row = {nic, name, jobrole, activity, result, time.toString()};
        tableModel.addRow(row);
    }

    public void generatePDFReport() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to generate report.");
            return;
        }

        try {
            String timestamp = java.time.LocalDateTime.now().toString().replace(":", "-").replace(".", "-");
            
            File reportsDir = new File("Reports/Log Activities");
            if (!reportsDir.exists()) reportsDir.mkdirs();
            
            String pdfFilePath = "Reports/Log Activities/" + timestamp + ".pdf";
            
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
            document.open();

            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,18,com.itextpdf.text.Font.BOLD,new BaseColor(0, 0, 139));
            com.itextpdf.text.Font dateFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.ITALIC, BaseColor.DARK_GRAY);
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
            
            Paragraph subtitle = new Paragraph("System Activity Report", subHeaderFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);

            Paragraph date = new Paragraph("Generated on: " + java.time.LocalDate.now(), dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);
            
            document.add(new Paragraph("\n"));
            
            LineSeparator line2 = new LineSeparator();
            line2.setPercentage(100);
            document.add(line2);
            
            Paragraph allEmployees = new Paragraph("Activity Log", subHeaderFont);
            allEmployees.setAlignment(Element.ALIGN_CENTER);
            document.add(allEmployees);
            
            document.add(new Paragraph("\n"));
            
            PdfPTable pdfTable = new PdfPTable(tableModel.getColumnCount());
            pdfTable.setWidthPercentage(100);

            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(tableModel.getColumnName(i),headerFont));
                cell.setBackgroundColor(BaseColor.DARK_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(7);
                pdfTable.addCell(cell);
            }

            int activityColIndex = -1;
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                if (tableModel.getColumnName(i).equalsIgnoreCase("Activity")) {
                    activityColIndex = i;
                    break;
                }
            }

            for (int row = 0; row < tableModel.getRowCount(); row++) {
                String activityValue = (activityColIndex != -1) ? tableModel.getValueAt(row, activityColIndex).toString() : "";
                BaseColor rowColor = BaseColor.WHITE; // default

                if ("Login".equalsIgnoreCase(activityValue)) {
                    rowColor = new BaseColor(200, 255, 200);
                } else if ("Logged out".equalsIgnoreCase(activityValue)) {
                    rowColor = new BaseColor(255, 200, 200);
                }

                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    PdfPCell cell = new PdfPCell(new Phrase(tableModel.getValueAt(row, col).toString(), dataFont));
                    cell.setBackgroundColor(rowColor);
                    cell.setPadding(5);
                    pdfTable.addCell(cell);
                }
            }

            document.add(pdfTable);
            
             document.add(new Paragraph("\n\n"));
            
            
            Paragraph responsibilityParagraph = new Paragraph(
            "This Activity Log Entry Report has been generated by Kingfisher Resort for internal use to monitor and evaluate employee system interactions and operational activities. The resort assumes full responsibility for the accuracy and completeness of the activity data presented, as sourced from the employee management system. Any discrepancies or updates will be addressed and reflected in subsequent reports.",dataFont);
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

            JOptionPane.showMessageDialog(null, "PDF Report generated successfully!");
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to generate activity log report", "Success");
            
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(new File(pdfFilePath));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Cannot open generated PDF automatically.");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error generating PDF report.");
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to generate activity log report", "Success");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new activityLog();
    }
}
