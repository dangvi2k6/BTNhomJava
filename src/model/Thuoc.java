package model;

import java.util.Date;

public class Thuoc {
    private String maThuoc;
    private String tenThuoc;
    private String loaiThuoc;
    private String hangSanXuat;
    private double giaNhap;
    private double giaBan;
    private int soLuongTon;
    private Date ngaySanXuat;
    private Date hanSuDung;

    public Thuoc() {}

    public Thuoc(String maThuoc, String tenThuoc, String loaiThuoc, String hangSanXuat,
                 double giaNhap, double giaBan, int soLuongTon, Date ngaySanXuat, Date hanSuDung) {
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.loaiThuoc = loaiThuoc;
        this.hangSanXuat = hangSanXuat;
        this.giaNhap = giaNhap;
        this.giaBan = giaBan;
        this.soLuongTon = soLuongTon;
        this.ngaySanXuat = ngaySanXuat;
        this.hanSuDung = hanSuDung;
    }

    // Getters & Setters
    public String getMaThuoc()                   { return maThuoc; }
    public void setMaThuoc(String maThuoc)       { this.maThuoc = maThuoc; }

    public String getTenThuoc()                  { return tenThuoc; }
    public void setTenThuoc(String tenThuoc)     { this.tenThuoc = tenThuoc; }

    public String getLoaiThuoc()                 { return loaiThuoc; }
    public void setLoaiThuoc(String loaiThuoc)   { this.loaiThuoc = loaiThuoc; }

    public String getHangSanXuat()                       { return hangSanXuat; }
    public void setHangSanXuat(String hangSanXuat)       { this.hangSanXuat = hangSanXuat; }

    public double getGiaNhap()                   { return giaNhap; }
    public void setGiaNhap(double giaNhap)       { this.giaNhap = giaNhap; }

    public double getGiaBan()                    { return giaBan; }
    public void setGiaBan(double giaBan)         { this.giaBan = giaBan; }

    public int getSoLuongTon()                           { return soLuongTon; }
    public void setSoLuongTon(int soLuongTon)            { this.soLuongTon = soLuongTon; }

    public Date getNgaySanXuat()                         { return ngaySanXuat; }
    public void setNgaySanXuat(Date ngaySanXuat)         { this.ngaySanXuat = ngaySanXuat; }

    public Date getHanSuDung()                   { return hanSuDung; }
    public void setHanSuDung(Date hanSuDung)     { this.hanSuDung = hanSuDung; }

    @Override
    public String toString() {
        return maThuoc + " - " + tenThuoc;
    }
}
