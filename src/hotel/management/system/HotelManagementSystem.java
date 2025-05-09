package hotel.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HotelManagementSystem extends JFrame implements ActionListener{

    HotelManagementSystem(){

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setLayout(null);
        
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/hotel2.jpg"));
        Image i2 = i1.getImage().getScaledInstance(Toolkit.getDefaultToolkit().getScreenSize().width,
                                                   Toolkit.getDefaultToolkit().getScreenSize().height,
                                                   Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);

        JLabel image = new JLabel(i3);
        image.setBounds(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width,
                               Toolkit.getDefaultToolkit().getScreenSize().height);
        add(image);
        
        
        JLabel text = new JLabel("KINGFISHER RESORT MANAGEMENT SYSTEM");
        text.setBounds(375, 200, 1000, 90);
        text.setForeground(Color.WHITE);
        text.setFont(new Font("serif", Font.PLAIN, 40));
        image.add(text);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        
        JButton enterBtn = new JButton("ENTER TO SYSTEM");
        enterBtn.setForeground(Color.WHITE);
        enterBtn.setBackground(new Color(51, 153, 255));
        enterBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        enterBtn.setFocusPainted(false);
        enterBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        int btnWidth = 300;
        int btnHeight = 60;
        int btnX = (screenWidth - btnWidth) / 2;
        int btnY = screenHeight - btnHeight - 80;

        enterBtn.setBounds(btnX, btnY, btnWidth, btnHeight);
        enterBtn.addActionListener(this);
        image.add(enterBtn);
        
        setVisible(true);
        
            }
    
    public void actionPerformed(ActionEvent ae){
                new Login();
                setVisible(false);
    }
    public static void main(String[] args) {
       new HotelManagementSystem();
    }
    
}
