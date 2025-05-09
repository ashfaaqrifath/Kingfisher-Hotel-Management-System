package hotel.management.system;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.*;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import java.sql.Timestamp;
import javax.swing.table.DefaultTableModel;
import java.sql.Statement;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class checkIn extends javax.swing.JFrame {

    public checkIn() {
        initComponents();
        setLocation(265, 115);
        loadCustomerIDs();
        initializeTable();
        loadCheckinTable(null);
    }

    // initialize JTable with custom column names
    private void initializeTable() {
        String[] columnNames = {"ID", "Name", "Room No", "Nights", "Total"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        jTable2.setModel(model);

        jTable2.setAutoCreateColumnsFromModel(false);

        jTable2.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        jTable2.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        jTable2.getColumnModel().getColumn(2).setPreferredWidth(100); // Room No
        jTable2.getColumnModel().getColumn(3).setPreferredWidth(70);  // Nights
        jTable2.getColumnModel().getColumn(4).setPreferredWidth(100); // Total

        loadCheckinTable(null);
    }

// loadCheckin Table Method
    private int loadCheckinTable(String targetCustomerID) {
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);
        int targetRowIndex = -1;

        try {
            Conn connObj = new Conn();
            Connection con = connObj.c;
            String query = "SELECT customer_id, name, room_no, nights, (price * nights) AS total FROM checkin_details";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            int rowIndex = 0;
            while (rs.next()) {
                String customerID = rs.getString("customer_id");
                model.addRow(new Object[]{
                    customerID,
                    rs.getString("name"),
                    rs.getString("room_no"),
                    rs.getInt("nights"),
                    rs.getDouble("total")
                });

                if (customerID.equals(targetCustomerID)) {
                    targetRowIndex = rowIndex;
                }
                rowIndex++;
            }

            rs.close();
            st.close();
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading check-in table: " + e.getMessage());
            e.printStackTrace();
        }

        return targetRowIndex;
    }

    private void generatePDFReport() {
        try {
            Conn c = new Conn();
            String query = "SELECT * FROM checkin_details";
            PreparedStatement stmt = c.c.prepareStatement(query); // Use 'c.c' instead of 'c'
            ResultSet rs = stmt.executeQuery();

            // PDF document
            String timestamp = java.time.LocalDateTime.now().toString().replace(":", "-").replace(".", "-");

            File reportsDir = new File("Reports/Checkin");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }

            String pdfFilePath = "Reports/Checkin/" + timestamp + ".pdf";

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
            document.open();

            // Define fonts
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD, new BaseColor(0, 0, 139));
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

            Paragraph subtitle = new Paragraph("Checkin Management", subHeaderFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);

            Paragraph date = new Paragraph("Generated on: " + java.time.LocalDate.now(), dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);

            document.add(new Paragraph("\n"));

            LineSeparator line2 = new LineSeparator();
            line2.setPercentage(100);
            document.add(line2);

            Paragraph allEmployees = new Paragraph("All Checkin Details", subHeaderFont);
            allEmployees.setAlignment(Element.ALIGN_CENTER);
            document.add(allEmployees);

            document.add(new Paragraph("\n"));

            // Table Header with styling
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(8);
            table.setWidthPercentage(100);

            // Add styled headers
            PdfPCell c1 = new PdfPCell(new Phrase("Customer ID", headerFont));
            c1.setBackgroundColor(BaseColor.DARK_GRAY);
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setPadding(5);
            table.addCell(c1);

            PdfPCell c2 = new PdfPCell(new Phrase("Room No", headerFont));
            c2.setBackgroundColor(BaseColor.DARK_GRAY);
            c2.setHorizontalAlignment(Element.ALIGN_CENTER);
            c2.setPadding(5);
            table.addCell(c2);

            PdfPCell c3 = new PdfPCell(new Phrase("Name", headerFont));
            c3.setBackgroundColor(BaseColor.DARK_GRAY);
            c3.setHorizontalAlignment(Element.ALIGN_CENTER);
            c3.setPadding(5);
            table.addCell(c3);

            PdfPCell c4 = new PdfPCell(new Phrase("Check-in Time", headerFont));
            c4.setBackgroundColor(BaseColor.DARK_GRAY);
            c4.setHorizontalAlignment(Element.ALIGN_CENTER);
            c4.setPadding(5);
            table.addCell(c4);

            PdfPCell c5 = new PdfPCell(new Phrase("Nights", headerFont));
            c5.setBackgroundColor(BaseColor.DARK_GRAY);
            c5.setHorizontalAlignment(Element.ALIGN_CENTER);
            c5.setPadding(5);
            table.addCell(c5);

            PdfPCell c6 = new PdfPCell(new Phrase("Price", headerFont));
            c6.setBackgroundColor(BaseColor.DARK_GRAY);
            c6.setHorizontalAlignment(Element.ALIGN_CENTER);
            c6.setPadding(5);
            table.addCell(c6);

            PdfPCell c7 = new PdfPCell(new Phrase("Amount Paid", headerFont));
            c7.setBackgroundColor(BaseColor.DARK_GRAY);
            c7.setHorizontalAlignment(Element.ALIGN_CENTER);
            c7.setPadding(5);
            table.addCell(c7);

            PdfPCell c8 = new PdfPCell(new Phrase("Pending Amount", headerFont));
            c8.setBackgroundColor(BaseColor.DARK_GRAY);
            c8.setHorizontalAlignment(Element.ALIGN_CENTER);
            c8.setPadding(5);
            table.addCell(c8);

            // Summary values
            int totalCustomers = 0;
            double totalPending = 0;
            double totalPaid = 0;

            while (rs.next()) {
                table.addCell(rs.getString("customer_id"));
                table.addCell(rs.getString("room_no"));
                table.addCell(rs.getString("name"));
                table.addCell(rs.getString("checkin_time"));
                table.addCell(rs.getString("nights"));
                table.addCell("Rs. " + rs.getDouble("total"));
                table.addCell("Rs. " + rs.getDouble("paid_amount"));
                table.addCell("Rs. " + rs.getDouble("pending_amount"));

                totalCustomers++;
                totalPaid += rs.getDouble("paid_amount");
                totalPending += rs.getDouble("pending_amount");
            }

            document.add(table);
            document.add(new com.itextpdf.text.Paragraph(" "));

            // Pie Chart
            DefaultPieDataset dataset = new DefaultPieDataset();
            dataset.setValue("Paid Amount", totalPaid);
            dataset.setValue("Pending Amount", totalPending);

            JFreeChart chart = ChartFactory.createPieChart("Payment Distribution", dataset, true, true, false);
            int width = 400, height = 300;
            BufferedImage chartImage = chart.createBufferedImage(width, height);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                ImageIO.write(chartImage, "PNG", byteArrayOutputStream); // PNG format
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                // Create Image from byte array and add to document
                Image image = Image.getInstance(byteArray);
                image.setAlignment(Image.ALIGN_CENTER);
                document.add(image);
            } catch (Exception e) {
                e.printStackTrace();
            }

            document.add(new com.itextpdf.text.Paragraph("Summary", dataFont));
            document.add(new com.itextpdf.text.Paragraph("Total Check-in Customers: " + totalCustomers));
            document.add(new com.itextpdf.text.Paragraph("Total Pending: Rs. " + totalPending));
            document.add(new com.itextpdf.text.Paragraph("Total Paid: Rs. " + totalPaid));
            document.add(new Paragraph("\n\n"));

            Paragraph responsibilityParagraph = new Paragraph(
                    "This report has been generated by Kingfisher Resort for internal use to analyze the check-in activities and other key operational metrics. The resort takes full responsibility for the accuracy and completeness of the data presented herein, which is sourced from our check-in management system. Any discrepancies or updates in the data will be reflected in future reports.", dataFont);
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
            stmt.close();
            c.c.close();

            JOptionPane.showMessageDialog(this, "Report generated successfully!");
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Attempted to generate checkin report", "Success");

            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(new File(pdfFilePath));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Cannot open generated PDF automatically.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage());
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Attempted to generate checkin report", "Failed");
        }
    }

    private void loadCustomerIDs() {
        try {
            Conn connObj = new Conn();
            Connection conn = connObj.c;

            String sql = "SELECT customer_id FROM customers";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            CustIDtxt.removeAllItems();

            while (rs.next()) {
                CustIDtxt.addItem(String.valueOf(rs.getInt("customer_id")));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading Customer IDs:" + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        CustIDtxt = new javax.swing.JComboBox<>();
        Paidtxt = new javax.swing.JTextField();
        RoomNotxt = new javax.swing.JTextField();
        Nametxt = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        Pendingtxt = new javax.swing.JTextField();
        checkBtn = new javax.swing.JButton();
        updateBtn = new javax.swing.JButton();
        btnreport = new javax.swing.JButton();
        Checkintxt = new javax.swing.JSpinner();
        btnback = new javax.swing.JButton();
        btndelete = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        pricetxt = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        NightsSpinner = new javax.swing.JSpinner();
        btnok = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        txttotal = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        btnclear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Update Status");
        jLabel1.setToolTipText("");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel2.setText("Room Number");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel4.setText("Customer id");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel5.setText("Name");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel6.setText("Checkin Time");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel7.setText("Amount Paid");

        CustIDtxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CustIDtxtActionPerformed(evt);
            }
        });

        Paidtxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PaidtxtActionPerformed(evt);
            }
        });

        RoomNotxt.setName("room No"); // NOI18N
        RoomNotxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RoomNotxtActionPerformed(evt);
            }
        });

        Nametxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NametxtActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel8.setText("Pending Amount");

        checkBtn.setBackground(new java.awt.Color(0, 204, 0));
        checkBtn.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        checkBtn.setForeground(new java.awt.Color(255, 255, 255));
        checkBtn.setText("Check");
        checkBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        checkBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBtnActionPerformed(evt);
            }
        });

        updateBtn.setBackground(new java.awt.Color(51, 51, 255));
        updateBtn.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        updateBtn.setForeground(new java.awt.Color(255, 255, 255));
        updateBtn.setText("Update");
        updateBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        updateBtn.setDefaultCapable(false);
        updateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateBtnActionPerformed(evt);
            }
        });

        btnreport.setBackground(new java.awt.Color(204, 0, 204));
        btnreport.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btnreport.setForeground(new java.awt.Color(255, 255, 255));
        btnreport.setText("Generate Report");
        btnreport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnreport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnreportActionPerformed(evt);
            }
        });

        Checkintxt.setModel(new javax.swing.SpinnerDateModel());

        btnback.setBackground(new java.awt.Color(0, 0, 0));
        btnback.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btnback.setForeground(new java.awt.Color(255, 255, 255));
        btnback.setText("Back");
        btnback.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnbackActionPerformed(evt);
            }
        });

        btndelete.setBackground(new java.awt.Color(255, 0, 0));
        btndelete.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btndelete.setForeground(new java.awt.Color(255, 255, 255));
        btndelete.setText("Delete");
        btndelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btndelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btndeleteActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel10.setText("Price");

        pricetxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pricetxtActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel12.setText("Nights");

        btnok.setText("Ok");
        btnok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnokActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel13.setText("Total");

        txttotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txttotalActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(jTable2);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        btnclear.setBackground(new java.awt.Color(102, 102, 102));
        btnclear.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btnclear.setForeground(new java.awt.Color(255, 255, 255));
        btnclear.setText("Clear");
        btnclear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnclearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(382, 382, 382)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE))
                                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(106, 106, 106)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(NightsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(btnok, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(Checkintxt, javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(Nametxt, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                                                        .addComponent(CustIDtxt, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(RoomNotxt)
                                                        .addComponent(Pendingtxt)
                                                        .addComponent(pricetxt))))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(Paidtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(106, 106, 106)
                                        .addComponent(txttotal)))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(checkBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(updateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btndelete, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnreport)
                                .addGap(18, 18, 18)
                                .addComponent(btnclear, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnback, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(CustIDtxt)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(RoomNotxt, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Nametxt, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Checkintxt, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pricetxt, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(NightsSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                                .addComponent(btnok, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txttotal, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Paidtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(Pendingtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(checkBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(updateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btndelete, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnreport, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnclear, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnback, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(55, 55, 55))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void PaidtxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PaidtxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PaidtxtActionPerformed

    private void RoomNotxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RoomNotxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RoomNotxtActionPerformed

    private void NametxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NametxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NametxtActionPerformed


    private void checkBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBtnActionPerformed
        String Cus_id = (String) CustIDtxt.getSelectedItem();
        String Room_no = RoomNotxt.getText();
        String Name = Nametxt.getText();
        java.util.Date checkinDate = (java.util.Date) Checkintxt.getValue();
        java.sql.Timestamp Check_time = new java.sql.Timestamp(checkinDate.getTime());
        String price = pricetxt.getText();
        String total = txttotal.getText();
        String nights = NightsSpinner.getValue().toString();
        String Paid = Paidtxt.getText();
        String Pending = Pendingtxt.getText();

        if (Cus_id == null || Cus_id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a valid Customer ID!");
            return;
        }

        //  ADD VALIDATIONS
        try {
            double priceVal = Double.parseDouble(total);
            double paidVal = Double.parseDouble(Paid);
            double pendingVal = Double.parseDouble(Pending);

            if (priceVal != (paidVal + pendingVal)) {
                JOptionPane.showMessageDialog(this, "Invalid payment details! Price must equal Paid + Pending.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Price, Paid, and Pending fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Conn connObj = new Conn();
            Connection conn = connObj.c;

            // check if customer already checked in
            String checkQuery = "SELECT COUNT(*) FROM checkin_details WHERE customer_id = ?";
            try ( PreparedStatement checkPst = conn.prepareStatement(checkQuery)) {
                checkPst.setString(1, Cus_id);
                ResultSet rs = checkPst.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "Customer already checked in!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // Insert into checkin_details
            String sql = "INSERT INTO checkin_details (customer_id, room_no, name, checkin_time, price, nights, total, paid_amount, pending_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try ( PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, Cus_id);
                pst.setString(2, Room_no);
                pst.setString(3, Name);
                pst.setTimestamp(4, Check_time);
                pst.setString(5, price);
                pst.setString(6, nights);
                pst.setString(7, total);
                pst.setString(8, Paid);
                pst.setString(9, Pending);

                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Reservation Added Successfully!");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Attempted to add checkin: " + Name + " ( Customer ID:" + Cus_id + ")", "Success");

                // Generate QR Code
                String qrText = "Customer ID: " + Cus_id + "\n"
                        + "Name: " + Name + "\n"
                        + "Room No: " + Room_no + "\n"
                        + "Check_in Date: " + Check_time.toString() + "\n"
                        + "Total Price: " + total + "\n"
                        + "Paid Amount: " + Paid + "\n"
                        + "Pending Amount: " + Pending;
                generateQRCode(qrText);

                // Clear fields
                RoomNotxt.setText("");
                Nametxt.setText("");
                pricetxt.setText("");
                Paidtxt.setText("");
                Pendingtxt.setText("");
                txttotal.setText("");
                NightsSpinner.setValue(1);
                Checkintxt.setValue(new java.util.Date());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Attempted to add checkin: " + Name + " ( Customer ID:" + Cus_id + ")", "Failed");
        }

        // Reload table
        int newRow = loadCheckinTable(Cus_id);
        if (newRow >= 0) {
            jTable2.setRowSelectionInterval(newRow, newRow); // Auto-select the new row
        }

    }//GEN-LAST:event_checkBtnActionPerformed

    private void generateQRCode(String text) {
        try {
            int width = 300;
            int height = 300;

            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Displaying the QR code in a popup
            ImageIcon icon = new ImageIcon(qrImage);
            JOptionPane.showMessageDialog(this, "", "Generated QR Code", JOptionPane.INFORMATION_MESSAGE, icon);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating QR Code: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void CustIDtxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CustIDtxtActionPerformed
        String Cus_id = (String) CustIDtxt.getSelectedItem(); // Get selected customer ID

        if (Cus_id != null && !Cus_id.isEmpty()) {
            try {
                Conn connObj = new Conn();
                Connection conn = connObj.c;

                // check if this customer is already in checkin_details
                String checkSql = "SELECT * FROM checkin_details WHERE customer_id = ?";
                PreparedStatement checkPst = conn.prepareStatement(checkSql);
                checkPst.setString(1, Cus_id);
                ResultSet checkRs = checkPst.executeQuery();

                if (checkRs.next()) {
                    //already checked in then Fetch from checkin_details
                    RoomNotxt.setText(checkRs.getString("room_no"));
                    Nametxt.setText(checkRs.getString("name"));
                    pricetxt.setText(checkRs.getString("price"));
                    int nights = checkRs.getInt("nights");
                    NightsSpinner.setValue(nights);
                    txttotal.setText(checkRs.getString("total"));
                    Paidtxt.setText(checkRs.getString("paid_amount"));
                    Pendingtxt.setText(checkRs.getString("pending_amount"));

                    Timestamp checkinTime = checkRs.getTimestamp("checkin_time");
                    if (checkinTime != null) {
                        Checkintxt.setValue(new java.util.Date(checkinTime.getTime()));
                    } else {
                        Checkintxt.setValue(new java.util.Date());
                    }
                } else {
                    // not yet checked in then Fetch from customers table
                    String sql = "SELECT room_number, name, room_price FROM customers WHERE customer_id = ?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, Cus_id);
                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        RoomNotxt.setText(String.valueOf(rs.getInt("room_number")));
                        Nametxt.setText(rs.getString("name"));
                        pricetxt.setText(rs.getString("room_price"));
                        Paidtxt.setText("");
                        Pendingtxt.setText("");
                        Checkintxt.setValue(new java.util.Date());
                    } else {
                        // No customer found 
                        RoomNotxt.setText("");
                        Nametxt.setText("");
                        Paidtxt.setText("");
                        Pendingtxt.setText("");
                        txttotal.setText("");
                        NightsSpinner.setValue(1);
                        Checkintxt.setValue(new java.util.Date());
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error fetching data: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_CustIDtxtActionPerformed

    private void updateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
        String Cus_id = (String) CustIDtxt.getSelectedItem(); // Get selected customer ID
        String Room_no = RoomNotxt.getText();
        String Name = Nametxt.getText();
        java.util.Date checkinDate = (java.util.Date) Checkintxt.getValue();
        java.sql.Timestamp Check_time = new java.sql.Timestamp(checkinDate.getTime());
        String price = pricetxt.getText();
        String total = txttotal.getText();
        String nights = NightsSpinner.getValue().toString();
        String Paid = Paidtxt.getText();
        String Pending = Pendingtxt.getText();

        if (Cus_id != null && !Cus_id.isEmpty()) {

            // VALIDATIONS
            try {
                double priceVal = Double.parseDouble(total);
                double paidVal = Double.parseDouble(Paid);
                double pendingVal = Double.parseDouble(Pending);

                if (priceVal != (paidVal + pendingVal)) {
                    JOptionPane.showMessageDialog(this, "Invalid payment details! Total must equal Paid + Pending.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for Paid, and Pending fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Conn connObj = new Conn();
                Connection conn = connObj.c;

                String sql = "UPDATE checkin_details SET room_no = ?, name = ?, checkin_time = ?, price = ?, nights = ?, total = ?, paid_amount = ?, pending_amount = ? WHERE customer_id = ?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, Room_no);
                pst.setString(2, Name);
                pst.setTimestamp(3, Check_time);
                pst.setString(4, price);
                pst.setString(5, nights);
                pst.setString(6, total);
                pst.setString(7, Paid);
                pst.setString(8, Pending);
                pst.setString(9, Cus_id);

                int rowsAffected = pst.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Reservation Updated Successfully!");
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Attempted to update checkin details: " + Name + " ( Customer ID: " + Cus_id + ")", "Success");
                } else {
                    JOptionPane.showMessageDialog(this, "Update Failed. Customer ID not found.");
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Attempted to update checkin details: " + Name + " ( Customer ID: Unknown)", "Failed");
                }

                RoomNotxt.setText("");
                Nametxt.setText("");
                pricetxt.setText("");
                Paidtxt.setText("");
                Pendingtxt.setText("");
                txttotal.setText("");
                NightsSpinner.setValue(1);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating data: " + ex.getMessage());
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Attempted to update checkin details: " + Name + " ( Customer ID: " + Cus_id + ")", "Failed");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a valid Customer ID!");
        }

        // Reload table 
        int updatedRow = loadCheckinTable(Cus_id);
        if (updatedRow >= 0) {
            jTable2.setRowSelectionInterval(updatedRow, updatedRow);
        }
    }//GEN-LAST:event_updateBtnActionPerformed

    private void btnreportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnreportActionPerformed
        generatePDFReport();
    }//GEN-LAST:event_btnreportActionPerformed

    private void btndeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btndeleteActionPerformed
        String Cus_id = (String) CustIDtxt.getSelectedItem();

        if (Cus_id != null && !Cus_id.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this reservation?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Conn connObj = new Conn();
                    Connection conn = connObj.c;

                    String sql = "DELETE FROM checkin_details WHERE customer_id = ?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, Cus_id);

                    int rowsAffected = pst.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Reservation Deleted Successfully!");
                        ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Attempt to delete checkin details", "Success");

                        // Reload table 
                        int nextRow = loadCheckinTable(null);
                        if (nextRow >= 0 && jTable2.getRowCount() > 0) {
                            int next = Math.min(nextRow, jTable2.getRowCount() - 1);
                            jTable2.setRowSelectionInterval(next, next);
                        }

                        RoomNotxt.setText("");
                        Nametxt.setText("");
                        pricetxt.setText("");
                        Paidtxt.setText("");
                        Pendingtxt.setText("");
                        txttotal.setText("");
                        NightsSpinner.setValue(1);
                        Checkintxt.setValue(new java.util.Date());

                    } else {
                        JOptionPane.showMessageDialog(this, "Deletion Failed. Customer ID not found.");
                        ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Attempt to delete checkin details", "Failed");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting data: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a valid Customer ID to delete!");
        }
    }//GEN-LAST:event_btndeleteActionPerformed

    private void pricetxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pricetxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pricetxtActionPerformed

    private void btnbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbackActionPerformed
        setVisible(false);
// TODO add your handling code here:
    }//GEN-LAST:event_btnbackActionPerformed

    private void txttotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txttotalActionPerformed

    }//GEN-LAST:event_txttotalActionPerformed

    private void btnokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnokActionPerformed
        btnok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    double price = Double.parseDouble(pricetxt.getText());
                    int nights = (Integer) NightsSpinner.getValue();
                    double total = price * nights;
                    txttotal.setText(String.valueOf(total));

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numeric values for price or paid.");
                }
            }
        });
    }//GEN-LAST:event_btnokActionPerformed

    private void btnclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnclearActionPerformed
        RoomNotxt.setText("");
        Nametxt.setText("");
        pricetxt.setText("");
        Paidtxt.setText("");
        Pendingtxt.setText("");
        txttotal.setText("");
        NightsSpinner.setValue(1);
        Checkintxt.setValue(new java.util.Date());

        loadCheckinTable(null);
    }//GEN-LAST:event_btnclearActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(checkIn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(checkIn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(checkIn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(checkIn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new checkIn().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner Checkintxt;
    private javax.swing.JComboBox<String> CustIDtxt;
    private javax.swing.JTextField Nametxt;
    private javax.swing.JSpinner NightsSpinner;
    private javax.swing.JTextField Paidtxt;
    private javax.swing.JTextField Pendingtxt;
    private javax.swing.JTextField RoomNotxt;
    private javax.swing.JButton btnback;
    private javax.swing.JButton btnclear;
    private javax.swing.JButton btndelete;
    private javax.swing.JButton btnok;
    private javax.swing.JButton btnreport;
    private javax.swing.JButton checkBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField pricetxt;
    private javax.swing.JTextField txttotal;
    private javax.swing.JButton updateBtn;
    // End of variables declaration//GEN-END:variables

}
