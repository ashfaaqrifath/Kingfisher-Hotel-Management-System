package hotel.management.system;

import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class ReceptionDashboard extends JFrame implements ActionListener {

    JButton btnNewCustomer, btnLogout, btnempMan, btninventory, btnEventManage,btncheckin, btncheckout, btnAddRoom;

    ReceptionDashboard() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setLayout(null);
        
        ImageIcon bgIcon = new ImageIcon(ClassLoader.getSystemResource("icons/hotel2blur.jpg"));
        Image bgImage = bgIcon.getImage().getScaledInstance(
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height,
                Image.SCALE_SMOOTH
        );
        
        JLabel background = new JLabel(new ImageIcon(bgImage));
        background.setBounds(0, 0,
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height);
        background.setLayout(null);

        JLabel userNameLabel = new JLabel("👤 " + Session.loggedName);
        Font defaultFont1 = UIManager.getFont("Label.font");
        userNameLabel.setFont(defaultFont1.deriveFont(18f));
        userNameLabel.setForeground(Color.WHITE);
        userNameLabel.setBounds(1300, 30, 400, 30);

        background.add(userNameLabel);

        add(background);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(null);
        buttonPanel.setBounds(575, 200, 400, 550);
        buttonPanel.setBackground(new Color(255, 255, 255, 180));
        background.add(buttonPanel);
        
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/hiconRB.png"));
        Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setBounds(120, 6, 150, 150);
        buttonPanel.add(imageLabel);
        
        btnEventManage = createButton("EVENT MANAGEMENT");
        btnEventManage.setBounds(75, 150, 250, 40);
        buttonPanel.add(btnEventManage);

        btnNewCustomer = createButton("CUSTOMER MANAGEMENT");
        btnNewCustomer.setBounds(75, 210, 250, 40);
        buttonPanel.add(btnNewCustomer);
        
        btnAddRoom = createButton("ROOM MANAGEMENT");
        btnAddRoom.setBounds(75, 270, 250, 40);
        buttonPanel.add(btnAddRoom);
        
        btncheckin = createButton("CHECK IN");
        btncheckin.setBounds(75, 330, 250, 40);
        buttonPanel.add(btncheckin);
        
        btncheckout = createButton("CHECK OUT");
        btncheckout.setBounds(75, 390, 250, 40);
        buttonPanel.add(btncheckout);
        
        btnLogout = createButton("LOGOUT");
        btnLogout.setBackground(Color.RED);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBounds(75, 480, 250, 40);
        buttonPanel.add(btnLogout);
        
        Font defaultFont = UIManager.getFont("Label.font");
        Font dateTimeFont = defaultFont.deriveFont(17f);
        Color textColor = Color.WHITE;

        JLabel dayLabel = new JLabel();
        dayLabel.setFont(dateTimeFont);
        dayLabel.setForeground(textColor);
        dayLabel.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width - 177, 770, 200, 25);
        dayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        background.add(dayLabel);

        JLabel dateLabel = new JLabel();
        dateLabel.setFont(dateTimeFont);
        dateLabel.setForeground(textColor);
        dateLabel.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width - 170, 800, 200, 25);
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        background.add(dateLabel);

        JLabel timeLabel = new JLabel();
        timeLabel.setFont(dateTimeFont);
        timeLabel.setForeground(textColor);
        timeLabel.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width - 179, 830, 200, 25);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        background.add(timeLabel);

        Timer timer = new Timer(1000, e -> {
            LocalDateTime now = LocalDateTime.now();
            dayLabel.setText("🌞  " + now.format(DateTimeFormatter.ofPattern("EEEE")));
            dateLabel.setText("📅  " + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            timeLabel.setText("⏰  " + now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        });
        timer.setInitialDelay(0);
        timer.start();
        
        setVisible(true);
        showTodayReminder();
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.addActionListener(this);
        button.setFocusPainted(false);
        return button;
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == btnLogout) {
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Logged out", "Success");
            Session.clear();
            int response = JOptionPane.showConfirmDialog(null,"Are you sure you want to logout?","Logout Confirmation",JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    setVisible(false);
                    new Login();
                }
        } else if (ae.getSource() == btnempMan) {
            new AddEmployee();
        } else if (ae.getSource() == btninventory) {
            new ManageInventory();
        } else if (ae.getSource() == btnNewCustomer) {
            new ManageCustomer();
        } else if (ae.getSource() == btnEventManage) {
            new AddEvent();
        } else if (ae.getSource() == btncheckin) {           
            new checkIn().setVisible(true);
        }else if (ae.getSource() == btncheckout) {           
            new checkOut().setVisible(true);
        }else if (ae.getSource() == btnAddRoom) {  
            new SearchRoom();
        }
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
                                    LocalTime currentTime = LocalTime.now();

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

    public static void main(String[] args) {
        new ReceptionDashboard();
    }
}
