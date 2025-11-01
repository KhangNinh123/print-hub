package com.iuh.printshop.printshop_be.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private Integer id;
    private String email;
    private String fullName;
    private String phone;
    private String defaultAddress;
    private Set<String> roles;
    private String message;
    
    public RegisterResponse(Integer id, String email, String fullName, String phone, String defaultAddress, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.defaultAddress = defaultAddress;
        this.roles = roles;
        this.message = "Đăng ký thành công! Vui lòng kiểm tra email để xác nhận tài khoản.";
    }
}


