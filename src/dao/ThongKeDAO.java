package dao;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDAO {
    private Connection conn = DBConnection.getConnection();

    // Doanh thu theo khoảng thời gian
    public List<Object[]> getDoanhThu(java.util.Date tuNgay, java.util.Date denNgay) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT ngayBan, COUNT(maHD) as soHoaDon, SUM(tongTien) as tongDoanhThu " +
                     "FROM HoaDon " +
                     "WHERE ngayBan >= ? AND ngayBan <= ? " +
                     "GROUP BY ngayBan ORDER BY ngayBan DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(tuNgay.getTime()));
            ps.setDate(2, new java.sql.Date(denNgay.getTime()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getDate("ngayBan"),
                    rs.getInt("soHoaDon"),
                    rs.getDouble("tongDoanhThu")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Top thuốc bán chạy
    public List<Object[]> getTopThuocBanChay(java.util.Date tuNgay, java.util.Date denNgay) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT t.maThuoc, t.tenThuoc, SUM(ct.soLuong) as tongSoLuong, SUM(ct.thanhTien) as tongTien " +
                     "FROM ChiTietHoaDon ct " +
                     "JOIN HoaDon hd ON ct.maHD = hd.maHD " +
                     "JOIN Thuoc t ON ct.maThuoc = t.maThuoc " +
                     "WHERE hd.ngayBan >= ? AND hd.ngayBan <= ? " +
                     "GROUP BY t.maThuoc, t.tenThuoc " +
                     "ORDER BY tongSoLuong DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(tuNgay.getTime()));
            ps.setDate(2, new java.sql.Date(denNgay.getTime()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("maThuoc"),
                    rs.getString("tenThuoc"),
                    rs.getInt("tongSoLuong"),
                    rs.getDouble("tongTien")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
