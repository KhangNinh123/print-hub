# Hướng dẫn kiểm tra trạng thái User sau khi xác thực OTP

## 🎉 **Chúc mừng! Xác thực OTP thành công!**

Bạn đã xác thực thành công và user đã được kích hoạt trong database. Bây giờ hãy kiểm tra trạng thái:

## 🔍 **Kiểm tra trạng thái User:**

### **1. Kiểm tra User Status:**
```bash
GET /api/auth/user-status?email=huynhtannha2003@gmail.com
```

**Response mong đợi:**
```json
{
    "email": "huynhtannha2003@gmail.com",
    "isActive": true,
    "canLogin": true,
    "message": "Tài khoản đã được kích hoạt"
}
```

### **2. Kiểm tra OTP Status:**
```bash
GET /api/auth/otp-status?email=huynhtannha2003@gmail.com
```

**Response mong đợi:**
```json
{
    "hasActiveOtp": false,
    "attempts": 0,
    "remainingAttempts": 5,
    "timeToLive": 0,
    "isBlocked": false
}
```

## 🚀 **Test đăng nhập:**

### **Đăng nhập với tài khoản đã kích hoạt:**
```json
POST /api/auth/login
{
    "email": "huynhtannha2003@gmail.com",
    "password": "password123"
}
```

**Response mong đợi:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "id": 1,
    "email": "huynhtannha2003@gmail.com",
    "fullName": "Test User",
    "phone": "0123456789",
    "defaultAddress": "123 Test Street",
    "roles": ["ROLE_CUSTOMER"]
}
```

## 📋 **Quy trình hoàn chỉnh:**

### **1. Đăng ký:**
```json
POST /api/auth/register
{
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User",
    "phone": "0123456789",
    "defaultAddress": "123 Test Street"
}
```

### **2. Xác thực OTP:**
```json
POST /api/auth/verify-email
{
    "email": "test@example.com",
    "otpCode": "123456"
}
```

### **3. Kiểm tra trạng thái:**
```bash
GET /api/auth/user-status?email=test@example.com
```

### **4. Đăng nhập:**
```json
POST /api/auth/login
{
    "email": "test@example.com",
    "password": "password123"
}
```

## 🎯 **Các trạng thái User:**

### **Trước khi xác thực OTP:**
```json
{
    "email": "test@example.com",
    "isActive": false,
    "canLogin": false,
    "message": "Tài khoản chưa được kích hoạt"
}
```

### **Sau khi xác thực OTP:**
```json
{
    "email": "test@example.com",
    "isActive": true,
    "canLogin": true,
    "message": "Tài khoản đã được kích hoạt"
}
```

## 🔒 **Logic bảo mật:**

### **1. Trước khi xác thực:**
- ✅ **is_active = false** - Tài khoản chưa kích hoạt
- ✅ **Không thể đăng nhập** - Spring Security chặn
- ✅ **Cần xác thực OTP** - Để kích hoạt tài khoản

### **2. Sau khi xác thực:**
- ✅ **is_active = true** - Tài khoản đã kích hoạt
- ✅ **Có thể đăng nhập** - Spring Security cho phép
- ✅ **Nhận JWT token** - Để truy cập các API khác

## 🎉 **Kết quả:**

- ✅ **User đã được kích hoạt** trong database
- ✅ **is_active = true** - Có thể đăng nhập
- ✅ **OTP đã được xóa** - Bảo mật
- ✅ **Có thể đăng nhập** - Nhận JWT token

## 🚀 **Bước tiếp theo:**

1. **Kiểm tra trạng thái user** - Xác nhận đã kích hoạt
2. **Test đăng nhập** - Nhận JWT token
3. **Sử dụng JWT token** - Truy cập các API khác
4. **Xóa debug logging** - Nếu không cần thiết

Bây giờ bạn có thể **đăng nhập và sử dụng ứng dụng** bình thường! 🎯
