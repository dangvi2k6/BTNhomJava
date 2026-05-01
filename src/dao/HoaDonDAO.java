package dao;

import model.ChiTietHoaDon;
import model.HoaDon;
import util.DBConnection;

import java.sql.*;
import java.util.List;

public class HoaDonDAO {
    private Connection conn = DBConnection.getConnection();

    public String generateMaHD() {
        String sql = "SELECT MAX(CAST(SUBSTRING(maHD, 3, LEN(maHD)) AS INT)) FROM HoaDon";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                int max = rs.getInt(1);
                return String.format("HD%03d", max + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "HD001";
    }

    public boolean createHoaDon(HoaDon hd, List<ChiTietHoaDon> chiTiets) {
        String sqlHD = "INSERT INTO HoaDon(maHD, ngayBan, maNV, maKH, tongTien) VALUES (?, ?, ?, ?, ?)";
        String sqlCT = "INSERT INTO ChiTietHoaDon(maHD, maThuoc, soLuong, giaBan, thanhTien) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdateThuoc = "UPDATE Thuoc SET soLuongTon = soLuongTon - ? WHERE maThuoc = ?";

        try {
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Thêm Hóa đơn
            try (PreparedStatement psHD = conn.prepareStatement(sqlHD)) {
                psHD.setString(1, hd.getMaHD());
                psHD.setDate(2, hd.getNgayBan());
                psHD.setString(3, hd.getMaNV());
                if (hd.getMaKH() != null && !hd.getMaKH().trim().isEmpty()) {
                    psHD.setString(4, hd.getMaKH());
                } else {
                    psHD.setNull(4, Types.VARCHAR);
                }
                psHD.setDouble(5, hd.getTongTien());
                psHD.executeUpdate();
            }

            // 2. Thêm Chi tiết hóa đơn và Cập nhật số lượng thuốc
            try (PreparedStatement psCT = conn.prepareStatement(sqlCT);
                 PreparedStatement psUpdateThuoc = conn.prepareStatement(sqlUpdateThuoc)) {
                for (ChiTietHoaDon ct : chiTiets) {
                    // Thêm CT
                    psCT.setString(1, hd.getMaHD());
                    psCT.setString(2, ct.getMaThuoc());
                    psCT.setInt(3, ct.getSoLuong());
                    psCT.setDouble(4, ct.getGiaBan());
                    psCT.setDouble(5, ct.getThanhTien());
                    psCT.addBatch();

                    // Cập nhật số lượng thuốc
                    psUpdateThuoc.setInt(1, ct.getSoLuong());
                    psUpdateThuoc.setString(2, ct.getMaThuoc());
                    psUpdateThuoc.addBatch();
                }
                psCT.executeBatch();
                psUpdateThuoc.executeBatch();
            }

            conn.commit(); // Xác nhận transaction
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback(); // Rollback nếu có lỗi
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
