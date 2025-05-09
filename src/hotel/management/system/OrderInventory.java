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
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.image.BufferedImage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.text.DecimalFormat;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jfree.data.general.*;
import javax.imageio.ImageIO;
import java.io.*;


/**
 *
 * @author ashfq
 */
public class OrderInventory extends JFrame implements ActionListener {

    JTextField txtQuantity;
    JButton btnOrder, btnBack, btnReport, btnClear, btnOrder2;
    JComboBox<String> selectItemName;
    JTable inventoryTable;
    DefaultTableModel tableView;
    JRadioButton btnKilo, btnGrams, btnUnits;
    ButtonGroup quantityBtns;

    OrderInventory() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel heading = new JLabel("INVENTORY ORDERING");
        heading.setBounds(300, 10, 600, 50);
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setForeground(new Color(30, 30, 30));
        heading.setHorizontalAlignment(JLabel.CENTER);
        add(heading);

        JLabel lblItemName = new JLabel("Select Item");
        lblItemName.setBounds(40, 180, 120, 30);
        lblItemName.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblItemName);

        selectItemName = new JComboBox<>();
        selectItemName.setBounds(200, 180, 150, 30);
        selectItemName.setBackground(Color.WHITE);
        selectItemName.addActionListener(e -> fillDetails());
        add(selectItemName);

        

        JLabel lblQuantity = new JLabel("Quantity");
        lblQuantity.setBounds(40, 240, 120, 30);
        lblQuantity.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblQuantity);

        txtQuantity = new JTextField();
        txtQuantity.setBounds(200, 240, 150, 30);
        add(txtQuantity);


        btnKilo = new JRadioButton("kg");
        btnGrams = new JRadioButton("g");
        btnUnits = new JRadioButton("Units");

        btnKilo.setBounds(200, 280, 40, 30);
        btnGrams.setBounds(250, 280, 40, 30);
        btnUnits.setBounds(300, 280, 70, 30);

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

        
        String[] columns = { "Item ID", "Item Name", "Category", "Quantity", "Price/unit(Rs)", "Orders", "Date Added" };
        tableView = new DefaultTableModel(columns, 0);
        inventoryTable = new JTable(tableView);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBounds(400, 80, 700, 390);
        add(scrollPane);


        btnOrder = new JButton("ORDER");
        btnOrder.setBackground(new Color(0, 153, 76));
        btnOrder.setForeground(Color.WHITE);
        btnOrder.setBounds(280, 520, 120, 30);
        btnOrder.addActionListener(this);
        add(btnOrder);
        
        btnOrder2 = new JButton("BATCH");
        btnOrder2.setBackground(new Color(0, 128, 128));
        btnOrder2.setForeground(Color.WHITE);
        btnOrder2.setBounds(420, 520, 120, 30);
        btnOrder2.addActionListener(this);
        add(btnOrder2);

        btnReport = new JButton("REPORT");
        btnReport.setBackground(new Color(102, 0, 153));
        btnReport.setForeground(Color.WHITE);
        btnReport.setBounds(560, 520, 120, 30);
        btnReport.addActionListener(this);
        add(btnReport);

        btnClear = new JButton("CLEAR");
        btnClear.setBackground(Color.GRAY);
        btnClear.setForeground(Color.WHITE);
        btnClear.setBounds(700, 520, 120, 30);
        btnClear.addActionListener(this);
        add(btnClear);

        btnBack = new JButton("BACK");
        btnBack.setBackground(Color.BLACK);
        btnBack.setForeground(Color.WHITE);
        btnBack.setBounds(840, 520, 120, 30);
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
        } else if (btnKilo.isSelected()) {
            return qty + "kg";
        } else {
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
                String quantity = rs.getString("quantity").toLowerCase();

                if (quantity.endsWith("kg")) {
                    txtQuantity.setText(quantity.replace("kg", ""));
                    btnKilo.setSelected(true);
                } else if (quantity.endsWith("g")) {
                    txtQuantity.setText(quantity.replace("g", ""));
                    btnGrams.setSelected(true);
                } else {
                    txtQuantity.setText(quantity.replaceAll("[^\\d]", ""));
                    btnUnits.setSelected(true);
                }
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
                    rs.getString("orders"),
                    rs.getString("date_added")
                };
                tableView.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtQuantity.setText("");
        selectItemName.setSelectedIndex(-1);
        btnGrams.setSelected(true);
    }

    private void updateQuantity() {
        String selectedItem = (String) selectItemName.getSelectedItem();
        if (selectedItem == null || txtQuantity.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields must be filled correctly");
            return;
        }

        try {
            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("SELECT quantity, orders, price FROM inventory WHERE item_name = '" + selectedItem + "'");

            if (rs.next()) {
                String dbQtyStr = rs.getString("quantity").toLowerCase();
                int currentOrders = rs.getInt("orders");
                double unitPrice = rs.getDouble("price");

                String enteredQtyStr = txtQuantity.getText().trim();
                double dbQty = 0.0;
                double enteredQty = Double.parseDouble(enteredQtyStr);

                if (dbQtyStr.endsWith("kg")) {
                    dbQty = Double.parseDouble(dbQtyStr.replace("kg", "").trim()) * 1000;
                    if (btnKilo.isSelected()) enteredQty *= 1000;
                } else if (dbQtyStr.endsWith("g")) {
                    dbQty = Double.parseDouble(dbQtyStr.replace("g", "").trim());
                    if (btnKilo.isSelected()) enteredQty *= 1000;
                } else {
                    dbQty = Double.parseDouble(dbQtyStr.replaceAll("[^\\d.]", ""));
                }

                if (enteredQty > dbQty) {
                    JOptionPane.showMessageDialog(null, "Not enough stock");
                    return;
                }

                double newQty = dbQty - enteredQty;
                String updatedQty;
                if (dbQtyStr.endsWith("kg")) {
                    updatedQty = String.format("%.2fkg", newQty / 1000);
                } else if (dbQtyStr.endsWith("g")) {
                    updatedQty = String.format("%.0fg", newQty);
                } else {
                    updatedQty = String.format("%.0f", newQty);
                }

                double totalCost = unitPrice * enteredQty;
                String totalCostStr = String.format("%.2f", totalCost);

                int newOrderCount = currentOrders + 1;
                c.s.executeUpdate("UPDATE inventory SET quantity = '" + updatedQty + "', orders = " + newOrderCount + " WHERE item_name = '" + selectedItem + "'");

                
                String formattedQty = formatQuantity();
                String insertOrderQuery = "INSERT INTO inventory_orders (item_name, order_quantity, cost) VALUES ('" + selectedItem + "', '" + formattedQty + "', " + totalCostStr + ")";
                c.s.executeUpdate(insertOrderQuery, Statement.RETURN_GENERATED_KEYS);

                
                ResultSet orderKeys = c.s.getGeneratedKeys();
                if (orderKeys.next()) {
                    int orderId = orderKeys.getInt(1); 

                    
                    ResultSet orderDateRS = c.s.executeQuery("SELECT order_date FROM inventory_orders WHERE order_id = " + orderId);
                    if (orderDateRS.next()) {
                        String orderDate = orderDateRS.getString("order_date"); 
                        generateQRCode(orderId, selectedItem, formattedQty, totalCost, orderDate);
                    }
                }

                JOptionPane.showMessageDialog(null, "Inventory order placed.");
                loadInventoryData();
                fillDetails();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error placing order.");
        }
    }



    private void generateQRCode(int orderID, String itemName, String quantity, double totalCost, String orderDate) {
        
        try {
            
            String qrContent = "Order ID: " + orderID + "\nItem Name: " + itemName + "\nQuantity: " + quantity + "\nTotal Cost: LKR " + new DecimalFormat("#.00").format(totalCost) + "\nOrder Date: " + orderDate;

            
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 300, 300);

            
            BufferedImage qrImage = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    qrImage.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
                }
            }

            
            File outputFile = new File("QR/" + "orderID-" + orderID + ".png");
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }
            ImageIO.write(qrImage, "PNG", outputFile);
            
            
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(outputFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        
        
        if (ae.getSource() == btnOrder2) {
            
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;

                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length != 2) continue;

                        String itemName = parts[0].trim();
                        String enteredQtyStr = parts[1].trim();

                        try {
                            Conn c = new Conn();
                            ResultSet rs = c.s.executeQuery("SELECT quantity, orders, price FROM inventory WHERE item_name = '" + itemName + "'");

                            if (rs.next()) {
                                String dbQtyStr = rs.getString("quantity").toLowerCase();
                                int currentOrders = rs.getInt("orders");
                                double unitPrice = rs.getDouble("price");

                                double dbQty = 0.0;
                                double enteredQty = Double.parseDouble(enteredQtyStr);

                                if (dbQtyStr.endsWith("kg")) {
                                    dbQty = Double.parseDouble(dbQtyStr.replace("kg", "").trim()) * 1000;
                                } else if (dbQtyStr.endsWith("g")) {
                                    dbQty = Double.parseDouble(dbQtyStr.replace("g", "").trim());
                                } else {
                                    dbQty = Double.parseDouble(dbQtyStr.replaceAll("[^\\d.]", ""));
                                }

                                if (enteredQty > dbQty) {
                                    System.out.println("Skipping " + itemName + ": Not enough stock");
                                    continue;
                                }

                                double newQty = dbQty - enteredQty;
                                String updatedQty;
                                String formattedQty;

                                if (dbQtyStr.endsWith("kg")) {
                                    updatedQty = String.format("%.2fkg", newQty / 1000);
                                    formattedQty = String.format("%.2fkg", enteredQty / 1000);
                                } else if (dbQtyStr.endsWith("g")) {
                                    updatedQty = String.format("%.0fg", newQty);
                                    formattedQty = String.format("%.0fg", enteredQty);
                                } else {
                                    updatedQty = String.format("%.0f", newQty);
                                    formattedQty = String.format("%.0f", enteredQty);
                                }

                                double totalCost = unitPrice * enteredQty;
                                String totalCostStr = String.format("%.2f", totalCost);

                                int newOrderCount = currentOrders + 1;
                                c.s.executeUpdate("UPDATE inventory SET quantity = '" + updatedQty + "', orders = " + newOrderCount + " WHERE item_name = '" + itemName + "'");

                                String insertOrderQuery = "INSERT INTO inventory_orders (item_name, order_quantity, cost) VALUES ('" + itemName + "', '" + formattedQty + "', " + totalCostStr + ")";
                                c.s.executeUpdate(insertOrderQuery, Statement.RETURN_GENERATED_KEYS);

                                ResultSet orderKeys = c.s.getGeneratedKeys();
                                if (orderKeys.next()) {
                                    int orderId = orderKeys.getInt(1);
                                    ResultSet orderDateRS = c.s.executeQuery("SELECT order_date FROM inventory_orders WHERE order_id = " + orderId);
                                    if (orderDateRS.next()) {
                                    }
                                }
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    JOptionPane.showMessageDialog(null, "Inventroy order placed.");
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to order item using batch form from inventory", "Success");
                    loadInventoryData();
                    fillDetails();
                    

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error reading file.");
                }
            }
        }

        

        if (ae.getSource() == btnOrder) { 
            
            int response = JOptionPane.showConfirmDialog(null, "Place order?", "Place order", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                updateQuantity();
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to order item from inventory", "Success");
                
            }
        } 
        
        else if (ae.getSource() == btnClear) {
            clearFields();
        }


}

    public void generateReport() {

        try {
            String timestamp = java.time.LocalDateTime.now().toString().replace(":", "-").replace(".", "-");
            File reportsDir = new File("Reports/Orders");

            if (!reportsDir.exists()) reportsDir.mkdir();
            Document document = new Document();
            String pdfFilePath = "Reports/Orders/" + timestamp + ".pdf";
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
            
            Paragraph subtitle = new Paragraph("Order Management", subHeaderFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);

            Paragraph date = new Paragraph("Generated on: " + java.time.LocalDate.now(), dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);
            
            document.add(new Paragraph("\n"));
            
            LineSeparator line2 = new LineSeparator();
            line2.setPercentage(100);
            document.add(line2);
            
            Paragraph allEmployees = new Paragraph("All Order Details", subHeaderFont);
            allEmployees.setAlignment(Element.ALIGN_CENTER);
            document.add(allEmployees);
            
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            String[] headers = { "Order ID", "Item Name", "Order Quantity", "Order Date", "Cost" };
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.DARK_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(7);
                table.addCell(cell);
            }

            DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
            DefaultCategoryDataset costBarDataset = new DefaultCategoryDataset();
            DefaultPieDataset pieDataset = new DefaultPieDataset();
            int totalOrders = 0;
            double totalOrderedQuantity = 0;
            double totalCost = 0;

            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("SELECT * FROM inventory_orders");
            while (rs.next()) {
                String orderId = rs.getString("order_id");
                String itemName = rs.getString("item_name");
                String orderQuantityRaw = rs.getString("order_quantity");
                String orderDate = rs.getString("order_date");
                double cost = rs.getDouble("cost");

                double quantityVal = parseQuantity(orderQuantityRaw);

                table.addCell(new Phrase(orderId, dataFont));
                table.addCell(new Phrase(itemName, dataFont));
                table.addCell(new Phrase(orderQuantityRaw, dataFont));
                table.addCell(new Phrase(orderDate, dataFont));
                table.addCell(new Phrase(String.format("$%.2f", cost), dataFont));

                barDataset.addValue(quantityVal, "Quantity", itemName);
                costBarDataset.addValue(cost, "Cost", itemName);
                totalOrderedQuantity += quantityVal;
                totalOrders++;
                totalCost += cost;
            }

            document.add(table);
            document.add(new Paragraph("\n"));

            JFreeChart barChart = ChartFactory.createBarChart("Order Quantity Statistics", "Item Name", "Quantity", barDataset, PlotOrientation.VERTICAL, true, true, false);
            CategoryPlot plot = barChart.getCategoryPlot();
            plot.setBackgroundPaint(Color.white);
            plot.setDomainGridlinePaint(Color.black);

            CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90); 
            domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 9)); 

            plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 9)); 
            barChart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 12)); 

            BufferedImage barChartImage = barChart.createBufferedImage(500, 300);
            File barChartFile = new File("Reports/assets/barChart.png");
            ImageIO.write(barChartImage, "png", barChartFile);
            com.itextpdf.text.Image barChartImg = com.itextpdf.text.Image.getInstance("Reports/assets/barChart.png");
            barChartImg.setAlignment(Element.ALIGN_CENTER);
            document.add(barChartImg);
            document.add(new Paragraph("\n"));

            
            JFreeChart costBarChart = ChartFactory.createBarChart("Order Cost Statistics", "Item Name", "Cost ($)", costBarDataset, PlotOrientation.VERTICAL, true, true, false);
            CategoryPlot costPlot = costBarChart.getCategoryPlot();
            costPlot.setBackgroundPaint(Color.white);
            costPlot.setDomainGridlinePaint(Color.black);

            CategoryAxis costDomainAxis = costPlot.getDomainAxis();
            costDomainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90); 
            costDomainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 9)); 

            costPlot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 9)); 
            costBarChart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 12)); 

            BufferedImage costBarChartImage = costBarChart.createBufferedImage(500, 300);
            File costBarChartFile = new File("Reports/assets/costBarChart.png");
            ImageIO.write(costBarChartImage, "png", costBarChartFile);
            com.itextpdf.text.Image costBarChartImg = com.itextpdf.text.Image.getInstance("Reports/assets/costBarChart.png");
            costBarChartImg.setAlignment(Element.ALIGN_CENTER);
            document.add(costBarChartImg);
            document.add(new Paragraph("\n"));



            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Total orders: " + totalOrders, dataFont));
            document.add(new Paragraph("Total ordered quantity: " + totalOrderedQuantity, dataFont));
            document.add(new Paragraph("Total cost: LKR " + String.format("%.2f", totalCost), dataFont));  
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("\n\n"));
            
            
            Paragraph responsibilityParagraph = new Paragraph(
            "This report has been generated by Kingfisher Resort for internal use to analyze order details and other key operational metrics. The resort takes full responsibility for the accuracy and completeness of the data presented herein, which is sourced from our order management system. Any discrepancies or updates in the data will be reflected in future reports.",dataFont);
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
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to generate order details report", "Success");

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(pdfFilePath));
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating report");
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to generate order details report", "Failed");
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
        new OrderInventory();
    }
}
