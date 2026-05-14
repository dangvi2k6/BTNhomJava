package dao;

import model.ChiTietNhap;
import model.PhieuNhap;
import util.DBConnection;

import java.sql.*;
import java.util.List;

public class NhapHangDAO {

    public String generateMaPhieu() {
        // PN00001...
        String sql = "SELECT TOP 1 maPhieu FROM PhieuNhap ORDER BY maPhieu DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String last = rs.getString(1); // e.g. PN00012
                int num = Integer.parseInt(last.replaceAll("\\D+", ""));
                return String.format("PN%05d", num + 1);
            }
        } catch (Exception ignored) {}
        return "PN00001";
    }

    public boolean createPhieuNhap(PhieuNhap pn, List<ChiTietNhap> items) {
        String insertPN = "INSERT INTO PhieuNhap(maPhieu, ngayNhap, maNCC, maNV, tongTien) VALUES(?,?,?,?,?)";

        // ChiTietNhap có thêm soLo, hanSuDung
        String insertCT = "INSERT INTO ChiTietNhap(maPhieu, maThuoc, soLuong, giaNhap, soLo, hanSuDung) VALUES(?,?,?,?,?,?)";

        // upsert tồn theo lô
        String upsertLo = """
            MERGE ThuocLo AS t
            USING (SELECT ? AS maThuoc, ? AS soLo) AS s
            ON (t.maThuoc = s.maThuoc AND t.soLo = s.soLo)
            WHEN MATCHED THEN
                UPDATE SET t.soLuongTonLo = t.soLuongTonLo + ?, t.hanSuDung = ?
            WHEN NOT MATCHED THEN
                INSERT (maThuoc, soLo, hanSuDung, soLuongTonLo) VALUES (?, ?, ?, ?);
        """;

        // tăng tồn tổng
        String updateTon = "UPDATE Thuoc SET soLuongTon = ISNULL(soLuongTon,0) + ? WHERE maThuoc = ?";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            // 1) insert header
            try (PreparedStatement ps = con.prepareStatement(insertPN)) {
                ps.setString(1, pn.getMaPhieu());
                ps.setDate(2, new java.sql.Date(pn.getNgayNhap().getTime()));
                ps.setString(3, pn.getMaNCC());
                ps.setString(4, pn.getMaNV());
                ps.setDouble(5, pn.getTongTien());
                ps.executeUpdate();
            }

            // 2) insert details + update lot + update total stock
            for (ChiTietNhap ct : items) {
                if (ct.getSoLo() == null || ct.getSoLo().trim().isEmpty()) {
                    throw new IllegalArgumentException("Thiếu số lô cho thuốc " + ct.getMaThuoc());
                }
                if (ct.getHanSuDung() == null) {
                    throw new IllegalArgumentException("Thiếu hạn sử dụng cho thuốc " + ct.getMaThuoc());
                }

                try (PreparedStatement ps = con.prepareStatement(insertCT)) {
                    ps.setString(1, pn.getMaPhieu());
                    ps.setString(2, ct.getMaThuoc());
                    ps.setInt(3, ct.getSoLuong());
                    ps.setDouble(4, ct.getGiaNhap());
                    ps.setString(5, ct.getSoLo());
                    ps.setDate(6, new java.sql.Date(ct.getHanSuDung().getTime()));
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(upsertLo)) {
                    // USING
                    ps.setString(1, ct.getMaThuoc());
                    ps.setString(2, ct.getSoLo());
                    // WHEN MATCHED UPDATE
                    ps.setInt(3, ct.getSoLuong());
                    ps.setDate(4, new java.sql.Date(ct.getHanSuDung().getTime()));
                    // WHEN NOT MATCHED INSERT
                    ps.setString(5, ct.getMaThuoc());
                    ps.setString(6, ct.getSoLo());
                    ps.setDate(7, new java.sql.Date(ct.getHanSuDung().getTime()));
                    ps.setInt(8, ct.getSoLuong());
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(updateTon)) {
                    ps.setInt(1, ct.getSoLuong());
                    ps.setString(2, ct.getMaThuoc());
                    ps.executeUpdate();
                }
            }

            con.commit();
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            if (con != null) {
                try { con.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            if (con != null) {
                try { 
                    con.setAutoCommit(true); 
                    con.close(); 
                } catch (SQLException ignored) {}
            }
        }
    }
}