package hotel.management.system;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import net.proteanit.sql.*;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.table.JTableHeader;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.draw.LineSeparator;
import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableCellRenderer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DefaultPieDataset;


public class viewemployee extends JFrame implements ActionListener{
    
     JTable table;
     JButton btnback,btnGenerateReport;
    
    viewemployee(){
                
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        JLabel heading = new JLabel("ALL EMPLOYEES");
        heading.setBounds(300, 10, 400, 50);
        heading.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 30));
        heading.setForeground(new Color(30, 30, 30));
        heading.setHorizontalAlignment(JLabel.CENTER);
        add(heading);
        
        table = new JTable();
        table.setRowHeight(30);
        table.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 90, 950, 400);
        add(scrollPane);
            
        
        
        try{
             Conn c = new Conn();
             ResultSet rs = c.s.executeQuery("select * from employee");
             table.setModel(DbUtils.resultSetToTableModel(rs));
             
             DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
             centerRenderer.setHorizontalAlignment(JLabel.CENTER);

    
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
             
        }catch(Exception c){
             c.printStackTrace();
        }
        
        btnback = new JButton("BACK");
        btnback.setBackground(Color.BLACK);
        btnback.setForeground(Color.WHITE);
        btnback.setBounds(580, 520, 160, 30);
        btnback.addActionListener(this);
        add(btnback);
        
        btnGenerateReport = new JButton("REPORT");
        btnGenerateReport.setBackground(new Color(102, 0, 153));
        btnGenerateReport.setForeground(Color.WHITE);
        btnGenerateReport.setBounds(250, 520, 160, 30);
        btnGenerateReport.addActionListener(this);
        add(btnGenerateReport);
        
        setBounds(265, 115, 1000, 650);
        setVisible(true);
        
    }
    
     public void actionPerformed(ActionEvent ae){
         if (ae.getSource() == btnback) {
            setVisible(false);
            new AddEmployee();
        } else if (ae.getSource() == btnGenerateReport) {
            generatePDF();
        }
     }
     
     
     
    public void generatePDF() {
        try {
            
            String timestamp = java.time.LocalDateTime.now().toString().replace(":", "-").replace(".", "-");
            
            File reportsDir = new File("Reports/Employees");
            if (!reportsDir.exists()) reportsDir.mkdirs();
            
            String pdfFilePath = "Reports/Employees/" + timestamp + ".pdf";
            
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
            document.open();

            // Define fonts with full package names to avoid ambiguity
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
            
//            LineSeparator line = new LineSeparator(1f, 100f, new BaseColor(0, 0, 139), Element.ALIGN_CENTER, -1);
//            document.add(line);
            
            Paragraph title = new Paragraph("KINGFISHER RESORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            Paragraph subtitle = new Paragraph("Employee Management", subHeaderFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);

            Paragraph date = new Paragraph("Generated on: " + java.time.LocalDate.now(), dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);
            
            document.add(new Paragraph("\n"));
            
            LineSeparator line2 = new LineSeparator();
            line2.setPercentage(100);
            document.add(line2);
            
            Paragraph allEmployees = new Paragraph("All Employees Details", subHeaderFont);
            allEmployees.setAlignment(Element.ALIGN_CENTER);
            document.add(allEmployees);
            
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);

            String[] headers = {"NIC", "Name", "Age", "Gender", "Role", "Address", "Phone", "Email"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.DARK_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(7);
                table.addCell(cell);
            }

            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("SELECT * FROM employee");
            while (rs.next()) {
                table.addCell(new PdfPCell(new Phrase(rs.getString("nic"), dataFont)));
                table.addCell(new PdfPCell(new Phrase(rs.getString("name"), dataFont)));
                table.addCell(new PdfPCell(new Phrase(rs.getString("age"), dataFont)));
                table.addCell(new PdfPCell(new Phrase(rs.getString("gender"), dataFont)));
                table.addCell(new PdfPCell(new Phrase(rs.getString("job"), dataFont)));
                table.addCell(new PdfPCell(new Phrase(rs.getString("address"), dataFont)));
                table.addCell(new PdfPCell(new Phrase(rs.getString("phone"), dataFont)));
                table.addCell(new PdfPCell(new Phrase(rs.getString("email"), dataFont)));
            }
            
            rs.close();
            
            document.add(table);

            document.add(new Paragraph("\n"));
            
            Paragraph salEmployee = new Paragraph("Salary breakdown by job role.", subHeaderFont);
            salEmployee.setAlignment(Element.ALIGN_CENTER);
            document.add(salEmployee);
            
            document.add(new Paragraph("\n"));
            
            PdfPTable salaryTable = new PdfPTable(5);
            salaryTable.setWidthPercentage(100);

            String[] salaryHeaders = {"Job Role", "No. of Employees","Salary per month for Job role","Monthly Cost", "Yearly Cost"};
            for (String header : salaryHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.DARK_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(10);
                salaryTable.addCell(cell);
            }

            ResultSet rs2 = c.s.executeQuery("SELECT job, COUNT(*) AS employee_count, SUM(salary) AS total_monthly, AVG(salary) AS avg_salary " + "FROM employee GROUP BY job");

            while (rs2.next()) {
                String job = rs2.getString("job");
                int count = rs2.getInt("employee_count");
                int monthly = rs2.getInt("total_monthly");
                int yearly = monthly * 12;
                int avg = rs2.getInt("avg_salary");

                salaryTable.addCell(new PdfPCell(new Phrase(job, dataFont)));
                salaryTable.addCell(new PdfPCell(new Phrase(String.valueOf(count), dataFont)));
                salaryTable.addCell(new PdfPCell(new Phrase("Rs. " + avg, dataFont)));
                salaryTable.addCell(new PdfPCell(new Phrase("Rs. " + monthly, dataFont)));
                salaryTable.addCell(new PdfPCell(new Phrase("Rs. " + yearly, dataFont)));
            }
            
                document.add(salaryTable);

                document.add(new Paragraph("\n"));

                Paragraph totalCostHeader = new Paragraph("Total Employee Cost", subHeaderFont);
                totalCostHeader.setAlignment(Element.ALIGN_CENTER);
                document.add(totalCostHeader);
                document.add(new Paragraph("\n"));

                PdfPTable totalCostTable = new PdfPTable(2);
                totalCostTable.setWidthPercentage(80);
                totalCostTable.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell leftHeader = new PdfPCell(new Phrase("Description", headerFont));
                PdfPCell rightHeader = new PdfPCell(new Phrase("Amount", headerFont));
                leftHeader.setBackgroundColor(BaseColor.DARK_GRAY);
                rightHeader.setBackgroundColor(BaseColor.DARK_GRAY);
                leftHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
                rightHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
                leftHeader.setPadding(8);
                rightHeader.setPadding(8);
                totalCostTable.addCell(leftHeader);
                totalCostTable.addCell(rightHeader);

                ResultSet totalCostRs = c.s.executeQuery("SELECT SUM(salary) AS total_monthly FROM employee");
                if (totalCostRs.next()) {
                    int totalMonthly = totalCostRs.getInt("total_monthly");
                    int totalYearly = totalMonthly * 12;

                    totalCostTable.addCell(new PdfPCell(new Phrase("Monthly Salary Cost", dataFont)));
                    totalCostTable.addCell(new PdfPCell(new Phrase("Rs. " + totalMonthly, dataFont)));

                    totalCostTable.addCell(new PdfPCell(new Phrase("Yearly Salary Cost", dataFont)));
                    totalCostTable.addCell(new PdfPCell(new Phrase("Rs. " + totalYearly, dataFont)));
                }
                totalCostRs.close();

                document.add(totalCostTable);

            
            rs2.close();
            
            document.add(new Paragraph("\n"));
            
            Paragraph retirementTitle = new Paragraph("Employees Nearing Retirement (Age 60+)", subHeaderFont);
            retirementTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(retirementTitle);

            document.add(new Paragraph("\n"));

            PdfPTable retireTable = new PdfPTable(4);
            retireTable.setWidthPercentage(80);
            retireTable.setHorizontalAlignment(Element.ALIGN_CENTER);

            String[] retireHeaders = {"Name", "Joined Date", "Age", "Job Roll"};
            for (String header : retireHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.DARK_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(10);
                retireTable.addCell(cell);
            }

            ResultSet rs3 = c.s.executeQuery("SELECT name, datejoined, age, job FROM employee WHERE age >= 60");
            while (rs3.next()) {
                retireTable.addCell(new PdfPCell(new Phrase(rs3.getString("name"), dataFont)));
                retireTable.addCell(new PdfPCell(new Phrase(rs3.getString("datejoined"), dataFont)));
                retireTable.addCell(new PdfPCell(new Phrase(rs3.getString("age"), dataFont)));
                retireTable.addCell(new PdfPCell(new Phrase(rs3.getString("job"), dataFont)));
            }
            
            rs3.close();
            
            document.add(retireTable);
            
            document.add(new Paragraph("\n"));
            
            LineSeparator line3 = new LineSeparator();
            line3.setPercentage(100);
            document.add(line3);
            
            document.add(new Paragraph("\n"));
            
            ResultSet genderRs = c.s.executeQuery("SELECT gender, COUNT(*) AS count FROM employee GROUP BY gender");
            DefaultPieDataset genderDataset = new DefaultPieDataset();
            while (genderRs.next()) {
                genderDataset.setValue(genderRs.getString("gender"), genderRs.getInt("count"));
            }
            
            JFreeChart genderChart = ChartFactory.createPieChart("Gender Distribution", genderDataset, true, true, false);
            Image genderChartImage = convertChartToImage(genderChart);
            document.add(genderChartImage);
            
            ResultSet jobRs = c.s.executeQuery("SELECT job, COUNT(*) AS count FROM employee GROUP BY job");
            DefaultPieDataset jobDataset = new DefaultPieDataset();
            while (jobRs.next()) {
                jobDataset.setValue(jobRs.getString("job"), jobRs.getInt("count"));
            }
            JFreeChart jobChart = ChartFactory.createPieChart("Job Role Distribution", jobDataset, true, true, false);
            Image jobChartImage = convertChartToImage(jobChart);
            document.add(jobChartImage);   
            
            document.add(new Paragraph("\n\n"));
            
            
            Paragraph responsibilityParagraph = new Paragraph(
            "This report has been generated by Kingfisher Resort for internal use to analyze the employee demographics and other key workforce metrics. " +
            "The company takes full responsibility for the accuracy and completeness of the data presented herein, which is sourced from our employee management system. " +
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

            JOptionPane.showMessageDialog(null, "PDF Report generated successfully!");
            
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(new File(pdfFilePath));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Cannot open generated PDF automatically.");
                }
            }
            
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to generate employee report", "Success");
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error generating PDF report.");
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to generate employee report", "Failed");
        }
    }
        private Image convertChartToImage(JFreeChart chart) throws IOException, BadElementException {
            BufferedImage bufferedImage = chart.createBufferedImage(500, 300);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
            byte[] imageData = byteArrayOutputStream.toByteArray();
            return Image.getInstance(imageData);
        }
    
    public static void main(String[] args){
        new viewemployee();
    }
    
}
