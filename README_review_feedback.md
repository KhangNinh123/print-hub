# Print Hub - Review & Feedback API

## Mô tả
Module Review & Feedback cung cấp API cho phép người dùng **đánh giá sản phẩm** và **gửi phản hồi** về trải nghiệm dịch vụ trên hệ thống Print Hub.

## Tính năng chính
- **Đánh giá sản phẩm (Review)**: Khách hàng có thể tạo, cập nhật, xem hoặc xoá đánh giá cho từng sản phẩm.
- **Phản hồi (Feedback)**: Gửi phản hồi về sản phẩm, cửa hàng hoặc đơn hàng.
- **Tính điểm trung bình**: Hệ thống tự động tính điểm rating trung bình của từng sản phẩm.
- **Phân trang dữ liệu**: Hỗ trợ lấy danh sách review và feedback theo trang.
- **Quản lý bởi Admin**: Admin có thể xem toàn bộ feedback hoặc review để kiểm duyệt.

## Công nghệ sử dụng
- **Backend**: Spring Boot 3.x, Spring Data JPA
- **Database**: MariaDB
- **Validation**: Hibernate Validator
- **Authentication (tuỳ chọn)**: Spring Security / JWT
- **API Testing**: Postman
- **Documentation**: Swagger UI

## Cấu trúc dự án
```
print-hub/
├── printshop-be/
│   ├── src/main/java/
│   │   └── com/iuh/printshop/printshop_be/
│   │       ├── controller/
│   │       │   ├── ReviewController.java      # REST API cho review
│   │       │   └── FeedbackController.java    # REST API cho feedback
│   │       ├── entity/
│   │       │   ├── Review.java                # Entity đánh giá
│   │       │   └── Feedback.java              # Entity phản hồi
│   │       ├── repository/
│   │       │   ├── ReviewRepository.java
│   │       │   └── FeedbackRepository.java
│   │       ├── service/
│   │       │   ├── ReviewService.java
│   │       │   └── FeedbackService.java
│   │       └── dto/
│   │           ├── ReviewDTO.java
│   │           └── FeedbackDTO.java
│   └── src/main/resources/application.yml
└── printshop_mariadb.sql
```

## API Endpoints

### 📦 Review API
| Method | Endpoint | Mô tả |
|--------|-----------|-------|
| **GET** | `/api/products/{productId}/reviews?page=0&size=10` | Lấy danh sách review của sản phẩm |
| **GET** | `/api/products/{productId}/rating` | Tính trung bình rating của sản phẩm |
| **POST** | `/api/products/{productId}/reviews` | Tạo review mới |
| **PUT** | `/api/reviews/{id}` | Cập nhật review |
| **DELETE** | `/api/reviews/{id}` | Xoá review |

**Payload tạo review**
```json
{
  "rating": 5,
  "title": "Tốt",
  "content": "In rõ, giao nhanh"
}
```

**Response ví dụ**
```json
{
  "id": 1,
  "productId": 1,
  "userId": 2,
  "rating": 5,
  "title": "Tốt",
  "content": "In rõ, giao nhanh",
  "createdAt": "2025-10-24T15:08:26Z"
}
```

### 💬 Feedback API
| Method | Endpoint | Mô tả |
|--------|-----------|-------|
| **POST** | `/api/feedbacks` | Gửi feedback |
| **GET** | `/api/me/feedbacks?page=0&size=10` | Lấy feedback của người dùng hiện tại |
| **GET** | `/api/feedbacks?page=0&size=10` | Admin xem toàn bộ feedback |
| **GET** | `/api/orders/{orderId}/feedbacks` | Xem feedback theo đơn hàng |

**Payload gửi feedback**
```json
{
  "type": "SHOP",
  "subject": "Phục vụ tốt",
  "content": "Nhân viên nhiệt tình",
  "rating": 5,
  "orderId": null
}
```

**Response ví dụ**
```json
{
  "id": 1,
  "userId": 2,
  "type": "SHOP",
  "subject": "Phục vụ tốt",
  "content": "Nhân viên nhiệt tình",
  "rating": 5,
  "createdAt": "2025-10-24T15:08:26Z"
}
```

## Ràng buộc dữ liệu
- `rating`: Bắt buộc, từ 1 đến 5
- `title`: Không quá 120 ký tự
- `type`: Chỉ nhận `SHOP`, `PRODUCT`, hoặc `ORDER`
- Review chỉ 1 user/1 sản phẩm (ràng buộc unique)

## Mã lỗi phổ biến
| Mã lỗi | Nguyên nhân | Cách xử lý |
|--------|--------------|------------|
| 400 | Payload sai hoặc thiếu trường | Kiểm tra JSON body |
| 404 | Không tìm thấy product/review/order | Kiểm tra ID |
| 409 | Người dùng đã review sản phẩm này | Bắt lỗi trùng dữ liệu |
| 500 | Lỗi hệ thống | Kiểm tra log trong console |

## Cài đặt & chạy thử
```bash
./mvnw spring-boot:run
```
Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

Postman Collection: `PrintShop API (Review & Feedback).postman_collection.json`

---
© 2025 - Print Hub | Backend Review & Feedback Module
