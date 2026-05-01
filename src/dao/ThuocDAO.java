package dao;

import model.Thuoc;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThuocDAO {

    private Connection conn = DBConnection.getConnection();

    // Lấy tất cả thuốc
    public List<Thuoc> getAll() {
        List<Thuoc> list = new ArrayList<>();
        String sql = "SELECT * FROM Thuoc ORDER BY maThuoc";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Tìm kiếm theo tên hoặc mã
    public List<Thuoc> search(String keyword) {
        List<Thuoc> list = new ArrayList<>();
        String sql = "SELECT * FROM Thuoc WHERE tenThuoc LIKE ? OR maThuoc LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Thuốc sắp hết (tồn kho <= 10)
    public List<Thuoc> getThuocSapHet() {
        List<Thuoc> list = new ArrayList<>();
        String sql = "SELECT * FROM Thuoc WHERE soLuongTon <= 10 ORDER BY soLuongTon";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Thuốc gần hết hạn (trong vòng 90 ngày)
    public List<Thuoc> getThuocGanHetHan() {
        List<Thuoc> list = new ArrayList<>();
        String sql = "SELECT * FROM Thuoc WHERE hanSuDung <= DATEADD(DAY, 90, CAST(GETDATE() AS DATE)) AND hanSuDung >= CAST(GETDATE() AS DATE) ORDER BY hanSuDung";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Thêm thuốc
    public boolean insert(Thuoc t) {
        String sql = "INSERT INTO Thuoc VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getMaThuoc());
            ps.setString(2, t.getTenThuoc());
            ps.setString(3, t.getLoaiThuoc());
            ps.setString(4, t.getHangSanXuat());
            ps.setDouble(5, t.getGiaNhap());
            ps.setDouble(6, t.getGiaBan());
            ps.setInt(7, t.getSoLuongTon());
            ps.setDate(8, t.getNgaySanXuat() != null ? new java.sql.Date(t.getNgaySanXuat().getTime()) : null);
            ps.setDate(9, t.getHanSuDung() != null ? new java.sql.Date(t.getHanSuDung().getTime()) : null);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Sửa thuốc
    public boolean update(Thuoc t) {
        String sql = "UPDATE Thuoc SET tenThuoc=?, loaiThuoc=?, hangSanXuat=?, giaNhap=?, giaBan=?, soLuongTon=?, ngaySanXuat=?, hanSuDung=? WHERE maThuoc=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTenThuoc());
            ps.setString(2, t.getLoaiThuoc());
            ps.setString(3, t.getHangSanXuat());
            ps.setDouble(4, t.getGiaNhap());
            ps.setDouble(5, t.getGiaBan());
            ps.setInt(6, t.getSoLuongTon());
            ps.setDate(7, t.getNgaySanXuat() != null ? new java.sql.Date(t.getNgaySanXuat().getTime()) : null);
            ps.setDate(8, t.getHanSuDung() != null ? new java.sql.Date(t.getHanSuDung().getTime()) : null);
            ps.setString(9, t.getMaThuoc());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Xóa thuốc
    public boolean delete(String maThuoc) {
        String sql = "DELETE FROM Thuoc WHERE maThuoc = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maThuoc);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Sinh mã thuốc tự động
    public String generateMaThuoc() {
        String sql = "SELECT MAX(CAST(SUBSTRING(maThuoc, 2, LEN(maThuoc)) AS INT)) FROM Thuoc";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return String.format("T%03d", rs.getInt(1) + 1);
        } catch (SQLException e) { e.printStackTrace(); }
        return "T001";
    }

    private Thuoc mapRow(ResultSet rs) throws SQLException {
        return new Thuoc(
            rs.getString("maThuoc"),
            rs.getString("tenThuoc"),
            rs.getString("loaiThuoc"),
            rs.getString("hangSanXuat"),
            rs.getDouble("giaNhap"),
            rs.getDouble("giaBan"),
            rs.getInt("soLuongTon"),
            rs.getDate("ngaySanXuat"),
            rs.getDate("hanSuDung")
        );
    }
}
