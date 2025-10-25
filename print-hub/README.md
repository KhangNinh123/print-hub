# Print Hub

## Cấu trúc dự án

```
print-hub/
├── printshop-be/                    # Backend Spring Boot
│   ├── src/main/java/
│   │   └── com/iuh/printshop/printshop_be/
│   │       ├── config/              # Cấu hình ứng dụng
│   │       ├── controller/           # REST Controllers
│   │       ├── dto/                  # Data Transfer Objects
│   │       │   ├── auth/             # DTO cho authentication
│   │       │   └── user/             # DTO cho user
│   │       ├── entity/               # JPA Entities
│   │       ├── repository/           # JPA Repositories
│   │       ├── security/             # JWT Security
│   │       └── service/              # Business Logic
│   ├── src/main/resources/
│   │   └── application.yml           # Cấu hình ứng dụng
│   ├── src/test/java/                # Test files
│   ├── pom.xml                       # Maven dependencies
│   ├── printshop_mariadb.sql         # Database schema
│   ├── mvnw                          # Maven wrapper
│   └── mvnw.cmd                      # Maven wrapper (Windows)
```

## Mô tả

Print Hub là một nền tảng bán máy in và máy scan được xây dựng với Spring Boot.

### Backend (printshop-be)
- **Framework**: Spring Boot 3.5.6
- **Database**: MariaDB
- **Security**: JWT Authentication
- **Cache**: Redis
- **Documentation**: OpenAPI/Swagger

### Các tính năng chính
- Quản lý người dùng và xác thực
- Quản lý sản phẩm (máy in, máy scan)
- Quản lý thương hiệu và danh mục
- API RESTful với Swagger documentation

### Cách chạy dự án

1. **Cài đặt dependencies**:
   ```bash
   cd print-hub/printshop-be
   ./mvnw clean install
   ```

2. **Chạy ứng dụng**:
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Truy cập Swagger UI**:
   ```
   http://localhost:8080/swagger-ui.html
   ```
