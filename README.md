# BÀI TẬP SPRING BOOT 3 - SPRING SECURITY 6 - JSON WEB TOKEN (JWT) VỚI NIMBUS JOSE+JWT

## 1. THÔNG TIN SINH VIÊN
- **Họ và tên:** Nguyễn Minh Trí
- **Mã số sinh viên:** 24110359
- **Email:** 24110359@student.hcmute.edu.vn
- **Lớp:** Lập trình Web (WEBPR330479)
- **Giảng viên hướng dẫn:** ThS. Nguyễn Hữu Trung

---

## 2. NỘI DUNG VÀ YÊU CẦU ĐỀ BÀI
1. **Làm bài tập ví dụ trong bài giảng JWT:**
   - Triển khai đầy đủ hệ thống Authentication & Authorization theo Slide hướng dẫn (từ Bước 1 đến Bước 10).
   - Bảo mật API RESTful bằng JWT.
   - Giao diện đăng nhập và hồ sơ cá nhân hiển thị qua Ajax + Thymeleaf.
2. **Sử dụng thư viện Nimbus thay thế JWT (JJWT):**
   - Thay vì sử dụng bộ ba thư viện `io.jsonwebtoken` (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`), dự án sử dụng thư viện **Nimbus JOSE + JWT (`com.nimbusds:nimbus-jose-jwt`)**.
   - Thiết kế lại lớp `JwtService` dùng `SignedJWT`, `MACSigner`, `MACVerifier`, `JWTClaimsSet`.
3. **Giải đáp thắc mắc về cài đặt JWT:**
   - JWT hay thư viện Nimbus **KHÔNG CẦN TẢI THỦ CÔNG**. Maven (`pom.xml`) sẽ tự động tải các gói jar cần thiết từ kho Maven Central về máy khi biên dịch.

---

## 3. CƠ SỞ DỮ LIỆU & KHẢ NĂNG TƯƠNG THÍCH KHI CHẤM BÀI
Dự án được cấu hình sẵn cả driver **Microsoft SQL Server** và **MySQL** trong `pom.xml`:
- **Đang chạy mặc định:** **Microsoft SQL Server** (kết nối database `jwt_springboot3`, user `sa`).
- **Dành cho Thầy chấm bài bằng MySQL:** 
  - Đã có sẵn file [`database.sql`](file:///d:/UTE/Nam_3_2026_2027_1/nam3/LTW/Bt29-9-2026/database.sql) để import nhanh vào MySQL.
  - Trong [`application.properties`](file:///d:/UTE/Nam_3_2026_2027_1/nam3/LTW/Bt29-9-2026/src/main/resources/application.properties), chỉ cần mở comment phần MySQL theo đúng slide của Thầy:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/jwt_springboot3?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false
    spring.datasource.username=root
    spring.datasource.password=1234567@a$
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    ```
  - Cơ chế `spring.jpa.hibernate.ddl-auto=update` và `DataInitializer` sẽ **tự động tạo bảng `users` và chèn tài khoản mẫu** nếu database còn trống, Thầy không cần thao tác thêm gì phức tạp.

---

## 4. TÀI KHOẢN TRẢI NGHIỆM TRÊN HỆ THỐNG
Hệ thống được thiết lập tự động khởi tạo các tài khoản sau khi khởi động:
| Tài khoản | Email | Mật khẩu | Quyền / Vai trò |
|---|---|---|---|
| **Sinh viên** | `24110359@student.hcmute.edu.vn` | `123456` | Người dùng chính (có avatar cá nhân) |
| **Giảng viên** | `trungnh@hcmute.edu.vn` | `123456` | Tài khoản mẫu theo bài giảng |

---

## 5. HƯỚNG DẪN CHẠY VÀ KIỂM THỬ

### Cách 1: Chạy kiểm thử tự động (Unit & Integration Tests)
Chạy lệnh sau tại thư mục gốc của dự án:
```bash
mvn clean test
```
Toàn bộ 8 bài test sẽ chạy thành công trên database in-memory H2:
- 5 unit tests kiểm tra Nimbus JWT (tạo token, đọc subject, kiểm tra token hợp lệ, phát hiện sai user, phát hiện token hết hạn).
- 3 integration tests kiểm tra MockMvc (đăng ký `/auth/signup`, đăng nhập `/auth/login`, gọi endpoint bảo vệ `/users/me`).

### Cách 2: Khởi động ứng dụng Spring Boot
```bash
mvn spring-boot:run
```
Hoặc chạy fat JAR đã đóng gói sẵn:
```bash
java -jar target/JWT_springboot3-0.0.1-SNAPSHOT.jar
```

### Cách 3: Trải nghiệm trên trình duyệt Web
1. Mở trình duyệt và truy cập: [http://localhost:8005/login](http://localhost:8005/login)
2. Form đăng nhập đã điền sẵn email `24110359@student.hcmute.edu.vn` và mật khẩu `123456`.
3. Bấm **Login**:
   - Hệ thống gửi Ajax POST đến `/auth/login`.
   - Nhận chuỗi Nimbus JWT Token và lưu vào `localStorage.token`.
   - Tự động chuyển hướng sang trang hồ sơ cá nhân: [http://localhost:8005/user/profile](http://localhost:8005/user/profile).
4. Tại trang profile:
   - Ajax tự động đính kèm header `Authorization: Bearer <token>` gọi API `/users/me`.
   - Hiển thị Họ tên **Nguyễn Minh Trí**, ảnh đại diện sinh viên và chuỗi Token đang hoạt động.
   - Hiển thị bảng danh sách tất cả người dùng trong hệ thống (gọi API `GET /users/`).
5. Bấm **Logout**: Token được xóa khỏi `localStorage` và chuyển về trang đăng nhập.

---

## 6. HƯỚNG DẪN ĐẨY LÊN GITHUB ĐỂ NỘP BÀI (KHI CẦN)
Khi bạn sẵn sàng nộp bài, hãy chạy các lệnh sau tại thư mục này:
```bash
git init
git add .
git commit -m "Hoàn thành bài tập JWT Spring Boot 3 với Nimbus JOSE+JWT - MSSV 24110359"
git branch -M main
git remote add origin <LINK_REPO_GITHUB_CUA_BAN>
git push -u origin main
```
Sau đó sao chép link repository và nộp lên hệ thống UTExLMS.
