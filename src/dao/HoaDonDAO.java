package dao;

import model.ChiTietHoaDon;
import model.HoaDon;
import util.DBConnection;

import java.sql.*;
import java.util.List;

public class HoaDonDAO {

    public String generateMaHD() {
        String sql = "SELECT MAX(CAST(SUBSTRING(maHD, 3, LEN(maHD)) AS INT)) FROM HoaDon";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
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

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psHD = conn.prepareStatement(sqlHD)) {
                psHD.setString(1, hd.getMaHD());
                psHD.setDate(2, hd.getNgayBan());
                psHD.setString(3, hd.getMaNV());

                if (hd.getMaKH() != null && !hd.getMaKH().trim().isEmpty()) {
                    psHD.setString(4, hd.getMaKH());
                } else {
                    psHD.setNull(4, Types.NVARCHAR);
                }

                psHD.setDouble(5, hd.getTongTien());
                psHD.executeUpdate();
            }

            try (PreparedStatement psCT = conn.prepareStatement(sqlCT);
                 PreparedStatement psUpdateThuoc = conn.prepareStatement(sqlUpdateThuoc)) {

                for (ChiTietHoaDon ct : chiTiets) {
                    psCT.setString(1, hd.getMaHD());
                    psCT.setString(2, ct.getMaThuoc());
                    psCT.setInt(3, ct.getSoLuong());
                    psCT.setDouble(4, ct.getGiaBan());
                    psCT.setDouble(5, ct.getThanhTien());
                    psCT.addBatch();

                    psUpdateThuoc.setInt(1, ct.getSoLuong());
                    psUpdateThuoc.setString(2, ct.getMaThuoc());
                    psUpdateThuoc.addBatch();
                }

                psCT.executeBatch();
                psUpdateThuoc.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}