-- ====================================================================
-- SCRIPT TẠO CƠ SỞ DỮ LIỆU BÀI TẬP JWT SPRING BOOT 3 & SECURITY 6
-- Sinh viên: Nguyễn Minh Trí - MSSV: 24110359
-- Email: 24110359@student.hcmute.edu.vn
-- ====================================================================

-- -----------------------------------------------------
-- 1. DÀNH CHO MYSQL (Theo đúng slide bài giảng ThS. Nguyễn Hữu Trung)
-- -----------------------------------------------------
CREATE DATABASE IF NOT EXISTS `jwt_springboot3` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `jwt_springboot3`;

CREATE TABLE IF NOT EXISTS `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `full_name` VARCHAR(50) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `images` VARCHAR(500) NULL,
    `created_at` DATETIME NULL,
    `updated_at` DATETIME NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dữ liệu mẫu (Mật khẩu mặc định là: 123456 đã được mã hóa BCrypt)
INSERT INTO `users` (`full_name`, `email`, `password`, `images`, `created_at`, `updated_at`)
VALUES 
('Nguyễn Minh Trí', '24110359@student.hcmute.edu.vn', '$2a$10$gqXm2hrdjOztvPljbsY4J.jN8uxYZoUSiYBKFuDh55P1MmIcj0u9W', '/images/avatar.png', NOW(), NOW()),
('Nguyễn Hữu Trung', 'trungnh@hcmute.edu.vn', '$2a$10$gqXm2hrdjOztvPljbsY4J.jN8uxYZoUSiYBKFuDh55P1MmIcj0u9W', '/images/avatar.png', NOW(), NOW())
ON DUPLICATE KEY UPDATE `email`=`email`;


-- -----------------------------------------------------
-- 2. DÀNH CHO MICROSOFT SQL SERVER (Nền tảng Windows)
-- -----------------------------------------------------
/*
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'jwt_springboot3')
BEGIN
    CREATE DATABASE jwt_springboot3;
END
GO

USE jwt_springboot3;
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'users')
BEGIN
    CREATE TABLE users (
        id INT IDENTITY(1,1) PRIMARY KEY,
        full_name NVARCHAR(50) NOT NULL,
        email NVARCHAR(100) NOT NULL UNIQUE,
        password NVARCHAR(255) NOT NULL,
        images NVARCHAR(500) NULL,
        created_at DATETIME2 NULL,
        updated_at DATETIME2 NULL
    );
END
GO
*/
