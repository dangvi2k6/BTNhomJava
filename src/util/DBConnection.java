package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // === NẾU DÙNG WINDOWS AUTHENTICATION (không cần user/pass) ===
    // Uncomment dòng dưới và comment 3 dòng URL/USERNAME/PASSWORD trên:
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyNhaThuoc;encrypt=true;trustServerCertificate=true";
    private static final String USERNAME = "chivi";
    private static final String PASSWORD = "123";

    private static Connection connection = null;

    public static Connection getConnection() {
        // System.out.println("java.library.path=" + System.getProperty("java.library.path"));
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(URL,USERNAME, PASSWORD);


                System.out.println("Ket noi SQL Server thanh cong!");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Khong tim thay JDBC Driver: " + e.getMessage());
            System.out.println("   → Hay them mssql-jdbc-x.x.x.jar vào project!");
        } catch (SQLException e) {
            System.out.println("Loi ket noi SQL Server: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Đa đong ket noi DB.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
