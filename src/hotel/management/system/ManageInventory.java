/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hotel.management.system;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.HashMap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;

import org.jfree.data.general.*;
import javax.imageio.ImageIO;
import java.io.*;


/**
 *
 * @author ashfq
 */
public class ManageInventory extends JFrame implements ActionListener {

    JTextField txtQuantity, txtPrice, txtNameEdit, txtSupplierName, txtSupplierPhone;
    JButton btnUpdate, btnDelete, btnBack, btnAdd, btnReport, btnClear, btnAdd2;
    JComboBox<String> selectItemName, selectCategory;
    JTable inventoryTable;
    DefaultTableModel tableView;
    JRadioButton btnKilo, btnGrams, btnUnits;
    ButtonGroup quantityBtns;
    
   

    ManageInventory() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel heading = new JLabel("INVENTORY MANAGEMENT");
        heading.setBounds(300, 10, 600, 50);
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setForeground(new Color(30, 30, 30));
        heading.setHorizontalAlignment(JLabel.CENTER);
        add(heading);

        JLabel lblItemName = new JLabel("Select Item");
        lblItemName.setBounds(30, 80, 120, 30);
        lblItemName.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblItemName);

        selectItemName = new JComboBox<>();
        selectItemName.setBounds(200, 80, 150, 30);
        selectItemName.setBackground(Color.WHITE);
        selectItemName.addActionListener(e -> fillDetails());
        add(selectItemName);

        JLabel lblItemNameEdit = new JLabel("Item Name");
        lblItemNameEdit.setBounds(30, 130, 160, 30);
        lblItemNameEdit.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblItemNameEdit);

        txtNameEdit = new JTextField();
        txtNameEdit.setBounds(200, 130, 150, 30);
        add(txtNameEdit);

        JLabel lblCategory = new JLabel("Category");
        lblCategory.setBounds(30, 180, 120, 30);
        lblCategory.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblCategory);

        String[] categories = { "Food", "Accessories", "Supplies" };
        selectCategory = new JComboBox<>(categories);
        selectCategory.setBounds(200, 180, 150, 30);
        selectCategory.setBackground(Color.WHITE);
        add(selectCategory);

        JLabel lblQuantity = new JLabel("Quantity");
        lblQuantity.setBounds(30, 230, 120, 30);
        lblQuantity.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblQuantity);

        txtQuantity = new JTextField();
        txtQuantity.setBounds(200, 230, 150, 30);
        add(txtQuantity);

        btnKilo = new JRadioButton("kg");
        btnGrams = new JRadioButton("g");
        btnUnits = new JRadioButton("Units");

        btnKilo.setBounds(150, 265, 60, 30);
        btnGrams.setBounds(220, 265, 80, 30);
        btnUnits.setBounds(300, 265, 70, 30);

        btnKilo.setBackground(Color.WHITE);
        btnGrams.setBackground(Color.WHITE);
        btnUnits.setBackground(Color.WHITE);

        quantityBtns = new ButtonGroup();
        quantityBtns.add(btnKilo);
        quantityBtns.add(btnGrams);
        quantityBtns.add(btnUnits);

        add(btnKilo);
        add(btnGrams);
        add(btnUnits);

        JLabel lblPrice = new JLabel("Price");
        lblPrice.setBounds(30, 310, 120, 30);
        lblPrice.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblPrice);

        txtPrice = new JTextField();
        txtPrice.setBounds(200, 310, 150, 30);
        add(txtPrice);

        JLabel lblSupplierName = new JLabel("Supplier Name");
        lblSupplierName.setBounds(30, 360, 120, 30);
        lblSupplierName.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblSupplierName);

        txtSupplierName = new JTextField();
        txtSupplierName.setBounds(200, 360, 150, 30);
        add(txtSupplierName);

        JLabel lblSupplierPhone = new JLabel("Supplier Phone");
        lblSupplierPhone.setBounds(30, 410, 120, 30);
        lblSupplierPhone.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblSupplierPhone);

        txtSupplierPhone = new JTextField();
        txtSupplierPhone.setBounds(200, 410, 150, 30);
        add(txtSupplierPhone);

        String[] columns = { "Item ID", "Item Name", "Category", "Quantity", "Price/unit(Rs)", "Supplier Phone", "Date Added" };
        tableView = new DefaultTableModel(columns, 0);
        inventoryTable = new JTable(tableView);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBounds(400, 80, 700, 390);
        add(scrollPane);
        
        
        btnAdd = new JButton("ADD");
        btnAdd.setBackground(new Color(0, 153, 76));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setBounds(140, 520, 120, 30);
        btnAdd.addActionListener(this);
        add(btnAdd);
        

        btnAdd2 = new JButton("BATCH");
        btnAdd2.setBackground(new Color(0, 128, 128));
        btnAdd2.setForeground(Color.WHITE);
        btnAdd2.setBounds(280, 520, 120, 30);
        btnAdd2.addActionListener(this);
        add(btnAdd2);

        btnUpdate = new JButton("UPDATE");
        btnUpdate.setBackground(new Color(255, 165, 0));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setBounds(420, 520, 120, 30);
        btnUpdate.addActionListener(this);
        add(btnUpdate);

        btnDelete = new JButton("DELETE");
        btnDelete.setBackground(Color.RED);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setBounds(560, 520, 120, 30);
        btnDelete.addActionListener(this);
        add(btnDelete);

        btnReport = new JButton("REPORT");
        btnReport.setBackground(new Color(102, 0, 153));
        btnReport.setForeground(Color.WHITE);
        btnReport.setBounds(700, 520, 120, 30);
        btnReport.addActionListener(this);
        add(btnReport);

        btnClear = new JButton("CLEAR");
        btnClear.setBackground(Color.GRAY);
        btnClear.setForeground(Color.WHITE);
        btnClear.setBounds(840, 520, 120, 30);
        btnClear.addActionListener(this);
        add(btnClear);

        btnBack = new JButton("BACK");
        btnBack.setBackground(Color.BLACK);
        btnBack.setForeground(Color.WHITE);
        btnBack.setBounds(980, 520, 120, 30);
        btnBack.addActionListener(this);
        add(btnBack);

        setBounds(165, 150, 1200, 610); 
        setVisible(true);

        loadItemNames();
        loadInventoryData();
        clearFields();
    }


    
    
   
    private String formatQuantity() {
        
        String qty = txtQuantity.getText();
        if (btnGrams.isSelected()) {
            return qty + "g";
        }
        
        else if (btnKilo.isSelected()) {
            return qty + "kg";
        }
        
        else {
            return qty;
        }
    }

    private void loadItemNames() {
        selectItemName.removeAllItems();
        try {
            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("SELECT DISTINCT item_name FROM inventory");
            while (rs.next()) {
                selectItemName.addItem(rs.getString("item_name"));
            }
            selectItemName.setSelectedIndex(-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillDetails() {
        String selectedItem = (String) selectItemName.getSelectedItem();
        if (selectedItem == null) return;

        try {
            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("SELECT * FROM inventory WHERE item_name = '" + selectedItem + "'");
            
            if (rs.next()) {
                
                txtNameEdit.setText(rs.getString("item_name"));
                String quantity = rs.getString("quantity").toLowerCase();

                if (quantity.endsWith("kg")) {
                    txtQuantity.setText(quantity.replace("kg", ""));
                    btnKilo.setSelected(true);
                }
                
                else if (quantity.endsWith("g")) {
                    txtQuantity.setText(quantity.replace("g", ""));
                    btnGrams.setSelected(true);
                }
                
                else {
                    txtQuantity.setText(quantity.replaceAll("[^\\d]", ""));
                    btnUnits.setSelected(true);
                }

                txtPrice.setText(rs.getString("price"));
                selectCategory.setSelectedItem(rs.getString("category"));
                txtSupplierName.setText(rs.getString("supplier_name"));
                txtSupplierPhone.setText(rs.getString("supplier_phone"));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadInventoryData() {
        
        tableView.setRowCount(0);
        
        try {
            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("SELECT * FROM inventory");
            while (rs.next()) {
                String[] row = {
                    rs.getString("item_id"),
                    rs.getString("item_name"),
                    rs.getString("category"),
                    rs.getString("quantity"),
                    rs.getString("price"),
                    rs.getString("supplier_phone"),
                    rs.getString("date_added")
                };
                tableView.addRow(row);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtNameEdit.setText("");
        txtQuantity.setText("");
        txtPrice.setText("");
        txtSupplierName.setText("");
        txtSupplierPhone.setText("");
        selectCategory.setSelectedIndex(0);
        selectItemName.setSelectedIndex(-1);
        btnGrams.setSelected(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnBack) {
            setVisible(false);
            return;
        }

        if (ae.getSource() == btnReport) {
            generateReport();
            return;
        }
        
        
        
        if (ae.getSource() == btnAdd2) {
    
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                try (BufferedReader reader = new BufferedReader(new FileReader(file));
                     Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotelmanagementsystem", "root", "1234");
                     PreparedStatement stmt = conn.prepareStatement("INSERT INTO inventory (item_name, category, quantity, price, supplier_name, supplier_phone, date_added, orders) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)")) {

                    String line;
                    conn.setAutoCommit(false);

                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length == 7) {
                            stmt.setString(1, parts[0].trim());
                            stmt.setString(2, parts[1].trim());
                            stmt.setString(3, parts[2].trim());

                            try {
                                stmt.setInt(4, Integer.parseInt(parts[3].trim()));
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(null, "Invalid price format: " + parts[3]);
                                continue; 
                            }

                            stmt.setString(5, parts[4].trim());
                            stmt.setString(6, parts[5].trim());
                            stmt.setInt(7, Integer.parseInt(parts[6].trim()));
                            stmt.addBatch();
                        }
                    }

                    stmt.executeBatch();
                    conn.commit();
                    JOptionPane.showMessageDialog(null, "Items added");
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new item through batch insert.", "Success");
                    loadInventoryData();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new item through batch insert.", "Failed");
                }
            }
        }
        
        
        
        
        

        String itemName = txtNameEdit.getText();
        String quantityRaw = txtQuantity.getText();
        String quantity = formatQuantity();
        String price = txtPrice.getText();
        String category = (String) selectCategory.getSelectedItem();
        String supplierName = txtSupplierName.getText();
        String supplierPhone = txtSupplierPhone.getText();

        if (itemName.isEmpty() || itemName.length() > 20 ||
            quantityRaw.isEmpty() || !quantityRaw.matches("\\d{1,6}") ||
            price.isEmpty() || !price.matches("\\d+(\\.\\d{1,2})?") ||
            supplierName.isEmpty() || supplierName.length() > 20 ||
            !supplierPhone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(null, "All fields must be filled correctly");
            return;
        }

        if (ae.getSource() == btnAdd) {
            try {
                Conn c = new Conn();

                
                String checkQuery = "SELECT COUNT(*) FROM inventory WHERE item_name = '" + itemName + "'";
                ResultSet rs = c.s.executeQuery(checkQuery);
                rs.next();
                int count = rs.getInt(1);

                if (count > 0) {
                    JOptionPane.showMessageDialog(null, "Item already exists.");
                    return;
                }

                
                String query = "INSERT INTO inventory(item_name, category, quantity, price, supplier_name, supplier_phone) " +
                        "VALUES('" + itemName + "', '" + category + "', '" + quantity + "', " + price + ", '" +
                        supplierName + "', '" + supplierPhone + "')";
                c.s.executeUpdate(query);
                JOptionPane.showMessageDialog(null, "Item Added Successfully");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new item: " + itemName + " (" + category + ")", "Success");
                loadItemNames();
                loadInventoryData();
                selectItemName.setSelectedItem(itemName);

            } catch (Exception e) {
                e.printStackTrace();
                 ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new item: " + itemName + " (" + category + ")", "Failed");
            }
        }
        
        
        
      
        
        
        

        
        else if (ae.getSource() == btnUpdate) {
            
            String oldItemName = (String) selectItemName.getSelectedItem();
            
            try {
                Conn c = new Conn();
                String query = "UPDATE inventory SET item_name = '" + itemName + "', quantity = '" + quantity +
                        "', price = " + price + ", category = '" + category + "', supplier_name = '" + supplierName +
                        "', supplier_phone = '" + supplierPhone + "' WHERE item_name = '" + oldItemName + "'";
                c.s.executeUpdate(query);
                JOptionPane.showMessageDialog(null, "Item Updated Successfully");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to update item: " + itemName + " (" + category + ")", "Success");
                loadItemNames();
                loadInventoryData();
                selectItemName.setSelectedItem(itemName);
            }
            
            catch (Exception e) {
                e.printStackTrace();
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to update item: " + itemName + " (" + category + ")", "Failed");
            }
            
        }
        
        else if (ae.getSource() == btnDelete) {
            
            int response = JOptionPane.showConfirmDialog(null, "Delete this item?", "Delete Item", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                try {
                    Conn c = new Conn();
                    c.s.executeUpdate("DELETE FROM inventory WHERE item_name = '" + selectItemName.getSelectedItem() + "'");
                    JOptionPane.showMessageDialog(null, "Item Deleted Successfully");
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to delete item: " + itemName + " (" + category + ")", "Success");
                    loadItemNames();
                    loadInventoryData();
                    clearFields();
                } catch (Exception e) {
                    e.printStackTrace();
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to delete item: " + itemName + " (" + category + ")", "Failed");
                }
            }
        }
        
        else if (ae.getSource() == btnClear) {
            clearFields();
        }



}

    public void generateReport() {
        try {
            String timestamp = java.time.LocalDateTime.now().toString().replace(":", "-").replace(".", "-");
            File reportsDir = new File("Reports/Inventory");

            if (!reportsDir.exists()) reportsDir.mkdir();
            Document document = new Document();
            String pdfFilePath = "Reports/Inventory/" + timestamp + ".pdf";
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

                Paragraph subtitle = new Paragraph("Inventory Management", subHeaderFont);
                subtitle.setAlignment(Element.ALIGN_CENTER);
                document.add(subtitle);

                Paragraph date = new Paragraph("Generated on: " + java.time.LocalDate.now(), dateFont);
                date.setAlignment(Element.ALIGN_CENTER);
                document.add(date);

                document.add(new Paragraph("\n"));

                LineSeparator line2 = new LineSeparator();
                line2.setPercentage(100);
                document.add(line2);

                Paragraph allEmployees = new Paragraph("All Inventory Details", subHeaderFont);
                allEmployees.setAlignment(Element.ALIGN_CENTER);
                document.add(allEmployees);

                document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            String[] headers = { "Item ID", "Item Name", "Category", "Quantity", "Price/unit(Rs)", "Date Added", "Supplier Name", "Supplier Phone" };
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                    cell.setBackgroundColor(BaseColor.DARK_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(7);
                    table.addCell(cell);
            }

            DefaultCategoryDataset quantityDataset = new DefaultCategoryDataset();
            DefaultCategoryDataset ordersDataset = new DefaultCategoryDataset();
            DefaultCategoryDataset valueDataset = new DefaultCategoryDataset();
            DefaultCategoryDataset priceDataset = new DefaultCategoryDataset();
            DefaultCategoryDataset categoryValueDataset = new DefaultCategoryDataset();
            

            Map<String, Integer> categoryCount = new HashMap<>();
            Map<String, Double> categoryTotalPrice = new HashMap<>();
            double totalInventoryValue = 0;
            int totalItems = 0;
            int totalSuppliers = 0;
            double totalPrice = 0;
            String lastSupplier = "";

            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("SELECT * FROM inventory");
            while (rs.next()) {
                String itemId = rs.getString("item_id");
                String itemName = rs.getString("item_name");
                String category = rs.getString("category");
                String quantityRaw = rs.getString("quantity");
                String priceRaw = rs.getString("price");
                String date_added = rs.getString("date_added");
                String supplierName = rs.getString("supplier_name");
                String supplierPhone = rs.getString("supplier_phone");
                int orders = rs.getInt("orders");

                double quantityVal = parseQuantity(quantityRaw);
                double priceVal = Double.parseDouble(priceRaw);

                table.addCell(new Phrase(itemId, dataFont));
                table.addCell(new Phrase(itemName, dataFont));
                table.addCell(new Phrase(category, dataFont));
                table.addCell(new Phrase(quantityRaw, dataFont));
                table.addCell(new Phrase(priceRaw, dataFont));
                table.addCell(new Phrase(date_added, dataFont));
                table.addCell(new Phrase(supplierName, dataFont));
                table.addCell(new Phrase(supplierPhone, dataFont));

                quantityDataset.addValue(quantityVal, "Stock Remaining", itemName);
                ordersDataset.addValue(orders, "Orders", itemName);
                valueDataset.addValue(quantityVal * priceVal, "Inventory Value", itemName);
                priceDataset.addValue(priceVal, "Price", itemName);

                totalInventoryValue += quantityVal * priceVal;
                totalPrice += priceVal;
                totalItems++;
                if (!supplierName.equals(lastSupplier)) {
                    totalSuppliers++;
                    lastSupplier = supplierName;
                }

                categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
                categoryTotalPrice.put(category, categoryTotalPrice.getOrDefault(category, 0.0) + priceVal);
            }

            document.add(table);
            document.add(new Paragraph("\n"));

            Paragraph stats = new Paragraph("Total Inventory Items: " + totalItems + "    |    Total Suppliers: " + totalSuppliers, dataFont);
            stats.setAlignment(Element.ALIGN_CENTER);
            document.add(stats);
            document.add(new Paragraph("\n"));

            JFreeChart ordersChart = ChartFactory.createBarChart("Orders Per Item", "Item Name", "Orders", ordersDataset, PlotOrientation.VERTICAL, true, true, false);
            ordersChart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2));
            BufferedImage ordersImage = ordersChart.createBufferedImage(500, 300);
            File ordersChartFile = new File("Reports/assets/ordersChart.png");
            ImageIO.write(ordersImage, "png", ordersChartFile);
            com.itextpdf.text.Image ordersChartImg = com.itextpdf.text.Image.getInstance("Reports/assets/ordersChart.png");
            ordersChartImg.setAlignment(Element.ALIGN_CENTER);
            document.add(ordersChartImg);
            document.add(new Paragraph("\n"));

            JFreeChart quantityChart = ChartFactory.createBarChart("Stock Remaining Per Item", "Item Name", "Quantity", quantityDataset, PlotOrientation.VERTICAL, true, true, false);
            quantityChart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2));
            BufferedImage quantityImage = quantityChart.createBufferedImage(500, 300);
            File quantityChartFile = new File("Reports/assets/quantityChart.png");
            ImageIO.write(quantityImage, "png", quantityChartFile);
            com.itextpdf.text.Image quantityChartImg = com.itextpdf.text.Image.getInstance("Reports/assets/quantityChart.png");
            quantityChartImg.setAlignment(Element.ALIGN_CENTER);
            document.add(quantityChartImg);
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));

            JFreeChart priceChart = ChartFactory.createBarChart("Average Price Per Item", "Item Name", "Price", priceDataset, PlotOrientation.VERTICAL, true, true, false);
            priceChart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2));
            BufferedImage priceImage = priceChart.createBufferedImage(500, 300);
            File priceChartFile = new File("Reports/assets/priceChart.png");
            ImageIO.write(priceImage, "png", priceChartFile);
            com.itextpdf.text.Image priceChartImg = com.itextpdf.text.Image.getInstance("Reports/assets/priceChart.png");
            priceChartImg.setAlignment(Element.ALIGN_CENTER);
            document.add(priceChartImg);
            document.add(new Paragraph("\n"));

            for (Map.Entry<String, Double> entry : categoryTotalPrice.entrySet()) {
                categoryValueDataset.addValue(entry.getValue(), "Total Price", entry.getKey());
            }
            JFreeChart categoryValueChart = ChartFactory.createBarChart("Total Price Per Category", "Category", "Total Price", categoryValueDataset, PlotOrientation.VERTICAL, true, true, false);
            categoryValueChart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2));
            BufferedImage categoryValueImage = categoryValueChart.createBufferedImage(500, 300);
            File categoryValueChartFile = new File("Reports/assets/categoryValueChart.png");
            ImageIO.write(categoryValueImage, "png", categoryValueChartFile);
            com.itextpdf.text.Image categoryValueChartImg = com.itextpdf.text.Image.getInstance("Reports/assets/categoryValueChart.png");
            categoryValueChartImg.setAlignment(Element.ALIGN_CENTER);
            document.add(categoryValueChartImg);

            document.add(new Paragraph("\n\n"));


                Paragraph responsibilityParagraph = new Paragraph(
                "This report has been generated by Kingfisher Resort for internal use to analyze inventory levels and other key operational metrics. The resort takes full responsibility for the accuracy and completeness of the data presented herein, which is sourced from our inventory management system. Any discrepancies or updates in the data will be reflected in future reports.",dataFont);
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
            JOptionPane.showMessageDialog(this, "Report generated successfully");
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to generate inventory report", "Success");

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(pdfFilePath));
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating report");
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to generate inventory report", "Failed");
        }
    }






    private double parseQuantity(String quantity) {
        try {
            quantity = quantity.toLowerCase().trim();
            
            if (quantity.endsWith("g")) {
                return Double.parseDouble(quantity.replace("g", "").trim());
                
            }
            
            else if (quantity.endsWith("kg")) {
                return Double.parseDouble(quantity.replace("kg", "").trim());
                
            }
            
            else {
                return Double.parseDouble(quantity); 
            }
            
        } catch (Exception e) {
            return 0; 
        }
    }






    public static void main(String[] args) {
        new ManageInventory();
    }
}