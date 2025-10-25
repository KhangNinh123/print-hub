# Print Hub - Backend API

## Mô tả
Print Hub là một nền tảng web giới thiệu và bán máy in/máy scan với hệ thống backend API được xây dựng bằng Spring Boot.

## Tính năng chính
- **Quản lý sản phẩm**: Brand, Category, Product với đầy đủ CRUD operations
- **Hệ thống xác thực**: JWT-based authentication với phân quyền Admin/Customer
- **Quản lý người dùng**: Đăng ký, đăng nhập, quản lý profile
- **Upload file**: Hỗ trợ upload hình ảnh sản phẩm
- **API Documentation**: Swagger UI tích hợp sẵn

## Công nghệ sử dụng
- **Backend**: Spring Boot 3.5.6, Spring Security, Spring Data JPA
- **Database**: MariaDB
- **Authentication**: JWT (JSON Web Token)
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Build Tool**: Maven
- **Java Version**: 21

## Cấu trúc dự án
```
print-hub/
├── printshop-be/                 # Backend Spring Boot
│   ├── src/main/java/
│   │   └── com/iuh/printshop/printshop_be/
│   │       ├── config/          # Cấu hình ứng dụng
│   │       ├── controller/      # REST Controllers
│   │       ├── dto/             # Data Transfer Objects
│   │       ├── entity/         # JPA Entities
│   │       ├── repository/     # JPA Repositories
│   │       ├── security/       # JWT Security
│   │       └── service/        # Business Logic
│   ├── src/main/resources/
│   │   └── application.yml     # Cấu hình ứng dụng
│   └── pom.xml                # Maven dependencies
└── printshop_mariadb.sql      # Database schema
```

## Cài đặt và chạy

### Yêu cầu hệ thống
- Java 21+
- MariaDB
- Maven 

### Cài đặt Database
1. Tạo database MariaDB
2. Chạy script `printshop_mariadb.sql` để tạo schema và dữ liệu mẫu

### Cấu hình ứng dụng
Chỉnh sửa file `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/printshop
    username: your_username
    password: your_password
```

### Chạy ứng dụng
```bash
cd printshop-be
./mvnw spring-boot:run
```

Ứng dụng sẽ chạy tại: http://localhost:8080

### API Documentation
Swagger UI: http://localhost:8080/swagger-ui.html

## API Endpoints chính

### Authentication
- `POST /api/auth/register` - Đăng ký tài khoản
- `POST /api/auth/login` - Đăng nhập

### Products
- `GET /api/products` - Lấy danh sách sản phẩm
- `GET /api/products/{id}` - Lấy chi tiết sản phẩm
- `POST /api/products` - Tạo sản phẩm mới (Admin)
- `PUT /api/products/{id}` - Cập nhật sản phẩm (Admin)
- `DELETE /api/products/{id}` - Xóa sản phẩm (Admin)

### Brands & Categories
- `GET /api/brands` - Lấy danh sách thương hiệu
- `GET /api/categories` - Lấy danh sách danh mục

### Users (Admin only)
- `GET /api/users` - Lấy danh sách người dùng

## Database Schema
- **users**: Thông tin người dùng
- **roles**: Vai trò (ADMIN, CUSTOMER)
- **user_roles**: Liên kết user-role
- **brands**: Thương hiệu sản phẩm
- **categories**: Danh mục sản phẩm
- **products**: Thông tin sản phẩm
- **product_images**: Hình ảnh sản phẩm
- **orders**: Đơn hàng
- **order_items**: Chi tiết đơn hàng
