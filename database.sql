-- =============================================
-- DATABASE: QuanLyNhaThuoc (SQL SERVER)
-- =============================================

USE QuanLyNhaThuoc;
GO

-- 1. Xóa bảng cũ nếu tồn tại (để cập nhật cấu trúc)
IF OBJECT_ID('ChiTietNhap', 'U') IS NOT NULL DROP TABLE ChiTietNhap;
IF OBJECT_ID('ThuocLo', 'U') IS NOT NULL DROP TABLE ThuocLo;
GO

-- 2. Tạo bảng ThuocLo (Lưu tồn kho theo từng lô thuốc)
CREATE TABLE ThuocLo (
    maThuoc       NVARCHAR(10) NOT NULL,
    soLo          NVARCHAR(50) NOT NULL,
    hanSuDung     DATE         NOT NULL,
    soLuongTonLo  INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (maThuoc, soLo),
    FOREIGN KEY (maThuoc) REFERENCES Thuoc(maThuoc)
);
GO

-- 3. Tạo lại bảng ChiTietNhap (Khớp với logic Nhập hàng theo lô)
CREATE TABLE ChiTietNhap (
    maPhieu  NVARCHAR(10) NOT NULL,
    maThuoc  NVARCHAR(10) NOT NULL,
    soLuong  INT          NOT NULL,
    giaNhap  FLOAT        NOT NULL,
    soLo     NVARCHAR(50) NOT NULL,
    hanSuDung DATE        NOT NULL,
    PRIMARY KEY (maPhieu, maThuoc, soLo),
    FOREIGN KEY (maPhieu) REFERENCES PhieuNhap(maPhieu),
    FOREIGN KEY (maThuoc) REFERENCES Thuoc(maThuoc)
);
GO
