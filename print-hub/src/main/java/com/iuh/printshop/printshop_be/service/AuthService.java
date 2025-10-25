package com.iuh.printshop.printshop_be.service;

import com.iuh.printshop.printshop_be.dto.auth.AuthResponse;
import com.iuh.printshop.printshop_be.dto.auth.LoginRequest;
import com.iuh.printshop.printshop_be.dto.auth.RegisterRequest;
import com.iuh.printshop.printshop_be.entity.Role;
import com.iuh.printshop.printshop_be.entity.User;
import com.iuh.printshop.printshop_be.repository.RoleRepository;
import com.iuh.printshop.printshop_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
//@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final OtpServiceInterface otpService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, EmailService emailService, OtpServiceInterface otpService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.otpService = otpService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setDefaultAddress(request.getDefaultAddress());
        user.setIsActive(false); // Tài khoản chưa kích hoạt, chờ nhập mã OTP

        // Assign default role (ROLE_CUSTOMER)
        Set<Role> roles = new HashSet<>();
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("ROLE_CUSTOMER not found"));
        roles.add(customerRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        try {
            // Generate OTP và lưu vào Redis/Memory, sau đó gửi email
            System.out.println("=== DEBUG REGISTRATION OTP ===");
            System.out.println("Generating OTP for email: " + request.getEmail());
            String otpCode = otpService.generateAndStoreOtp(request.getEmail());
            System.out.println("Generated OTP: " + otpCode);
            System.out.println("Sending email...");
            emailService.sendVerificationEmail(request.getEmail(), otpCode);
            System.out.println("Email sent successfully!");
            System.out.println("=== END DEBUG REGISTRATION OTP ===");
        } catch (Exception e) {
            // Nếu có lỗi, vẫn cho phép đăng ký nhưng không gửi OTP
            System.err.println("OTP service error during registration: " + e.getMessage());
            throw new RuntimeException("Không thể tạo mã OTP. Vui lòng thử lại sau.");
        }

        // Return response without JWT token (user needs to verify OTP first)
        return new AuthResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getPhone(),
                savedUser.getDefaultAddress(),
                savedUser.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Get user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmailWithRoles(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if account is activated
            if (!user.getIsActive()) {
                throw new RuntimeException("Tài khoản chưa được kích hoạt. Vui lòng xác thực email trước.");
            }

            // Generate JWT token
            String token = jwtService.generateToken(userDetails);

            return new AuthResponse(
                    token,
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getPhone(),
                    user.getDefaultAddress(),
                    user.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toSet())
            );
        } catch (Exception e) {
            // Log the actual error for debugging
            System.err.println("Login error: " + e.getMessage());
            throw new RuntimeException("Đăng nhập thất bại: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String email) {
        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getDefaultAddress(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
    }
    
    
    @Transactional
    public boolean verifyEmailWithOtp(String email, String otpCode) {
        System.out.println("=== DEBUG OTP VERIFICATION ===");
        System.out.println("Email: " + email);
        System.out.println("OTP Code: " + otpCode);
        
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        System.out.println("User found: " + user.getEmail());
        System.out.println("User isActive: " + user.getIsActive());

        // Kiểm tra có bị khóa do thử sai quá nhiều không
        if (otpService.isBlocked(email)) {
            System.out.println("User is blocked");
            throw new RuntimeException("Tài khoản đã bị khóa do nhập sai OTP quá nhiều lần. Vui lòng thử lại sau 30 phút.");
        }

        System.out.println("User is not blocked");
        System.out.println("Has active OTP: " + otpService.hasActiveOtp(email));
        System.out.println("Attempts: " + otpService.getAttempts(email));

        // Xác thực OTP với Redis/Memory
        boolean isValid = otpService.verifyOtp(email, otpCode);
        
        System.out.println("OTP verification result: " + isValid);
        
        if (!isValid) {
            int attempts = otpService.getAttempts(email);
            int remainingAttempts = 5 - attempts;
            System.out.println("OTP verification failed. Remaining attempts: " + remainingAttempts);
            throw new RuntimeException("Mã OTP không đúng. Bạn còn " + remainingAttempts + " lần thử.");
        }

        // Kích hoạt tài khoản: chỉ cần is_active = true
        user.setIsActive(true);
        userRepository.save(user);

        System.out.println("Account activated successfully!");
        System.out.println("=== END DEBUG ===");
        return true;
    }
    
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));

        if (user.getIsActive()) {
            throw new RuntimeException("Tài khoản đã được kích hoạt");
        }

        // Kiểm tra có bị khóa do thử sai quá nhiều không
        if (otpService.isBlocked(email)) {
            throw new RuntimeException("Tài khoản đã bị khóa do nhập sai OTP quá nhiều lần. Vui lòng thử lại sau 30 phút.");
        }

        // Generate new OTP và lưu vào Redis (ghi đè OTP cũ), sau đó gửi email
        String otpCode = otpService.generateAndStoreOtp(email);
        emailService.sendVerificationEmail(email, otpCode);
    }
    
    // Các method để hỗ trợ OTP status endpoint
    public boolean hasActiveOtp(String email) {
        return otpService.hasActiveOtp(email);
    }
    
    public int getAttempts(String email) {
        return otpService.getAttempts(email);
    }
    
    public long getOtpTimeToLive(String email) {
        return otpService.getOtpTimeToLive(email);
    }
    
    public boolean isBlocked(String email) {
        return otpService.isBlocked(email);
    }
    
    public boolean isUserActive(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));
        return user.getIsActive();
    }
    
}
