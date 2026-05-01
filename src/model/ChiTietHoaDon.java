package model;

public class ChiTietHoaDon {
    private String maHD;
    private String maThuoc;
    private int soLuong;
    private double giaBan;
    private double thanhTien;

    // Transient fields for display
    private String tenThuoc;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(String maHD, String maThuoc, int soLuong, double giaBan, double thanhTien) {
        this.maHD = maHD;
        this.maThuoc = maThuoc;
        this.soLuong = soLuong;
        this.giaBan = giaBan;
        this.thanhTien = thanhTien;
    }

    public String getMaHD() { return maHD; }
    public void setMaHD(String maHD) { this.maHD = maHD; }

    public String getMaThuoc() { return maThuoc; }
    public void setMaThuoc(String maThuoc) { this.maThuoc = maThuoc; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getGiaBan() { return giaBan; }
    public void setGiaBan(double giaBan) { this.giaBan = giaBan; }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }

    public String getTenThuoc() { return tenThuoc; }
    public void setTenThuoc(String tenThuoc) { this.tenThuoc = tenThuoc; }
}
