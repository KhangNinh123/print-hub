package com.iuh.printshop.printshop_be.controller;

import com.iuh.printshop.printshop_be.dto.auth.AuthResponse;
import com.iuh.printshop.printshop_be.dto.auth.LoginRequest;
import com.iuh.printshop.printshop_be.dto.auth.RegisterRequest;
import com.iuh.printshop.printshop_be.dto.auth.RegisterResponse;
import com.iuh.printshop.printshop_be.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
//@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class                                                                                                                                                                                                                                                                    AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account with ROLE_CUSTOMER")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            RegisterResponse registerResponse = new RegisterResponse(
                    response.getId(),
                    response.getEmail(),
                    response.getFullName(),
                    response.getPhone(),
                    response.getDefaultAddress(),
                    response.getRoles()
            );
            return ResponseEntity.ok(registerResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return JWT token")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Generate new JWT token for authenticated user")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String email) {
        try {
            AuthResponse response = authService.refreshToken(email);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    
    @PostMapping("/verify-email")
    @Operation(summary = "Verify email with OTP", description = "Verify user email with OTP code and activate account")
    public ResponseEntity<String> verifyEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String otpCode = request.get("otpCode");
            
            if (email == null || otpCode == null) {
                return ResponseEntity.badRequest().body("Email và mã OTP không được để trống");
            }
            
            boolean verified = authService.verifyEmailWithOtp(email, otpCode);
            if (verified) {
                return ResponseEntity.ok("Tài khoản đã được kích hoạt thành công! Bạn có thể đăng nhập ngay bây giờ.");
            }
            return ResponseEntity.badRequest().body("Xác thực OTP thất bại");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend-verify")
    public ResponseEntity<String> resendVerificationEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.resendVerificationEmail(email);
        return ResponseEntity.ok("Email xác nhận đã được gửi lại!");
    }
    @GetMapping("/otp-status")
    @Operation(summary = "Check OTP status", description = "Check OTP status and remaining attempts")
    public ResponseEntity<Object> getOtpStatus(@RequestParam String email) {
        try {
            OtpStatusResponse response = new OtpStatusResponse();
            response.setHasActiveOtp(authService.hasActiveOtp(email));
            response.setAttempts(authService.getAttempts(email));
            response.setRemainingAttempts(5 - response.getAttempts());
            response.setTimeToLive(authService.getOtpTimeToLive(email));
            response.setIsBlocked(authService.isBlocked(email));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/user-status")
    @Operation(summary = "Check user status", description = "Check if user is activated and can login")
    public ResponseEntity<Object> getUserStatus(@RequestParam String email) {
        try {
            UserStatusResponse response = new UserStatusResponse();
            response.setEmail(email);
            response.setIsActive(authService.isUserActive(email));
            response.setCanLogin(response.getIsActive());
            response.setMessage(response.getIsActive() ? "Tài khoản đã được kích hoạt" : "Tài khoản chưa được kích hoạt");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Inner class for User status response
    public static class UserStatusResponse {
        private String email;
        private boolean isActive;
        private boolean canLogin;
        private String message;
        
        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public boolean getIsActive() { return isActive; }
        public void setIsActive(boolean isActive) { this.isActive = isActive; }
        
        public boolean getCanLogin() { return canLogin; }
        public void setCanLogin(boolean canLogin) { this.canLogin = canLogin; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    // Inner class for OTP status response
    public static class OtpStatusResponse {
        private boolean hasActiveOtp;
        private int attempts;
        private int remainingAttempts;
        private long timeToLive;
        private boolean isBlocked;
        
        // Getters and setters
        public boolean isHasActiveOtp() { return hasActiveOtp; }
        public void setHasActiveOtp(boolean hasActiveOtp) { this.hasActiveOtp = hasActiveOtp; }
        
        public int getAttempts() { return attempts; }
        public void setAttempts(int attempts) { this.attempts = attempts; }
        
        public int getRemainingAttempts() { return remainingAttempts; }
        public void setRemainingAttempts(int remainingAttempts) { this.remainingAttempts = remainingAttempts; }
        
        public long getTimeToLive() { return timeToLive; }
        public void setTimeToLive(long timeToLive) { this.timeToLive = timeToLive; }
        
        public boolean isIsBlocked() { return isBlocked; }
        public void setIsBlocked(boolean isBlocked) { this.isBlocked = isBlocked; }
    }
}
