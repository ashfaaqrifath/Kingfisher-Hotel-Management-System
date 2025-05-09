package hotel.management.system;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

public class AdminDashboard extends JFrame implements ActionListener {

    JButton btnNewCustomer, btnLogout, btnbackup, btnempMan, btninventory, btnEventManage,btncheckin, btncheckout, btnAddRoom, btnOrder,btnactlog;

    AdminDashboard() {
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
        background.setBounds(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width,
        Toolkit.getDefaultToolkit().getScreenSize().height);
        background.setLayout(null);
        add(background);
        
        JLabel userNameLabel = new JLabel("👤 " + Session.loggedName);
        Font defaultFont1 = UIManager.getFont("Label.font");
        userNameLabel.setFont(defaultFont1.deriveFont(18f));
        userNameLabel.setForeground(Color.WHITE);
        userNameLabel.setBounds(1300, 30, 400, 30);
        background.add(userNameLabel);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(null);
        buttonPanel.setBounds(415, 150, 700, 600);
        buttonPanel.setBackground(new Color(255, 255, 255, 180));
        background.add(buttonPanel);
        
        
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/hiconRB.png"));
        Image scaledImage = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setBounds(240, 7, 200, 200);
        buttonPanel.add(imageLabel);
        

        btnempMan = createButton("EMPLOYEE MANAGEMENT");
        btnempMan.setBounds(50, 200, 250, 40);
        buttonPanel.add(btnempMan);

        btnEventManage = createButton("EVENT MANAGEMENT");
        btnEventManage.setBounds(50, 260, 250, 40);
        buttonPanel.add(btnEventManage);

        btnNewCustomer = createButton("CUSTOMER MANAGEMENT");
        btnNewCustomer.setBounds(400, 200, 250, 40);
        buttonPanel.add(btnNewCustomer);

        btninventory = createButton("INVENTORY MANAGEMENT");
        btninventory.setBounds(400, 320, 250, 40);
        buttonPanel.add(btninventory);
        
        btnOrder = createButton("ORDER MANAGEMENT");
        btnOrder.setBounds(50, 320, 250, 40);
        buttonPanel.add(btnOrder);
        
        btnAddRoom = createButton("ROOM MANAGEMENT");
        btnAddRoom.setBounds(400, 260, 250, 40);
        buttonPanel.add(btnAddRoom);
        
        btncheckin = createButton("CHECK IN");
        btncheckin.setBounds(50, 380, 250, 40);
        buttonPanel.add(btncheckin);
        
        btncheckout = createButton("CHECK OUT");
        btncheckout.setBounds(400, 380, 250, 40);
        buttonPanel.add(btncheckout);
        
        btnactlog = createButton("ACTIVITY LOG");
        btnactlog.setBounds(50, 440, 250, 40);
        buttonPanel.add(btnactlog);
        
        btnbackup = createButton("BACKUP DATABASE");
        btnbackup.setBounds(400, 440, 250, 40);
        buttonPanel.add(btnbackup);
        
        btnLogout = createButton("LOGOUT");
        btnLogout.setBackground(Color.RED);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBounds(150, 510, 400, 40);
        buttonPanel.add(btnLogout);

        Font defaultFont = UIManager.getFont("Label.font");
        Font dateTimeFont = defaultFont.deriveFont(17f);
        Color textColor = Color.WHITE;

        JLabel dayLabel = new JLabel();
        dayLabel.setFont(dateTimeFont);
        dayLabel.setForeground(textColor);
        dayLabel.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width - 207, 770, 200, 25);
        dayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        background.add(dayLabel);

        JLabel dateLabel = new JLabel();
        dateLabel.setFont(dateTimeFont);
        dateLabel.setForeground(textColor);
        dateLabel.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width - 200, 800, 200, 25);
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        background.add(dateLabel);

        JLabel timeLabel = new JLabel();
        timeLabel.setFont(dateTimeFont);
        timeLabel.setForeground(textColor);
        timeLabel.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width - 209, 830, 200, 25);
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
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> performAutoBackup(), 30, 30, TimeUnit.SECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {scheduler.shutdown();
        }));
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.addActionListener(this);
        button.setFocusPainted(false);
        return button;
    }
    
    private void performAutoBackup() {
        boolean sqlSuccess = BackupDB.backupSQL();
        boolean csvSuccess = BackupDB.backupCSV();

        if (sqlSuccess && csvSuccess) {
            System.out.println("Auto-backup completed successfully.");
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Auto-backup database", "Success");
        } else {
            System.out.println("Auto-backup failed. Check logs for details.");
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole, "Auto-backup database", "Failed");
        }
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
        }else if (ae.getSource() == btnactlog) {
            new activityLog();
        } else if (ae.getSource() == btnOrder) {           
            new OrderInventory();
        }else if (ae.getSource() == btnbackup) {           
            boolean sqlSuccess = BackupDB.backupSQL();
            boolean csvSuccess = BackupDB.backupCSV();
            if (sqlSuccess && csvSuccess) {
                JOptionPane.showMessageDialog(null, "Backup completed successfully!");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to backup database", "Success");
            } else {
                JOptionPane.showMessageDialog(null, "Backup failed. Please check console for errors.");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to backup database", "Failed");
            }
        }else if (ae.getSource() == btncheckin) {           
            new checkIn().setVisible(true);
        }else if (ae.getSource() == btncheckout) {           
            new checkOut().setVisible(true);
        }else if (ae.getSource() == btnAddRoom) {  
            new SearchRoom();
        }
    }

    public static void main(String[] args) {
        new AdminDashboard();
    }
}
