
package hotel.management.system;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import net.proteanit.sql.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;


public class SearchRoom extends JFrame implements ActionListener{
    
    JTable table;
    JButton back, search,report,add,update;
    JComboBox bedType,availabilityBox;
    JCheckBox available;
    
    SearchRoom(){
        
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        JLabel heading = new JLabel("MANAGE ROOM");
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setBounds(400, 30, 300, 30);
        add(heading);
        
        
        JLabel lblbed = new JLabel ("Bed Type");
        lblbed.setFont(new Font("Arial", Font.PLAIN, 17));
        lblbed.setBounds(40, 180, 120, 30);
        add(lblbed);
        
        bedType = new JComboBox(new String[]{"All", "Single Bed", "Double Bed"});
        bedType.setBounds(200, 180, 150, 30);
        bedType.setFont(new Font("Arial", Font.PLAIN, 17));
        bedType.setBackground(Color.WHITE);
        add(bedType);
        
        JLabel lblAvailability = new JLabel("Availability");
        lblAvailability.setFont(new Font("Arial", Font.PLAIN, 17));
        lblAvailability.setBounds(40, 300, 120, 30);
        add(lblAvailability);

        availabilityBox = new JComboBox(new String[]{"All", "Available", "Not yet"});
        availabilityBox.setBounds(200, 300, 150, 30);
        availabilityBox.setFont(new Font("Arial", Font.PLAIN, 17));
        availabilityBox.setBackground(Color.WHITE);
        add(availabilityBox);
        
        String[] columnNames = {"Room Number", "Availability", "Cleaning Status", "Price", "Type"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);

        // Set preferred column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(410, 90, 522, 400);
        add(scrollPane);

        try{
            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("select * from room");
            table.setModel(DbUtils.resultSetToTableModel(rs));
            
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);

    
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        search = new JButton("SEARCH");
        search.setBounds(120, 520, 130, 30);
        search.setBackground(Color.BLUE);
        search.setForeground(Color.WHITE);
        search.addActionListener(this);
        add(search);

        add = new JButton("ADD");
        add.setBounds(270, 520, 130, 30);
        add.setBackground(new Color(0, 153, 76));
        add.setForeground(Color.WHITE);
        add.addActionListener(this);
        add(add);
        
        update = new JButton("UPDATE");
        update.setBounds(420, 520, 130, 30);
        update.setBackground(new Color(255, 165, 0));
        update.setForeground(Color.WHITE);
        update.addActionListener(this);
        add(update);
        
        report = new JButton("REPORT");
        report.setBounds(570, 520, 130, 30);
        report.setBackground(new Color(102, 0, 153));
        report.setForeground(Color.WHITE);
        report.addActionListener(this);
        add(report);
        
        back = new JButton("BACK");
        back.setBounds(720, 520, 130, 30);
        back.setBackground(Color.BLACK);
        back.setForeground(Color.WHITE);
        back.addActionListener(this);
        add(back);
        
        setBounds(265, 115, 1000, 650);
        setVisible(true);
    }
    
    public void actionPerformed (ActionEvent ae){
        
        if (ae.getSource() == search) {
            try {
                String selectedBed = bedType.getSelectedItem().toString();
                String selectedAvailability = availabilityBox.getSelectedItem().toString();

                StringBuilder query = new StringBuilder("SELECT * FROM room WHERE 1=1");

                if (!selectedBed.equals("All")) {
                    query.append(" AND bed_type = '").append(selectedBed).append("'");
                }

                if (!selectedAvailability.equals("All")) {
                    query.append(" AND availability = '").append(selectedAvailability).append("'");
                }

                Conn conn = new Conn();
                ResultSet rs = conn.s.executeQuery(query.toString());

                table.setModel(DbUtils.resultSetToTableModel(rs));
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to search room type", "Success");
            } catch (Exception e) {
                e.printStackTrace();
        }
        }else if (ae.getSource() == report) {
            generatePDFReport();
        }else if (ae.getSource() == search) {
            new SearchRoom();
        }else if (ae.getSource() == add) {
            new AddRooms();
            setVisible(false);
        }else if (ae.getSource() == update) {
            new UpdateRoom();
            setVisible(false);
        }else if (ae.getSource() == back) {
            setVisible(false);
        }
    }

// Helper method for table headers with color
private void addTableHeader(PdfPTable table) {
    com.itextpdf.text.Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
    BaseColor headerColor = new BaseColor(0, 121, 182); // Blue

    PdfPCell cell;

    cell = new PdfPCell(new Paragraph("Room Number", headFont));
    cell.setBackgroundColor(BaseColor.DARK_GRAY);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setPadding(10);
    table.addCell(cell);

    cell = new PdfPCell(new Paragraph("Availability", headFont));
    cell.setBackgroundColor(BaseColor.DARK_GRAY);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setPadding(10);
    table.addCell(cell);

    cell = new PdfPCell(new Paragraph("Cleaning Status", headFont));
    cell.setBackgroundColor(BaseColor.DARK_GRAY);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setPadding(10);
    table.addCell(cell);

    cell = new PdfPCell(new Paragraph("Price", headFont));
    cell.setBackgroundColor(BaseColor.DARK_GRAY);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setPadding(10);
    table.addCell(cell);

    cell = new PdfPCell(new Paragraph("Bed Type", headFont));
    cell.setBackgroundColor(BaseColor.DARK_GRAY);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setPadding(10);
    table.addCell(cell);
}




public void generatePDFReport() {

    try {
        
        String timestamp = java.time.LocalDateTime.now().toString().replace(":", "-").replace(".", "-");
            
            File reportsDir = new File("Reports/Rooms");
            if (!reportsDir.exists()) reportsDir.mkdirs();
            
            String pdfFilePath = "Reports/Rooms/" + timestamp + ".pdf";
        
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
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
            
        Paragraph subtitle = new Paragraph("Room Management", subHeaderFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);

        Paragraph date = new Paragraph("Generated on: " + java.time.LocalDate.now(), dateFont);
        date.setAlignment(Element.ALIGN_CENTER);
        document.add(date);
            
        document.add(new Paragraph("\n"));
           
        LineSeparator line2 = new LineSeparator();
        line2.setPercentage(100);
        document.add(line2);

        Conn c = new Conn();
        ResultSet rs;
        
        Paragraph allRoomTitle = new Paragraph("All Room Details", subHeaderFont);
        allRoomTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(allRoomTitle);
        
         document.add(new Paragraph("\n"));
        
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        addTableHeader(table);

        rs = c.s.executeQuery("SELECT * FROM room");

        // Data for charts
        int available = 0, occupied = 0;
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();

        while (rs.next()) {
            String availability = rs.getString("availability");

            table.addCell(rs.getString("roomnumber"));
            table.addCell(availability);
            table.addCell(rs.getString("cleaning_status"));
            table.addCell(rs.getString("price"));
            table.addCell(rs.getString("bed_type"));

            if ("Available".equalsIgnoreCase(availability)) available++;
            else occupied++;
        }
        document.add(table);

        Paragraph availableRoomTitle = new Paragraph("Available Rooms", subHeaderFont);
        availableRoomTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(availableRoomTitle);

        document.add(new Paragraph("\n"));

        PdfPTable availableTable = new PdfPTable(5);
        availableTable.setWidthPercentage(100);
        addTableHeader(availableTable);

        rs = c.s.executeQuery("SELECT * FROM room WHERE availability = 'Available'");

        while (rs.next()) {
            availableTable.addCell(rs.getString("roomnumber"));
            availableTable.addCell(rs.getString("availability"));
            availableTable.addCell(rs.getString("cleaning_status"));
            availableTable.addCell(rs.getString("price"));
            availableTable.addCell(rs.getString("bed_type"));
        }
        document.add(availableTable);
        
        document.add(new Paragraph("\n"));
        
        LineSeparator line3 = new LineSeparator();
        line3.setPercentage(100);
        document.add(line3);
        
        document.add(new Paragraph("\n\n"));

        // Create Pie Chart
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        pieDataset.setValue("Available", available);
        pieDataset.setValue("Occupied", occupied);

        JFreeChart pieChart = ChartFactory.createPieChart("Room Availability", pieDataset, true, true, false);
        java.awt.Image pieImg = pieChart.createBufferedImage(500, 300);
        com.itextpdf.text.Image pieChartImg = com.itextpdf.text.Image.getInstance(pieImg, null);
        pieChartImg.setAlignment(Element.ALIGN_CENTER);
        document.add(pieChartImg);

        // Create Bar Chart
        barDataset.setValue(available, "Rooms", "Available");
        barDataset.setValue(occupied, "Rooms", "Occupied");

        JFreeChart barChart = ChartFactory.createBarChart("Room Availability", "Type", "Count", barDataset);
        java.awt.Image barImg = barChart.createBufferedImage(500, 300);
        com.itextpdf.text.Image barChartImg = com.itextpdf.text.Image.getInstance(barImg, null);
        barChartImg.setAlignment(Element.ALIGN_CENTER);
        document.add(barChartImg);
        
        document.add(new Paragraph("\n\n\n\n\n"));
        
        Paragraph responsibilityParagraph = new Paragraph(
            "This report has been generated by Kingfisher Resort for internal use to analyze room management data and key operational metrics. " +
            "The company takes full responsibility for the accuracy and completeness of the information presented herein, which is sourced from our room management system. " +
            "Any discrepancies or updates in the data will be reflected in future reports.",dataFont);
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
        
        document.close();
        JOptionPane.showMessageDialog(null, "PDF Report Generated Successfully!");
        
        if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(new File(pdfFilePath));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Cannot open generated PDF automatically.");
                }
            }
        
        ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to generate Room availability report", "Success");
        
    } catch (Exception e) {
        ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to generate Room availability report", "Failed");
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error generating PDF report.");
    }
}

    
    public static void main (String[] args){
        new SearchRoom();
    }
    
}
