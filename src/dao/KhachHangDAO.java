package dao;

import model.KhachHang;
import model.Thuoc;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {
    private Connection conn = DBConnection.getConnection();

    public List<KhachHang> getAll() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang ORDER BY maKH";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new KhachHang(
                    rs.getString("maKH"),
                    rs.getString("tenKH"),
                    rs.getString("sdt"),
                    rs.getString("diaChi"),
                    rs.getInt("diemTichLuy")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public KhachHang findBySdt(String sdt) {
        String sql = "SELECT * FROM KhachHang WHERE sdt = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sdt);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new KhachHang(
                    rs.getString("maKH"),
                    rs.getString("tenKH"),
                    rs.getString("sdt"),
                    rs.getString("diaChi"),
                    rs.getInt("diemTichLuy")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm kiếm theo tên hoặc mã
    public List<KhachHang> search(String keyword) {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE maKH LIKE ? OR tenKH LIKE ? OR sdt LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Thêm khách hàng
    public boolean add(KhachHang k) {
        String sql = "INSERT INTO KhachHang Values(?,?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, k.getMaKH());
            ps.setString(2, k.getTenKH());
            ps.setString(3, k.getSdt());
            ps.setString(4, k.getDiaChi());
            ps.setInt(5, k.getDiemTichLuy());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
    }

    // Sửa khách hàng
    public boolean update(KhachHang k) {
        String sql = "UPDATE KhachHang SET tenKH=?, sdt=?, diaChi=?, diemTichLuy=? WHERE maKH=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, k.getTenKH());
            ps.setString(2, k.getSdt());
            ps.setString(4, k.getDiaChi());
            ps.setInt(5, k.getDiemTichLuy());
            ps.setString(3, k.getMaKH());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa khách hàng
    public boolean delete(String maKH) {
        String sql = "DELETE FROM KhachHang WHERE maKH = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKH);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String generateMaKH() {
        String sql = "SELECT MAX(CAST(SUBSTRING(maKH, 3, LEN(maKH)) AS INT)) FROM KhachHang";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                int next = rs.getInt(1) + 1;
                return String.format("KH%03d", next);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "KH001"; // nếu chưa có dữ liệu
    }
    private KhachHang mapRow(ResultSet rs) throws SQLException {
        return new KhachHang(
                rs.getString("maKH"),
                rs.getString("tenKH"),
                rs.getString("sdt"),
                rs.getString("diaChi"),
                rs.getInt("diemTichLuy")
        );
    }
    
}
