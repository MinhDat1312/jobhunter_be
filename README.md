# JobHunter Backend

[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-green)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue)](https://www.mysql.com/)
[![Gradle](https://img.shields.io/badge/Gradle-Kotlin%20DSL-brightgreen)](https://gradle.org/)

Hệ thống backend cho ứng dụng tìm kiếm việc làm JobHunter, được xây dựng bằng Spring Boot với các tính năng hiện đại và bảo mật cao.

## 📋 Mục lục

- [Tính năng](#-tính-năng)
- [Công nghệ sử dụng](#-công-nghệ-sử-dụng)
- [Yêu cầu hệ thống](#-yêu-cầu-hệ-thống)
- [Cài đặt và chạy](#-cài-đặt-và-chạy)
- [Cấu hình](#-cấu-hình)
- [API Documentation](#-api-documentation)
- [Cấu trúc dự án](#-cấu-trúc-dự-án)
- [Cơ sở dữ liệu](#-cơ-sở-dữ-liệu)
- [Bảo mật](#-bảo-mật)
- [Đóng góp](#-đóng-góp)

## 🚀 Tính năng

### Quản lý người dùng

- ✅ Đăng ký, đăng nhập với JWT Authentication
- ✅ Phân quyền người dùng (Role-based Access Control)
- ✅ Quản lý hồ sơ cá nhân
- ✅ Xác thực email

### Quản lý công việc

- ✅ Đăng tải và quản lý tin tuyển dụng
- ✅ Tìm kiếm và lọc công việc theo nhiều tiêu chí
- ✅ Ứng tuyển công việc
- ✅ Theo dõi trạng thái ứng tuyển

### Tính năng nâng cao

- ✅ Upload file CV với Cloudinary
- ✅ Gửi email thông báo
- ✅ Dashboard thống kê
- ✅ Hệ thống phân quyền chi tiết
- ✅ API Documentation với OpenAPI/Swagger

## 🛠 Công nghệ sử dụng

### Backend Framework

- **Spring Boot 3.4.5** - Framework chính
- **Spring Security** - Bảo mật và xác thực
- **Spring Data JPA** - ORM và quản lý database
- **Spring OAuth2 Resource Server** - JWT Authentication

### Database & ORM

- **MySQL** - Cơ sở dữ liệu chính
- **Hibernate** - ORM implementation

### Các thư viện khác

- **Lombok** - Giảm boilerplate code
- **SpringDoc OpenAPI** - API Documentation
- **Cloudinary** - Cloud storage cho file upload
- **Spring Mail** - Gửi email
- **Thymeleaf** - Template engine
- **Commons IO** - Xử lý file I/O

## 💻 Yêu cầu hệ thống

- **Java**: 21 hoặc cao hơn
- **MySQL**: 8.0 hoặc cao hơn
- **Gradle**: 7.x hoặc cao hơn (hoặc sử dụng Gradle Wrapper)
- **RAM**: Tối thiểu 4GB
- **Disk**: Tối thiểu 2GB trống

## 🔧 Cài đặt và chạy

### 1. Clone repository

```bash
git clone https://github.com/MinhDat1312/jobhunter_be.git
cd jobhunter_be
```

### 2. Cấu hình database

Tạo database MySQL:

```sql
CREATE DATABASE job_hunter;
```

### 3. Cấu hình application.properties

Sao chép và chỉnh sửa file cấu hình:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Cập nhật thông tin database và các cấu hình khác trong `application.properties`.

### 4. Chạy ứng dụng

Sử dụng Gradle Wrapper:

```bash
# Windows
.\gradlew bootRun

# Linux/Mac
./gradlew bootRun
```

Hoặc build và chạy JAR:

```bash
.\gradlew build
java -jar build/libs/jobhunter_be-0.0.1-SNAPSHOT.jar
```

Ứng dụng sẽ chạy trên: `http://localhost:8080`

## ⚙️ Cấu hình

### Database Configuration

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/job_hunter
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### JWT Configuration

```properties
minhdat.jwt.base64-secret=your_jwt_secret
minhdat.jwt.access-token-validity-in-seconds=900
minhdat.jwt.refresh-token-validity-in-seconds=8640000
```

### Email Configuration

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

### File Upload Configuration

```properties
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
minhdat.upload-file.base-uri=file:///path/to/upload/directory/
```

## 📚 API Documentation

API documentation được tự động tạo bằng OpenAPI/Swagger.

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### Các endpoint chính:

- **Authentication**: `/api/v1/auth/*`
- **Users**: `/api/v1/users/*`
- **Jobs**: `/api/v1/jobs/*`
- **Applications**: `/api/v1/applications/*`
- **Skills**: `/api/v1/skills/*`
- **Careers**: `/api/v1/careers/*`

## 📁 Cấu trúc dự án

```
src/main/java/vn/minhdat/jobhunter_be/
├── common/              # Enums và constants
├── config/              # Cấu hình Spring
├── controller/          # REST Controllers
├── dto/                 # Data Transfer Objects
├── entity/              # JPA Entities
├── exception/           # Exception handlers
├── repository/          # JPA Repositories
├── service/             # Business logic
└── util/                # Utility classes

src/main/resources/
├── application.properties
├── static/              # Static resources
└── templates/           # Thymeleaf templates
```

## 🗄️ Cơ sở dữ liệu

### Các entity chính:

- **User**: Người dùng hệ thống
- **Role**: Vai trò người dùng
- **Permission**: Quyền hạn
- **Job**: Tin tuyển dụng
- **Application**: Đơn ứng tuyển
- **Skill**: Kỹ năng
- **Career**: Ngành nghề
- **Recruiter**: Nhà tuyển dụng
- **Applicant**: Ứng viên

### ERD (Entity Relationship Diagram)

Database được thiết kế với các mối quan hệ:

- User - Role (Many-to-Many)
- Role - Permission (Many-to-Many)
- Job - Skill (Many-to-Many)
- User - Application - Job (Many-to-Many qua bảng trung gian)

## 🔐 Bảo mật

### Authentication

- JWT (JSON Web Token) cho xác thực
- Access token: 15 phút
- Refresh token: 7 ngày

### Authorization

- Role-based Access Control (RBAC)
- Permission-based fine-grained access control
- Method-level security với `@PreAuthorize`

### Data Security

- Password hashing với BCrypt
- CORS configuration
- Input validation với Bean Validation

### Coding Standards

- Sử dụng Java 21 features
- Follow Spring Boot best practices
- Viết unit tests cho các service methods
- Comment code rõ ràng bằng tiếng Việt hoặc tiếng Anh

## 📞 Liên hệ

- Email: nguyenthangminhdat45392@gmail.com
- GitHub: [@MinhDat1312](https://github.com/MinhDat1312)

---
