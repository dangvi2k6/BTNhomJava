package dao;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class NhaCungCapDAO {

    // Trả về Map<maNCC, tenNCC> để đổ combobox
    public Map<String, String> getAllMap() {
        Map<String, String> map = new LinkedHashMap<>();
        String sql = "SELECT maNCC, tenNCC FROM NhaCungCap ORDER BY maNCC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("maNCC"), rs.getString("tenNCC"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
}