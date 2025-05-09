package hotel.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame implements ActionListener {

    JTextField nic;
    JPasswordField password;
    JButton login, cancel;
    

    Login() {
        
       setExtendedState(JFrame.MAXIMIZED_BOTH);
       setUndecorated(true);
       setLayout(new BorderLayout());

       
        ImageIcon bgIcon = new ImageIcon(ClassLoader.getSystemResource("icons/hotel2blur.jpg")); 
        Image bgImage = bgIcon.getImage().getScaledInstance(
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height,
                Image.SCALE_SMOOTH
        );
        JLabel background = new JLabel(new ImageIcon(bgImage));
        background.setLayout(new GridBagLayout());
        add(background);

        
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setPreferredSize(new Dimension(400, 350));
        loginPanel.setBackground(new Color(255, 255, 255, 200));
        
        
        JLabel title = new JLabel("USER LOGIN");
        title.setFont(new Font("Arial", Font.BOLD, 23));
        title.setBounds(100, 30, 200, 30);
        title.setHorizontalAlignment(JLabel.CENTER);
        loginPanel.add(title);

        JLabel jlnic = new JLabel("Enter NIC:");
        jlnic.setFont(new Font("SansSerif", Font.PLAIN, 16));
        jlnic.setBounds(40, 100, 100, 30);
        loginPanel.add(jlnic);

        nic = new JTextField();
        nic.setBounds(150, 100, 200, 30);
        loginPanel.add(nic);

        JLabel pass = new JLabel("Password:");
        pass.setFont(new Font("SansSerif", Font.PLAIN, 16));
        pass.setBounds(40, 170, 100, 30);
        loginPanel.add(pass);

        password = new JPasswordField();
        password.setBounds(150, 170, 200, 30);
        loginPanel.add(password);

        login = new JButton("LOGIN");
        login.setBounds(40, 250, 140, 35);
        login.setBackground(new Color(0, 153, 76));
        login.setForeground(Color.WHITE);
        login.addActionListener(this);
        loginPanel.add(login);

        cancel = new JButton("CANCEL");
        cancel.setBounds(210, 250, 140, 35);
        cancel.setBackground(Color.GRAY);
        cancel.setForeground(Color.WHITE);
        cancel.addActionListener(this);
        loginPanel.add(cancel);

        background.add(loginPanel, new GridBagConstraints());

        
//        setBounds(350, 200, 1000, 650);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == login) {
            String unic = nic.getText();
            String upass = new String(password.getPassword());

            if (!isValidNIC(unic)) {
                JOptionPane.showMessageDialog(null, "Invalid NIC. Use 10-character (e.g. 871234567V) or 12-digit NIC.");
                return;
            }

            if (upass.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Password cannot be empty!");
                return;
            }

            if (upass.length() != 6) {
                JOptionPane.showMessageDialog(null, "Password must be exactly 6 characters!");
                return;
            }

            try {
                Conn c = new Conn();
                String query = "SELECT job FROM employee WHERE nic = ? AND password = ?";
                PreparedStatement stmt = c.c.prepareStatement(query);
                stmt.setString(1, unic);
                stmt.setString(2, upass);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String jobRole = rs.getString("job");
                    
                    if (!jobRole.equalsIgnoreCase("Admin") && !jobRole.equalsIgnoreCase("Receptionist")) {
                        JOptionPane.showMessageDialog(null, "Only Admin and Receptionist can access the system.");
                        return;
                    }
                    
                        String customerQuery = "SELECT name FROM employee WHERE NIC = ?";
                        PreparedStatement custStmt = c.c.prepareStatement(customerQuery);
                        custStmt.setString(1, unic);
                        ResultSet custRs = custStmt.executeQuery();

                        String userName = "User";
                        if (custRs.next()) {
                            userName = custRs.getString("name");
                        }

                        String finalUserName = userName;
                        String finalJobRole = jobRole;
                        new WelcomeScreen(finalUserName, () -> {
                            if (finalJobRole.equalsIgnoreCase("Admin")) {
                                new AdminDashboard();
                                setVisible(false);
                            } else if (finalJobRole.equalsIgnoreCase("Receptionist")) {
                                new ReceptionDashboard();
                                setVisible(false);
                            }
                        });
                        
                        Session.loggedNIC = unic;
                        Session.loggedName = userName;
                        Session.loggedRole = jobRole;
                        
                        ActivityLogger.log(unic, userName, jobRole, "Login", "Success");
                        
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid NIC or password.");
                    ActivityLogger.log(unic, "Unknown", "Unknown", "Login", "Failed - Invalid credentials");

                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid NIC or password.");
                 ActivityLogger.log(unic, "Unknown", "Unknown", "Login", "Failed - Invalid credentials");
                e.printStackTrace();
            }

        } else if (ae.getSource() == cancel) {
            int response = JOptionPane.showConfirmDialog(null,"Are you sure you want to exit from system?","Logout Confirmation",JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                setVisible(false);
            }
        }
    }

    private boolean isValidNIC(String nic) {
        if (nic.length() == 10) {
            return nic.matches("\\d{9}[vVxX]");
        } else if (nic.length() == 12) {
            return nic.matches("\\d{12}");
        }
        return false;
    }

    public static void main(String[] args) {
        new Login();
    }
}
