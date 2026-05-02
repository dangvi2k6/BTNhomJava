package dao;

import model.KhachHang;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    public List<KhachHang> getAll() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang ORDER BY maKH";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public KhachHang findBySdt(String sdt) {
        String sql = "SELECT * FROM KhachHang WHERE sdt = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sdt);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<KhachHang> search(String keyword) {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE maKH LIKE ? OR tenKH LIKE ? OR sdt LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean add(KhachHang k) {
        String sql = "INSERT INTO KhachHang(maKH, tenKH, sdt, diaChi, diemTichLuy) VALUES(?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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

    public boolean update(KhachHang k) {
        String sql = "UPDATE KhachHang SET tenKH=?, sdt=?, diaChi=?, diemTichLuy=? WHERE maKH=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, k.getTenKH());
            ps.setString(2, k.getSdt());
            ps.setString(3, k.getDiaChi());
            ps.setInt(4, k.getDiemTichLuy());
            ps.setString(5, k.getMaKH());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maKH) {
        String sql = "DELETE FROM KhachHang WHERE maKH = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKH);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String generateMaKH() {
        String sql = "SELECT MAX(CAST(SUBSTRING(maKH, 3, LEN(maKH)) AS INT)) FROM KhachHang";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                int next = rs.getInt(1) + 1;
                return String.format("KH%03d", next);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "KH001";
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