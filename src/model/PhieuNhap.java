package model;

import java.util.Date;

public class PhieuNhap {
    private String maPhieu;
    private Date ngayNhap;
    private String maNCC;
    private String maNV;
    private double tongTien;

    public PhieuNhap() {}

    public PhieuNhap(String maPhieu, Date ngayNhap, String maNCC, String maNV, double tongTien) {
        this.maPhieu = maPhieu;
        this.ngayNhap = ngayNhap;
        this.maNCC = maNCC;
        this.maNV = maNV;
        this.tongTien = tongTien;
    }

    public String getMaPhieu() { return maPhieu; }
    public void setMaPhieu(String maPhieu) { this.maPhieu = maPhieu; }

    public Date getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(Date ngayNhap) { this.ngayNhap = ngayNhap; }

    public String getMaNCC() { return maNCC; }
    public void setMaNCC(String maNCC) { this.maNCC = maNCC; }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
}