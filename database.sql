-- =============================================
-- DATABASE: QuanLyNhaThuoc (SQL SERVER)
-- =============================================

-- Tạo database (chạy riêng dòng này trước)
-- CREATE DATABASE QuanLyNhaThuoc;
-- GO

USE QuanLyNhaThuoc;
GO

-- -----------------------------------------------
-- Bảng: NhanVien
-- -----------------------------------------------
CREATE TABLE NhanVien (
    maNV     NVARCHAR(10)  PRIMARY KEY,
    tenNV    NVARCHAR(100) NOT NULL,
    username NVARCHAR(50)  NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    vaiTro   NVARCHAR(10)  NOT NULL DEFAULT 'Staff',  -- 'Admin' hoặc 'Staff'
    sdt      NVARCHAR(15),
    diaChi   NVARCHAR(200)
);
GO

-- -----------------------------------------------
-- Bảng: KhachHang
-- -----------------------------------------------
CREATE TABLE KhachHang (
    maKH        NVARCHAR(10)  PRIMARY KEY,
    tenKH       NVARCHAR(100) NOT NULL,
    sdt         NVARCHAR(15),
    diaChi      NVARCHAR(200),
    diemTichLuy INT           DEFAULT 0
);
GO

-- -----------------------------------------------
-- Bảng: Thuoc
-- -----------------------------------------------
CREATE TABLE Thuoc (
    maThuoc     NVARCHAR(10)  PRIMARY KEY,
    tenThuoc    NVARCHAR(200) NOT NULL,
    loaiThuoc   NVARCHAR(100),
    hangSanXuat NVARCHAR(100),
    giaNhap     FLOAT         NOT NULL DEFAULT 0,
    giaBan      FLOAT         NOT NULL DEFAULT 0,
    soLuongTon  INT           NOT NULL DEFAULT 0,
    ngaySanXuat DATE,
    hanSuDung   DATE
);
GO

-- -----------------------------------------------
-- Bảng: NhaCungCap
-- -----------------------------------------------
CREATE TABLE NhaCungCap (
    maNCC  NVARCHAR(10)  PRIMARY KEY,
    tenNCC NVARCHAR(100) NOT NULL,
    sdt    NVARCHAR(15),
    diaChi NVARCHAR(200)
);
GO

-- -----------------------------------------------
-- Bảng: PhieuNhap
-- -----------------------------------------------
CREATE TABLE PhieuNhap (
    maPhieu  NVARCHAR(10) PRIMARY KEY,
    ngayNhap DATE         NOT NULL,
    maNCC    NVARCHAR(10),
    maNV     NVARCHAR(10),
    tongTien FLOAT        DEFAULT 0,
    FOREIGN KEY (maNCC) REFERENCES NhaCungCap(maNCC),
    FOREIGN KEY (maNV)  REFERENCES NhanVien(maNV)
);
GO

-- -----------------------------------------------
-- Bảng: ThuocLo (QUẢN LÝ THEO LÔ - FIXED)
-- -----------------------------------------------
CREATE TABLE ThuocLo (
    maThuoc       NVARCHAR(10) NOT NULL,
    soLo          NVARCHAR(50) NOT NULL,
    hanSuDung     DATE         NOT NULL,
    soLuongTonLo  INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (maThuoc, soLo),
    FOREIGN KEY (maThuoc) REFERENCES Thuoc(maThuoc)
);
GO

-- -----------------------------------------------
-- Bảng: ChiTietNhap (FIXED)
-- -----------------------------------------------
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

-- -----------------------------------------------
-- Bảng: HoaDon
-- -----------------------------------------------
CREATE TABLE HoaDon (
    maHD     NVARCHAR(10) PRIMARY KEY,
    ngayBan  DATE         NOT NULL,
    maNV     NVARCHAR(10),
    maKH     NVARCHAR(10),
    tongTien FLOAT        DEFAULT 0,
    FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),
    FOREIGN KEY (maKH) REFERENCES KhachHang(maKH)
);
GO

-- -----------------------------------------------
-- Bảng: ChiTietHoaDon
-- -----------------------------------------------
CREATE TABLE ChiTietHoaDon (
    maHD      NVARCHAR(10) NOT NULL,
    maThuoc   NVARCHAR(10) NOT NULL,
    soLuong   INT          NOT NULL,
    giaBan    FLOAT        NOT NULL,
    thanhTien FLOAT        NOT NULL,
    PRIMARY KEY (maHD, maThuoc),
    FOREIGN KEY (maHD)    REFERENCES HoaDon(maHD),
    FOREIGN KEY (maThuoc) REFERENCES Thuoc(maThuoc)
);
GO

-- =============================================
-- DỮ LIỆU MẪU
-- =============================================

INSERT INTO NhanVien VALUES
(N'NV001', N'Nguyen Van Admin', N'admin', N'admin123', N'Admin', N'0901234567', N'Da Nang'),
(N'NV002', N'Tran Thi Lan',     N'lan',   N'staff123', N'Staff', N'0912345678', N'Da Nang'),
(N'NV003', N'Le Van Nam',       N'nam',   N'staff123', N'Staff', N'0923456789', N'Da Nang');

INSERT INTO KhachHang VALUES
(N'KH001', N'Nguyen Van A', N'0901111111', N'123 Nguyen Hue, Da Nang', 50),
(N'KH002', N'Tran Thi B',   N'0902222222', N'456 Le Loi, Da Nang',     100),
(N'KH003', N'Le Van C',     N'0903333333', N'789 Tran Phu, Da Nang',   0);

INSERT INTO Thuoc VALUES
(N'T001', N'Paracetamol 500mg', N'Giam dau - Ha sot',    N'Pymepharco', 2000, 5000,  200, '2024-01-01', '2027-01-01'),
(N'T002', N'Amoxicillin 500mg', N'Khang sinh',            N'DHG Pharma', 5000, 12000, 50,  '2024-06-01', '2026-06-01'),
(N'T003', N'Vitamin C 1000mg',  N'Vitamin - Khoang chat', N'OPC Pharma', 3000, 8000,  5,   '2024-03-01', '2026-12-01'),
(N'T004', N'Ibuprofen 400mg',   N'Giam dau - Khang viem', N'Domesco',    4000, 10000, 150, '2024-02-01', '2027-02-01'),
(N'T005', N'Loratadine 10mg',   N'Chong di ung',          N'Stada VN',   6000, 15000, 80,  '2024-05-01', '2026-05-01');

INSERT INTO NhaCungCap VALUES
(N'NCC001', N'Cong ty Duoc Pymepharco', N'0511234567', N'Phu Yen'),
(N'NCC002', N'Cong ty DHG Pharma',      N'0292345678', N'Can Tho'),
(N'NCC003', N'Cong ty OPC Pharma',      N'0283456789', N'Ho Chi Minh');
GO
