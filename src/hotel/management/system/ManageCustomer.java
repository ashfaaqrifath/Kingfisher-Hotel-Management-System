package hotel.management.system;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.ChartUtils;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.IOException;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class ManageCustomer extends JFrame implements ActionListener {

    JTextField txtName, txtPhone, txtNIC, txtPrice;
    JButton btnUpdate, btnDelete, btnBack, btnAdd, btnGenerateReport, btnClear;
    JComboBox<String> selectCustomer, selectroomno, selecttype;
    JTable customerTable;
    DefaultTableModel tableModel;

    ManageCustomer() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel heading = new JLabel("CUSTOMER MANAGEMENT");
        heading.setBounds(300, 10, 600, 50);
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setForeground(new Color(30, 30, 30));
        heading.setHorizontalAlignment(JLabel.CENTER);
        add(heading);

        JLabel lblCustomer = new JLabel("Select Customer");
        lblCustomer.setBounds(30, 80, 150, 30);
        lblCustomer.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblCustomer);

        selectCustomer = new JComboBox<>();
        selectCustomer.setBounds(200, 80, 150, 30);
        selectCustomer.setBackground(Color.WHITE);
        selectCustomer.addActionListener(e -> fillDetails());
        add(selectCustomer);

        JLabel lblName = new JLabel("Customer Name");
        lblName.setBounds(30, 135, 160, 30);
        lblName.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(200, 135, 150, 30);
        add(txtName);

        JLabel lblPhone = new JLabel("Phone Number");
        lblPhone.setBounds(30, 185, 120, 30);
        lblPhone.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblPhone);

        txtPhone = new JTextField();
        txtPhone.setBounds(200, 185, 150, 30);
        add(txtPhone);

        JLabel lblNIC = new JLabel("NIC Number");
        lblNIC.setBounds(30, 235, 120, 30);
        lblNIC.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblNIC);

        txtNIC = new JTextField();
        txtNIC.setBounds(200, 235, 150, 30);
        add(txtNIC);

        JLabel lblType = new JLabel("Room Type");
        lblType.setBounds(30, 285, 120, 30);
        lblType.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblType);

        String[] typeOptions = {"Single Bed", "Double Bed"};
        selecttype = new JComboBox<>(typeOptions);
        selecttype.setBounds(200, 285, 150, 30);
        selecttype.setBackground(Color.WHITE);
        selecttype.addActionListener(e -> loadAvailableRooms());
        add(selecttype);

        JLabel lblRoomNumber = new JLabel("Room Number");
        lblRoomNumber.setBounds(30, 335, 120, 30);
        lblRoomNumber.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblRoomNumber);

        selectroomno = new JComboBox<>();
        selectroomno.setBounds(200, 335, 150, 30);
        selectroomno.setBackground(Color.WHITE);
        selectroomno.addActionListener(e -> loadRoomPrice());
        add(selectroomno);

        JLabel lblPrice = new JLabel("Price");
        lblPrice.setBounds(30, 385, 120, 30);
        lblPrice.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblPrice);

        txtPrice = new JTextField();
        txtPrice.setBounds(200, 385, 150, 30);
        txtPrice.setEditable(false);
        add(txtPrice);

        String[] columns = {"Customer ID", "Name", "Phone", "NIC", "Room Type", "Room Number", "Price"};
        tableModel = new DefaultTableModel(columns, 0);
        customerTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBounds(400, 80, 700, 390);
        add(scrollPane);

        btnAdd = new JButton("ADD");
        btnAdd.setBounds(135, 520, 120, 30);
        btnAdd.setBackground(new Color(0, 153, 76));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(this);
        add(btnAdd);

        btnUpdate = new JButton("UPDATE");
        btnUpdate.setBounds(295, 520, 120, 30);
        btnUpdate.setBackground(Color.BLUE);
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(this);
        add(btnUpdate);

        btnDelete = new JButton("DELETE");
        btnDelete.setBounds(455, 520, 120, 30);
        btnDelete.setBackground(Color.RED);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(this);
        add(btnDelete);

        btnGenerateReport = new JButton("REPORT");
        btnGenerateReport.setBounds(615, 520, 120, 30);
        btnGenerateReport.setBackground(new Color(102, 0, 153));
        btnGenerateReport.setForeground(Color.WHITE);
        btnGenerateReport.addActionListener(this);
        add(btnGenerateReport);

        btnClear = new JButton("CLEAR");
        btnClear.setBounds(774, 520, 120, 30);
        btnClear.setBackground(Color.GRAY);
        btnClear.setForeground(Color.WHITE);
        btnClear.addActionListener(this);
        add(btnClear);

        btnBack = new JButton("BACK");
        btnBack.setBounds(935, 520, 120, 30);
        btnBack.setBackground(Color.BLACK);
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(this);
        add(btnBack);

        setBounds(165, 150, 1200, 610);
        setVisible(true);

        loadCustomerNames();
        loadAvailableRooms();
        loadCustomerData();
    }

    private void loadCustomerNames() {
        selectCustomer.removeAllItems();
        try {
            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("SELECT name FROM customers");
            while (rs.next()) {
                selectCustomer.addItem(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAvailableRooms() {
        selectroomno.removeAllItems();
        try {
            Conn c = new Conn();
            String selectedType = (String) selecttype.getSelectedItem();
            String bedType = selectedType.equals("Single Bed") ? "Single Bed" : "Double Bed";
            ResultSet rs = c.s.executeQuery("SELECT roomnumber FROM room WHERE availability = 'Available' AND bed_type = '" + bedType + "'");
            while (rs.next()) {
                selectroomno.addItem(rs.getString("roomnumber"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRoomPrice() {
        String selectedRoom = (String) selectroomno.getSelectedItem();
        if (selectedRoom == null) return;
        try {
            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("SELECT price FROM room WHERE roomnumber = '" + selectedRoom + "'");
            if (rs.next()) {
                txtPrice.setText(rs.getString("price"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillDetails() {
        String selectedName = (String) selectCustomer.getSelectedItem();
        if (selectedName == null) return;
        try {
            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("SELECT * FROM customers WHERE name = '" + selectedName + "'");
            if (rs.next()) {
                txtName.setText(rs.getString("name"));
                txtPhone.setText(rs.getString("phone"));
                txtNIC.setText(rs.getString("NIC"));
                selecttype.setSelectedItem(rs.getString("room_type"));
                selectroomno.setSelectedItem(rs.getString("room_number"));
                txtPrice.setText(rs.getString("room_price"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCustomerData() {
        tableModel.setRowCount(0);
        try {
            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("SELECT * FROM customers");
            while (rs.next()) {
                String[] row = {
                    rs.getString("customer_id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("NIC"),
                    rs.getString("room_type"),
                    rs.getString("room_number"),
                    rs.getString("room_price")
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
    if (ae.getSource() == btnAdd || ae.getSource() == btnUpdate) {

        String name = txtName.getText();
        String phone = txtPhone.getText();
        String nic = txtNIC.getText();
        String type = (String) selecttype.getSelectedItem();
        String roomNumber = (String) selectroomno.getSelectedItem();
        String price = txtPrice.getText();

        if (name.isEmpty() || phone.isEmpty() || nic.isEmpty() || roomNumber == null || price.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add or update customer "+name+"( " + nic + ")", "Failed");
            return;
        }
        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Phone number must be exactly 10 digits.");
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add or update customer "+name+"( " + nic + ")", "Failed");
            return;
        }
        if (!(nic.matches("\\d{9}[vVxX]") || nic.matches("\\d{12}"))) {
            JOptionPane.showMessageDialog(this, "Invalid NIC Number.");
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add or update customer "+name+"( " + nic + ")", "Failed");
            return;
        }

        try {
            Conn c = new Conn();

            if (ae.getSource() == btnAdd) {
                
                ResultSet rs = c.s.executeQuery("SELECT * FROM customers WHERE NIC = '" + nic + "'");
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "A customer with this NIC already exists.");
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add duplicate customer " + name + " (" + nic + ")", "Failed");
                    return;
                }
                
                String query = "INSERT INTO customers(name, phone, NIC, room_type, room_number, room_price) "
                        + "VALUES('" + name + "', '" + phone + "', '" + nic + "', '" + type + "', '" + roomNumber + "', '" + price + "')";
                c.s.executeUpdate(query);
                c.s.executeUpdate("UPDATE room SET availability = 'Not yet' WHERE roomnumber = '" + roomNumber + "'");
                JOptionPane.showMessageDialog(null, "Customer Added Successfully");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add customer "+name+"( " + nic + ")", "Success");

            } else if (ae.getSource() == btnUpdate) {
                String oldName = (String) selectCustomer.getSelectedItem();
                String query = "UPDATE customers SET name = '" + name + "', phone = '" + phone + "', NIC = '" + nic
                        + "', room_type = '" + type + "', room_number = '" + roomNumber + "', room_price = '" + price
                        + "' WHERE name = '" + oldName + "'";
                c.s.executeUpdate(query);
                JOptionPane.showMessageDialog(null, "Customer Updated Successfully");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to update customer "+name+"( " + nic + ")", "Success");
            }

            loadCustomerNames();
            loadAvailableRooms();
            loadCustomerData();

        } catch (Exception e) {
            e.printStackTrace();
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add or update customer "+name+"( " + nic + ")", "Failed");
        }

    } else if (ae.getSource() == btnDelete) {
        try {
            Conn c = new Conn();
            
            String selectedName = (String) selectCustomer.getSelectedItem();
                
                if (selectedName == null || selectedName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Select customer to delete!");
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to delete customer without selection", "Failed");
                    return;
                }
            
            int response = JOptionPane.showConfirmDialog(this, "Delete this customer?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                
                c.s.executeUpdate("DELETE FROM customers WHERE name = '" + selectedName + "'");
                JOptionPane.showMessageDialog(null, "Customer Deleted Successfully");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to delete customer "+selectedName+".", "Success");

                loadCustomerNames();
                loadAvailableRooms();
                loadCustomerData();
            }
        } catch (Exception e) {
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to delete customer.", "Failed");
            e.printStackTrace();
        }
    } else if (ae.getSource() == btnClear) {
        txtName.setText("");
        txtPhone.setText("");
        txtNIC.setText("");
        selecttype.setSelectedIndex(0);
        selectroomno.removeAllItems();
        txtPrice.setText("");
        selectCustomer.setSelectedIndex(-1);

    } else if (ae.getSource() == btnBack) {
        setVisible(false);
    } else if (ae.getSource() == btnGenerateReport) {
        generatePDF();
    }
}

    private void generatePDF() {
        try {
            String timestamp = java.time.LocalDateTime.now().toString().replace(":", "-").replace(".", "-");
            
            File reportsDir = new File("Reports/Customers");
            if (!reportsDir.exists()) reportsDir.mkdirs();
            
            String pdfFilePath = "Reports/Customers/" + timestamp + ".pdf";
            
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
            
            Paragraph subtitle = new Paragraph("Customer Management", subHeaderFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);

            Paragraph date = new Paragraph("Generated on: " + java.time.LocalDate.now(), dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);
            
            document.add(new Paragraph("\n"));
            
            LineSeparator line2 = new LineSeparator();
            line2.setPercentage(100);
            document.add(line2);
            
            Paragraph allEmployees = new Paragraph("All Customers Details", subHeaderFont);
            allEmployees.setAlignment(Element.ALIGN_CENTER);
            document.add(allEmployees);

            document.add(new Paragraph("\n"));

            Conn c = new Conn();

            PdfPTable customerTable = new PdfPTable(6);
            customerTable.setWidthPercentage(100);

            String[] headers = {"Customer ID", "Name", "Phone", "NIC", "Room Number", "Price"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.DARK_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(7);
                customerTable.addCell(cell);
            }

            ResultSet rs = c.s.executeQuery("SELECT * FROM customers");
            while (rs.next()) {
                customerTable.addCell(rs.getString("customer_id"));
                customerTable.addCell(rs.getString("name"));
                customerTable.addCell(rs.getString("phone"));
                customerTable.addCell(rs.getString("NIC"));
                customerTable.addCell(rs.getString("room_number"));
                customerTable.addCell(rs.getString("room_price"));
            }
            document.add(customerTable);
            
            Paragraph rev = new Paragraph("Revenue Details", subHeaderFont);
            rev.setAlignment(Element.ALIGN_CENTER);
            document.add(rev);

            document.add(new Paragraph("\n"));
            
            ResultSet revenueRS = c.s.executeQuery("SELECT room_type, COUNT(*) AS total_customers, SUM(room_price) AS total_revenue FROM customers GROUP BY room_type UNION ALL SELECT 'Total', COUNT(*), SUM(room_price) FROM customers");
            
            PdfPTable revenueTable = new PdfPTable(4);
            revenueTable.setWidthPercentage(100);

            String[] revenueHeaders = {"Room Type", "Total Customers", "Total Revenue (LKR)", "Room Price (Avg.)"};
                for (String header : revenueHeaders) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                    cell.setBackgroundColor(BaseColor.DARK_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(7);
                    revenueTable.addCell(cell);
                }

            while (revenueRS.next()) {
                String roomType = revenueRS.getString("room_type");
                String totalCustomers = revenueRS.getString("total_customers");
                String totalRevenue = revenueRS.getString("total_revenue");

                double averagePrice = Double.parseDouble(totalRevenue) / Integer.parseInt(totalCustomers);
                String averagePriceString = String.format("%.2f", averagePrice);

                revenueTable.addCell(roomType);
                revenueTable.addCell(totalCustomers);
                revenueTable.addCell(totalRevenue);
                revenueTable.addCell(averagePriceString);
            }

        document.add(revenueTable);

            document.add(new Paragraph("\n"));
            
            DefaultPieDataset dataset = new DefaultPieDataset();
            ResultSet pieData = c.s.executeQuery("SELECT room_type, COUNT(*) AS count FROM customers GROUP BY room_type");
            while (pieData.next()) {
                dataset.setValue(pieData.getString("room_type"), pieData.getInt("count"));
            }

            JFreeChart chart = ChartFactory.createPieChart(
                "Customer Distribution by Room Type", dataset, true, true, false);

            int width = 500;
            int height = 400;
            File chartFile = new File("RoomTypeChart.png");
            ChartUtilities.saveChartAsPNG(chartFile, chart, width, height);

            Image chartImage = Image.getInstance(chartFile.getAbsolutePath());
            chartImage.scaleAbsolute(width, height);
            chartImage.setAlignment(Element.ALIGN_CENTER);
            document.add(chartImage);

            document.add(new Paragraph("\n\n"));
            
            Paragraph responsibilityParagraph = new Paragraph(
            "This report has been generated by Kingfisher Resort to provide an overview of customer demographics and other key service metrics. The company ensures the accuracy and completeness of the data presented, which is sourced from our customer management system. Any discrepancies or updates in the data will be addressed in future reports.",dataFont);
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
            
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(new File(pdfFilePath));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Cannot open generated PDF automatically.");
                }
            }
            
            JOptionPane.showMessageDialog(this, "PDF Report generated successfully");
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to genarate customer report", "Success");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating PDF: " + e.getMessage());
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to genarate customer report", "Failed");
        }
    }

    public static void main(String[] args) {
        new ManageCustomer();
        
    }
}


