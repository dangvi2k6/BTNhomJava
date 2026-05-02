package dao;

import model.NhanVien;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    private Connection conn = DBConnection.getConnection();

    // Đăng nhập: kiểm tra username + password
    public NhanVien login(String username, String password) {
        String sql = "SELECT * FROM NhanVien WHERE username = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Đăng nhập thất bại
    }

    // Lấy tất cả nhân viên la staff
    public List<NhanVien> getAllStaff() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE vaiTro='Staff' ORDER BY maNV";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<NhanVien> search(String keyword) throws Exception {
        String sql = """
            SELECT maNV, tenNV, username, vaiTro, sdt, diaChi
            FROM nhanvien
            WHERE vaiTro='Staff' AND (maNV LIKE ? OR tenNV LIKE ? OR username LIKE ? OR sdt LIKE ?)
            ORDER BY maNV
        """;

        String like = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        List<NhanVien> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }
    // Thêm nhân viên
    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NhanVien VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getTenNV());
            ps.setString(3, nv.getUsername());
            ps.setString(4, nv.getPassword());
            ps.setString(5, nv.getVaiTro());
            ps.setString(6, nv.getSdt());
            ps.setString(7, nv.getDiaChi());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Sửa nhân viên
    public boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET tenNV=?, username=?, password=?, vaiTro=?, sdt=?, diaChi=? WHERE maNV=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getTenNV());
            ps.setString(2, nv.getUsername());
            ps.setString(3, nv.getPassword());
            ps.setString(4, nv.getVaiTro());
            ps.setString(5, nv.getSdt());
            ps.setString(6, nv.getDiaChi());
            ps.setString(7, nv.getMaNV());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa nhân viên
    public boolean delete(String maNV) {
        String sql = "DELETE FROM NhanVien WHERE maNV = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Sinh mã NV tự động: NV001, NV002, ...
    public String generateMaNV() {
        String sql = "SELECT MAX(CAST(SUBSTRING(maNV, 3, LEN(maNV)) AS INT)) FROM NhanVien";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                int max = rs.getInt(1);
                return String.format("NV%03d", max + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "NV001";
    }

    public boolean existsUsername(String username, String excludeMaNV) throws Exception {
        String sql = "SELECT COUNT(*) FROM nhanvien WHERE username=? " + (excludeMaNV != null ? "AND maNV<>?" : "");
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            if (excludeMaNV != null) ps.setString(2, excludeMaNV);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // Map ResultSet → NhanVien object
    private NhanVien mapRow(ResultSet rs) throws SQLException {
        return new NhanVien(
            rs.getString("maNV"),
            rs.getString("tenNV"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("vaiTro"),
            rs.getString("sdt"),
            rs.getString("diaChi")
        );
    }
}
