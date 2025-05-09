package hotel.management.system;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;

public class BackupDB {

    public static boolean backupSQL() {
        try {
            String timestamp = java.time.LocalDate.now().toString().replace(":", "-").replace(".", "-");
            String backupDir = "Backups/";
            File dir = new File(backupDir);
            if (!dir.exists()) dir.mkdirs();

            String filePath = backupDir + "Backup_" + timestamp + ".sql";
            String user = "root";
            String password = "1234";
            String dbName = "hotelmanagementsystem";

            String command = "mysqldump -u" + user + " -p" + password + " " + dbName + " -r " + filePath;

            Process runtimeProcess = Runtime.getRuntime().exec(command);
            int processComplete = runtimeProcess.waitFor();

            return processComplete == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean backupCSV() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/hotelmanagementsystem";
        String user = "root";
        String password = "1234";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
             Statement tableListStmt = conn.createStatement();
             ResultSet tables = tableListStmt.executeQuery("SHOW TABLES")) {

            File dir = new File("Backups/");
            if (!dir.exists()) dir.mkdirs();

            while (tables.next()) {
                String table = tables.getString(1);

                try (Statement dataStmt = conn.createStatement();
                     ResultSet rs = dataStmt.executeQuery("SELECT * FROM " + table);
                     FileWriter writer = new FileWriter("Backups/" + table + ".csv")) {

                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        writer.write(meta.getColumnName(i));
                        if (i < columnCount) writer.write(",");
                    }
                    writer.write("\n");

                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            String value = rs.getString(i);
                            if (value != null) {
                                value = value.replace("\"", "\"\"");
                                if (value.contains(",") || value.contains("\""))
                                    value = "\"" + value + "\"";
                            }
                            writer.write(value != null ? value : "");
                            if (i < columnCount) writer.write(",");
                        }
                        writer.write("\n");
                    }
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
