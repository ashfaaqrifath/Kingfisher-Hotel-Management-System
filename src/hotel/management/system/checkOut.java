package hotel.management.system;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;

public class checkOut extends javax.swing.JFrame {

    public checkOut() {
        initComponents();
        setLocation(265, 115);

        cbcus_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CustIDComboBoxActionPerformed(evt);
            }
        });

        loadCheckedInCustomerIDs();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cbcus_id = new javax.swing.JComboBox<>();
        tfroomno = new javax.swing.JTextField();
        tfcheckintime = new javax.swing.JTextField();
        spincheckouttime = new javax.swing.JSpinner();
        btncheckout = new javax.swing.JButton();
        btnreport = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        tfpending = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        cbpaid = new javax.swing.JCheckBox();
        btnback = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        tfprice = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        btnclear = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 102, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Checkout");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel2.setText("Customer id");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel3.setText("Checkin Time");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel4.setText("Checkout Time");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel5.setText("Room Number");

        tfroomno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfroomnoActionPerformed(evt);
            }
        });

        spincheckouttime.setModel(new javax.swing.SpinnerDateModel());

        btncheckout.setBackground(new java.awt.Color(255, 0, 0));
        btncheckout.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btncheckout.setForeground(new java.awt.Color(255, 255, 255));
        btncheckout.setText("Checkout");
        btncheckout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncheckoutActionPerformed(evt);
            }
        });

        btnreport.setBackground(new java.awt.Color(204, 0, 204));
        btnreport.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btnreport.setForeground(new java.awt.Color(255, 255, 255));
        btnreport.setText("Generate Report");
        btnreport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnreportActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel7.setText("Pending Amount");

        tfpending.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfpendingActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel8.setText("Paid");

        cbpaid.setFont(new java.awt.Font("Tahoma", 2, 13)); // NOI18N
        cbpaid.setText("Yes");

        btnback.setBackground(new java.awt.Color(0, 0, 0));
        btnback.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btnback.setForeground(new java.awt.Color(255, 255, 255));
        btnback.setText("Back");
        btnback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnbackActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel9.setText("Total");

        tfprice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfpriceActionPerformed(evt);
            }
        });

        btnclear.setBackground(new java.awt.Color(102, 102, 102));
        btnclear.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btnclear.setForeground(new java.awt.Color(255, 255, 255));
        btnclear.setText("Clear");
        btnclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnclearActionPerformed(evt);
            }
        });

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Checkout.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(686, 686, 686)
                .addComponent(btnback, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(168, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(167, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(387, 387, 387))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btncheckout, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(btnclear, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnreport, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(80, 80, 80))))
            .addGroup(layout.createSequentialGroup()
                .addGap(95, 95, 95)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spincheckouttime, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfcheckintime, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbpaid, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfpending, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfprice, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tfroomno, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbcus_id, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 519, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbcus_id, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfroomno, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfprice, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfpending, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbpaid, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfcheckintime, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spincheckouttime, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(121, 121, 121)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btncheckout, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnreport, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnclear, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnback, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tfroomnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfroomnoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfroomnoActionPerformed

    private void tfpendingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfpendingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfpendingActionPerformed

    private void generatePDFReport() {
        try {
            Conn c = new Conn();
            String query = "SELECT * FROM checkout";
            PreparedStatement stmt = c.c.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            String timestamp = java.time.LocalDateTime.now().toString().replace(":", "-").replace(".", "-");
            
            File reportsDir = new File("Reports/Checkout");
            if (!reportsDir.exists()) reportsDir.mkdirs();
            
            String pdfFilePath = "Reports/Checkout/" + timestamp + ".pdf";
            
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
            document.open();

            // Define fonts 
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
            
            Paragraph subtitle = new Paragraph("Checkout Management", subHeaderFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);

            Paragraph date = new Paragraph("Generated on: " + java.time.LocalDate.now(), dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);
            
            document.add(new Paragraph("\n"));
            
            LineSeparator line2 = new LineSeparator();
            line2.setPercentage(100);
            document.add(line2);
            
            Paragraph allEmployees = new Paragraph("All Checkout Details", subHeaderFont);
            allEmployees.setAlignment(Element.ALIGN_CENTER);
            document.add(allEmployees);
            
            document.add(new Paragraph("\n"));

            // Table Header with styling
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(7);
            table.setWidthPercentage(100);

            // Add styled header cells
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

            PdfPCell c3 = new PdfPCell(new Phrase("Check-in Time", headerFont));
            c3.setBackgroundColor(BaseColor.DARK_GRAY);
            c3.setHorizontalAlignment(Element.ALIGN_CENTER);
            c3.setPadding(5);
            table.addCell(c3);

            PdfPCell c4 = new PdfPCell(new Phrase("Check-out Time", headerFont));
            c4.setBackgroundColor(BaseColor.DARK_GRAY);
            c4.setHorizontalAlignment(Element.ALIGN_CENTER);
            c4.setPadding(5);
            table.addCell(c4);

            PdfPCell c5 = new PdfPCell(new Phrase("Price", headerFont));
            c5.setBackgroundColor(BaseColor.DARK_GRAY);
            c5.setHorizontalAlignment(Element.ALIGN_CENTER);
            c5.setPadding(5);
            table.addCell(c5);

            PdfPCell c6 = new PdfPCell(new Phrase("Pending Amount", headerFont));
            c6.setBackgroundColor(BaseColor.DARK_GRAY);
            c6.setHorizontalAlignment(Element.ALIGN_CENTER);
            c6.setPadding(5);
            table.addCell(c6);

            PdfPCell c7 = new PdfPCell(new Phrase("Paid", headerFont));
            c7.setBackgroundColor(BaseColor.DARK_GRAY);
            c7.setHorizontalAlignment(Element.ALIGN_CENTER);
            c7.setPadding(5);
            table.addCell(c7);

            int totalCustomers = 0;
            double totalIncome = 0;

            while (rs.next()) {
                table.addCell(rs.getString("cus_id"));
                table.addCell(rs.getString("room_no"));
                table.addCell(rs.getString("checkin_time"));
                table.addCell(rs.getString("checkout_time"));
                table.addCell("Rs. " + rs.getString("price"));
                table.addCell("Rs. " + rs.getDouble("pending"));
                table.addCell(rs.getString("paid"));

                totalCustomers++;
                totalIncome += rs.getDouble("price");
            }

            document.add(table);
            document.add(new com.itextpdf.text.Paragraph(" "));
            document.add(new com.itextpdf.text.Paragraph("Summary", dataFont));
            document.add(new com.itextpdf.text.Paragraph("Total Check-out Customers: " + totalCustomers));
            document.add(new com.itextpdf.text.Paragraph("Total Income: Rs. " + totalIncome));
            
             document.add(new Paragraph("\n\n"));
            
            
            Paragraph responsibilityParagraph = new Paragraph(
            "This report has been generated by Kingfisher Resort for internal use to analyze the checkout activities and other key operational metrics. The resort takes full responsibility for the accuracy and completeness of the data presented herein, which is sourced from our checkout management system. Any discrepancies or updates in the data will be reflected in future reports.",dataFont);
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
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to generate checkout report", "Success");
            
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
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to generate checkout report", "Failed");
        }
    }


    private void btncheckoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncheckoutActionPerformed

        // Check checkbox
        if (!cbpaid.isSelected()) {
            JOptionPane.showMessageDialog(this, "Please confirm payment by ticking the 'Paid' checkbox before checkout.");
            return;
        }

        String selectedID = (String) cbcus_id.getSelectedItem();
        String roomNo = tfroomno.getText();
        String price = tfprice.getText();
        String paid = cbpaid.isSelected() ? "Yes" : "No";
        String pending = tfpending.getText();
        String checkinTime = tfcheckintime.getText();

        if (selectedID == null || roomNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a customer.");
            return;
        }

        try {
            // Parse the check-in time
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date parsedDate = sdf.parse(checkinTime);
            java.sql.Timestamp sqlCheckinTime = new java.sql.Timestamp(parsedDate.getTime());

            try {
                Conn connObj = new Conn(); 
                Connection conn = connObj.c; 
                conn.setAutoCommit(false); 

                // Insert into checkout table
                String insertQuery = "INSERT INTO checkout (cus_id, room_no, price, pending, paid, checkin_time, checkout_time) "
                        + "VALUES (?, ?, ?, ?, ?, ?, NOW())";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, selectedID);
                insertStmt.setString(2, roomNo);
                insertStmt.setString(3, price);
                insertStmt.setString(4, pending);
                insertStmt.setString(5, paid);
                insertStmt.setTimestamp(6, sqlCheckinTime);
                insertStmt.executeUpdate();

                // Delete from checkin_details
                String deleteQuery = "DELETE FROM checkin_details WHERE customer_id = ?";
                PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
                deleteStmt.setString(1, selectedID);
                deleteStmt.executeUpdate();

                conn.commit();
                JOptionPane.showMessageDialog(this, "Customer checked out successfully!");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add checkout:( Customer ID:" + selectedID + ")", "Success");

                // Reset
                tfroomno.setText("");
                cbpaid.setSelected(false);
                tfprice.setText("");
                tfpending.setText("");
                tfcheckintime.setText("");
                loadCheckedInCustomerIDs();

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Checkout failed: " + e.getMessage());
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add checkout:( Customer ID:" + selectedID + ")", "Failed");
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid check-in time format. Please use yyyy-MM-dd HH:mm:ss.");
        }
    }//GEN-LAST:event_btncheckoutActionPerformed

    private void btnreportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnreportActionPerformed
        generatePDFReport();
    }//GEN-LAST:event_btnreportActionPerformed

    private void tfpriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfpriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfpriceActionPerformed

    private void btnbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbackActionPerformed
        // TODO add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_btnbackActionPerformed

    private void btnclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnclearActionPerformed
        tfroomno.setText("");
        tfprice.setText("");
        tfpending.setText("");
        tfcheckintime.setText("");
        cbpaid.setSelected(false);
    }//GEN-LAST:event_btnclearActionPerformed

    private void loadCheckedInCustomerIDs() {
        try {
            Conn connObj = new Conn(); 
            Connection conn = connObj.c; 

            String query = "SELECT customer_id FROM checkin_details";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            cbcus_id.removeAllItems();
            while (rs.next()) {
                cbcus_id.addItem(rs.getString("customer_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customer IDs: " + e.getMessage());
        }
    }

    private void CustIDComboBoxActionPerformed(ActionEvent evt) {
        String selectedID = (String) cbcus_id.getSelectedItem();
        if (selectedID == null) {
            return;
        }

        try {
            Conn connObj = new Conn(); 
            Connection conn = connObj.c; 

            String query = "SELECT * FROM checkin_details WHERE customer_id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, selectedID);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                tfroomno.setText(rs.getString("room_no"));
                tfcheckintime.setText(rs.getString("checkin_time"));
                tfprice.setText(rs.getString("total"));
                tfpending.setText(rs.getString("pending_amount"));

            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load customer details: " + e.getMessage());
        }

    }

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
            java.util.logging.Logger.getLogger(checkOut.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(checkOut.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(checkOut.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(checkOut.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new checkOut().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnback;
    private javax.swing.JButton btncheckout;
    private javax.swing.JButton btnclear;
    private javax.swing.JButton btnreport;
    private javax.swing.JComboBox<String> cbcus_id;
    private javax.swing.JCheckBox cbpaid;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSpinner spincheckouttime;
    private javax.swing.JTextField tfcheckintime;
    private javax.swing.JTextField tfpending;
    private javax.swing.JTextField tfprice;
    private javax.swing.JTextField tfroomno;
    // End of variables declaration//GEN-END:variables
}
