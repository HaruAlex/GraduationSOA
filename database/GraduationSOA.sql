/* ============================================
   GRADUATION SOA - DATABASE
   SQL Server
   ============================================ */

-- 1. TẠO DATABASE
IF DB_ID('GraduationSOA') IS NULL
BEGIN
    CREATE DATABASE GraduationSOA;
END
GO

-- 2. SỬ DỤNG DATABASE
USE GraduationSOA;
GO

-- ============================================
-- 3. XÓA BẢNG CŨ NẾU ĐÃ TỒN TẠI
-- ============================================

IF OBJECT_ID('DANGKY', 'U') IS NOT NULL
    DROP TABLE DANGKY;
GO

IF OBJECT_ID('SINHVIEN', 'U') IS NOT NULL
    DROP TABLE SINHVIEN;
GO

IF OBJECT_ID('DETAI', 'U') IS NOT NULL
    DROP TABLE DETAI;
GO

-- ============================================
-- 4. TẠO BẢNG SINHVIEN
-- ============================================

CREATE TABLE SINHVIEN (
    MaSV VARCHAR(20) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    Email VARCHAR(100),
    Lop VARCHAR(50)
);
GO

-- ============================================
-- 5. TẠO BẢNG DETAI
-- ============================================

CREATE TABLE DETAI (
    MaDT VARCHAR(20) PRIMARY KEY,
    TenDT NVARCHAR(200) NOT NULL,
    GiangVien NVARCHAR(100),
    SoLuongToiDa INT NOT NULL DEFAULT 1
);
GO

-- ============================================
-- 6. TẠO BẢNG DANGKY
-- ============================================

CREATE TABLE DANGKY (
    MaSV VARCHAR(20) NOT NULL,
    MaDT VARCHAR(20) NOT NULL,
    NgayDangKy DATE NOT NULL DEFAULT GETDATE(),

    CONSTRAINT PK_DANGKY
        PRIMARY KEY (MaSV, MaDT),

    CONSTRAINT FK_DANGKY_SINHVIEN
        FOREIGN KEY (MaSV)
        REFERENCES SINHVIEN(MaSV),

    CONSTRAINT FK_DANGKY_DETAI
        FOREIGN KEY (MaDT)
        REFERENCES DETAI(MaDT)
);
GO

-- ============================================
-- 7. DỮ LIỆU MẪU SINH VIÊN
-- ============================================

INSERT INTO SINHVIEN (MaSV, HoTen, Email, Lop)
VALUES
('SV01', N'Nguyễn Văn An', 'sv01@gmail.com', 'CNTT01'),
('SV02', N'Trần Thị Bình', 'sv02@gmail.com', 'CNTT01'),
('SV03', N'Lê Văn Cường', 'sv03@gmail.com', 'CNTT02');
GO

-- ============================================
-- 8. DỮ LIỆU MẪU ĐỀ TÀI
-- ============================================

INSERT INTO DETAI (MaDT, TenDT, GiangVien, SoLuongToiDa)
VALUES
('DT01', N'Hệ thống quản lý đồ án tốt nghiệp', N'Nguyễn Văn A', 2),
('DT02', N'Xây dựng Web Service với Play Framework', N'Trần Văn B', 3),
('DT03', N'Ứng dụng REST API trong hệ thống SOA', N'Lê Văn C', 1);
GO

-- ============================================
-- 9. DỮ LIỆU MẪU ĐĂNG KÝ
-- ============================================

INSERT INTO DANGKY (MaSV, MaDT, NgayDangKy)
VALUES
('SV01', 'DT01', GETDATE());
GO

-- ============================================
-- 10. KIỂM TRA DỮ LIỆU
-- ============================================

SELECT * FROM SINHVIEN;
SELECT * FROM DETAI;
SELECT * FROM DANGKY;
GO