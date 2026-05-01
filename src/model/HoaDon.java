package model;

import java.sql.Date;

public class HoaDon {
    private String maHD;
    private Date ngayBan;
    private String maNV;
    private String maKH;
    private double tongTien;

    public HoaDon() {
    }

    public HoaDon(String maHD, Date ngayBan, String maNV, String maKH, double tongTien) {
        this.maHD = maHD;
        this.ngayBan = ngayBan;
        this.maNV = maNV;
        this.maKH = maKH;
        this.tongTien = tongTien;
    }

    public String getMaHD() { return maHD; }
    public void setMaHD(String maHD) { this.maHD = maHD; }

    public Date getNgayBan() { return ngayBan; }
    public void setNgayBan(Date ngayBan) { this.ngayBan = ngayBan; }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
}
