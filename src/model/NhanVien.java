package model;

public class NhanVien {
    private String maNV;
    private String tenNV;
    private String username;
    private String password;
    private String vaiTro;   // "Admin" hoặc "Staff"
    private String sdt;
    private String diaChi;

    public NhanVien() {}

    public NhanVien(String maNV, String tenNV, String username, String password, String vaiTro, String sdt, String diaChi) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.username = username;
        this.password = password;
        this.vaiTro = vaiTro;
        this.sdt = sdt;
        this.diaChi = diaChi;
    }

    // Getters & Setters
    public String getMaNV()              { return maNV; }
    public void setMaNV(String maNV)     { this.maNV = maNV; }

    public String getTenNV()             { return tenNV; }
    public void setTenNV(String tenNV)   { this.tenNV = tenNV; }

    public String getUsername()                  { return username; }
    public void setUsername(String username)     { this.username = username; }

    public String getPassword()                  { return password; }
    public void setPassword(String password)     { this.password = password; }

    public String getVaiTro()                    { return vaiTro; }
    public void setVaiTro(String vaiTro)         { this.vaiTro = vaiTro; }

    public String getSdt()               { return sdt; }
    public void setSdt(String sdt)       { this.sdt = sdt; }

    public String getDiaChi()                    { return diaChi; }
    public void setDiaChi(String diaChi)         { this.diaChi = diaChi; }

    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(vaiTro);
    }

    @Override
    public String toString() {
        return tenNV + " (" + vaiTro + ")";
    }
}
