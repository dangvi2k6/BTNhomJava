package model;

import java.util.Date;

public class ChiTietNhap {
    private String maPhieu;
    private String maThuoc;
    private String tenThuoc;

    private int soLuong;
    private double giaNhap;
    private double thanhTien;

    private String soLo;
    private Date hanSuDung;

    public ChiTietNhap() {}

    public ChiTietNhap(String maPhieu, String maThuoc, int soLuong, double giaNhap, String soLo, Date hanSuDung) {
        this.maPhieu = maPhieu;
        this.maThuoc = maThuoc;
        this.soLuong = soLuong;
        this.giaNhap = giaNhap;
        this.soLo = soLo;
        this.hanSuDung = hanSuDung;
        this.thanhTien = soLuong * giaNhap;
    }

    public String getMaPhieu() { return maPhieu; }
    public void setMaPhieu(String maPhieu) { this.maPhieu = maPhieu; }

    public String getMaThuoc() { return maThuoc; }
    public void setMaThuoc(String maThuoc) { this.maThuoc = maThuoc; }

    public String getTenThuoc() { return tenThuoc; }
    public void setTenThuoc(String tenThuoc) { this.tenThuoc = tenThuoc; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
        this.thanhTien = this.soLuong * this.giaNhap;
    }

    public double getGiaNhap() { return giaNhap; }
    public void setGiaNhap(double giaNhap) {
        this.giaNhap = giaNhap;
        this.thanhTien = this.soLuong * this.giaNhap;
    }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }

    public String getSoLo() { return soLo; }
    public void setSoLo(String soLo) { this.soLo = soLo; }

    public Date getHanSuDung() { return hanSuDung; }
    public void setHanSuDung(Date hanSuDung) { this.hanSuDung = hanSuDung; }
}