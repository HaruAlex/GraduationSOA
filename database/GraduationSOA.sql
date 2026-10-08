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
-- ============================================
-- THÊM 20 DÒNG DỮ LIỆU BẢNG SINHVIEN
-- ============================================
INSERT INTO SINHVIEN (MaSV, HoTen, Email, Lop)
VALUES
('SV05', N'Phạm Minh Dũng', 'sv05@gmail.com', 'CNTT01'),
('SV06', N'Hoàng Thị Giang', 'sv06@gmail.com', 'CNTT01'),
('SV07', N'Đỗ Hải Đăng', 'sv07@gmail.com', 'CNTT02'),
('SV08', N'Ngô Tấn Phát', 'sv08@gmail.com', 'CNTT02'),
('SV09', N'Bùi Phương Thảo', 'sv09@gmail.com', 'CNTT03'),
('SV10', N'Đặng Quốc Bảo', 'sv10@gmail.com', 'CNTT03'),
('SV11', N'Vũ Thị Hồng', 'sv11@gmail.com', 'CNTT01'),
('SV12', N'Lý Văn Khoa', 'sv12@gmail.com', 'CNTT02'),
('SV13', N'Nguyễn Đức Anh', 'sv13@gmail.com', 'CNTT03'),
('SV14', N'Trần Như Ngọc', 'sv14@gmail.com', 'CNTT01'),
('SV15', N'Lê Minh Hoàng', 'sv15@gmail.com', 'CNTT02'),
('SV16', N'Phạm Khánh Linh', 'sv16@gmail.com', 'CNTT03'),
('SV17', N'Hoàng Anh Tuấn', 'sv17@gmail.com', 'CNTT01'),
('SV18', N'Đỗ Thùy Trang', 'sv18@gmail.com', 'CNTT02'),
('SV19', N'Vũ Gia Huy', 'sv19@gmail.com', 'CNTT03'),
('SV20', N'Đặng Thanh Tùng', 'sv20@gmail.com', 'CNTT01'),
('SV21', N'Ngô Mai Phương', 'sv21@gmail.com', 'CNTT02'),
('SV22', N'Bùi Hoài Nam', 'sv22@gmail.com', 'CNTT03'),
('SV23', N'Lý Thị Mỹ', 'sv23@gmail.com', 'CNTT01'),
('SV24', N'Nguyễn Quang Minh', 'sv24@gmail.com', 'CNTT02');
GO

-- ============================================
-- THÊM 20 DÒNG DỮ LIỆU BẢNG DETAI
-- ============================================
INSERT INTO DETAI (MaDT, TenDT, GiangVien, SoLuongToiDa)
VALUES
('DT04', N'Phát triển ứng dụng Mobile với React Native', N'Phạm Văn D', 3),
('DT05', N'Xây dựng Microservices với Spring Boot', N'Hoàng Văn E', 2),
('DT06', N'Ứng dụng AI trong nhận diện khuôn mặt', N'Đỗ Thị F', 2),
('DT07', N'Quản lý chuỗi cung ứng bằng Blockchain', N'Ngô Văn G', 1),
('DT08', N'Xây dựng Chatbot tư vấn tuyển sinh', N'Bùi Văn H', 2),
('DT09', N'Phân tích dữ liệu lớn với Apache Spark', N'Đặng Thị I', 3),
('DT10', N'Hệ thống thương mại điện tử micro-frontend', N'Vũ Văn K', 2),
('DT11', N'Ứng dụng IoT trong giám sát nông nghiệp', N'Lý Văn L', 2),
('DT12', N'Tối ưu hóa truy vấn trong SQL Server', N'Nguyễn Thị M', 3),
('DT13', N'Xây dựng trang tin tức với Next.js và GraphQL', N'Trần Văn N', 2),
('DT14', N'Hệ thống quản lý kho hàng ứng dụng mã QR', N'Lê Thị O', 2),
('DT15', N'Phần mềm điểm danh tự động bằng camera', N'Phạm Văn P', 1),
('DT16', N'Xây dựng hệ thống khuyến nghị sản phẩm', N'Hoàng Thị Q', 3),
('DT17', N'Tự động hóa kiểm thử phần mềm với Selenium', N'Đỗ Văn R', 2),
('DT18', N'Phát triển trò chơi 2D bằng Unity', N'Vũ Thị S', 2),
('DT19', N'Ứng dụng Cloud Computing trong lưu trữ dữ liệu', N'Đặng Văn T', 3),
('DT20', N'Phân tích tình cảm người dùng trên Social Media', N'Ngô Thị U', 2),
('DT21', N'Xây dựng API Gateway với Konnect/Kong', N'Bùi Văn V', 1),
('DT22', N'Xử lý hình ảnh y tế hỗ trợ chẩn đoán bệnh', N'Lý Thị X', 2),
('DT23', N'Xây dựng hệ thống hỗ trợ học trực tuyến LMS', N'Nguyễn Văn Y', 3);
GO

-- ============================================
-- THÊM 20 DÒNG DỮ LIỆU BẢNG DANGKY
-- ============================================
INSERT INTO DANGKY (MaSV, MaDT, NgayDangKy)
VALUES
('SV03', 'DT02', '2026-09-20'),
('SV05', 'DT02', '2026-09-21'),
('SV06', 'DT04', '2026-09-22'),
('SV07', 'DT04', '2026-09-22'),
('SV08', 'DT05', '2026-09-23'),
('SV09', 'DT05', '2026-09-24'),
('SV10', 'DT06', '2026-09-25'),
('SV11', 'DT07', '2026-09-25'),
('SV12', 'DT08', '2026-09-26'),
('SV13', 'DT08', '2026-09-26'),
('SV14', 'DT09', '2026-09-27'),
('SV15', 'DT10', '2026-09-28'),
('SV16', 'DT11', '2026-09-29'),
('SV17', 'DT12', '2026-09-30'),
('SV18', 'DT13', '2026-10-01'),
('SV19', 'DT14', '2026-10-02'),
('SV20', 'DT15', '2026-10-03'),
('SV21', 'DT16', '2026-10-04'),
('SV22', 'DT17', '2026-10-05'),
('SV23', 'DT18', '2026-10-06');
GO