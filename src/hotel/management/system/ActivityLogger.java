package hotel.management.system;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityLogger {

    public static void log(String nic, String name, String jobrole, String activity, String result) {
        try {
            Conn c = new Conn();

            String insertQuery = "INSERT INTO ActivityLog(nic, name, jobrole, activity, result, activity_time) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = c.c.prepareStatement(insertQuery);
            pstmt.setString(1, nic);
            pstmt.setString(2, name);
            pstmt.setString(3, jobrole);
            pstmt.setString(4, activity);
            pstmt.setString(5, result);
            
            LocalDateTime now = LocalDateTime.now();
            pstmt.setString(6, now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
}