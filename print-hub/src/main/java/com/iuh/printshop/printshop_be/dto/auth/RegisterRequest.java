package com.iuh.printshop.printshop_be.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 120, message = "Họ tên không được quá 120 ký tự")
    private String fullName;

    @Size(max = 20, message = "Số điện thoại không được quá 20 ký tự")
    private String phone;

    @Size(max = 255, message = "Địa chỉ không được quá 255 ký tự")
    private String defaultAddress;
}
