package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // === CẤU HÌNH KẾT NỐI SQL SERVER ===
    // Dùng SQL Server Authentication (username/password):
    private static final String URL = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=QuanLyNhaThuoc;"
            + "encrypt=false;"
            + "trustServerCertificate=true;";

    private static final String USERNAME = "congvinh2712";        // username SQL Server của bạn
    private static final String PASSWORD = "Congvinh2712@";    // password SQL Server của bạn

    // === NẾU DÙNG WINDOWS AUTHENTICATION (không cần user/pass) ===
    // Uncomment dòng dưới và comment 3 dòng URL/USERNAME/PASSWORD trên:
    // private static final String URL = "jdbc:sqlserver://localhost:1433;"
    //         + "databaseName=QuanLyNhaThuoc;"
    //         + "integratedSecurity=true;"
    //         + "encrypt=false;"
    //         + "trustServerCertificate=true;";

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                // Nếu dùng Windows Authentication thì thay bằng:
                // connection = DriverManager.getConnection(URL);

                System.out.println("✅ Kết nối SQL Server thành công!");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Không tìm thấy JDBC Driver: " + e.getMessage());
            System.out.println("   → Hãy thêm mssql-jdbc-x.x.x.jar vào project!");
        } catch (SQLException e) {
            System.out.println("❌ Lỗi kết nối SQL Server: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Đã đóng kết nối DB.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
