package hotel.management.system;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

public class WelcomeScreen extends JFrame {

    public WelcomeScreen(String username, Runnable onComplete) { 

        setUndecorated(true);
        setSize(600, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.WHITE);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel resortLabel = new JLabel("KINGFISHER RESORT");
        resortLabel.setFont(new Font("Serif", Font.BOLD, 28));
        resortLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resortLabel.setForeground(new Color(0, 0, 139)); // Forest green
        resortLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/hicon.PNG")); // recommended
        Image scaledImage = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(imageLabel);
        topPanel.add(resortLabel);
        add(topPanel, BorderLayout.NORTH);

        JLabel welcomeLabel = new JLabel("Welcome " + username + " !");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setForeground(new Color(0, 102, 102));

        JLabel greetingLabel = new JLabel(getGreeting());
        greetingLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        greetingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        greetingLabel.setForeground(new Color(70, 130, 180));

        JLabel loadingLabel = new JLabel("Preparing the system for you...");
        loadingLabel.setFont(new Font("SansSerif", Font.ITALIC, 19));
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loadingLabel.setForeground(new Color(105, 105, 105));

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        centerPanel.add(welcomeLabel);
        centerPanel.add(greetingLabel);
        centerPanel.add(loadingLabel);

        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);

        new javax.swing.Timer(4000, e -> {
            ((javax.swing.Timer) e.getSource()).stop();
            dispose();
            onComplete.run();
        }).start();
    }

    private String getGreeting() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour >= 5 && hour < 12) return "Good Morning!!!";
        else if (hour >= 12 && hour < 17) return "Good Afternoon!!!";
        else if (hour >= 17 && hour < 21) return "Good Evening!!!";
        else return "Good Night!!!";
    }
}
