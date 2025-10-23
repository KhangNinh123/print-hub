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
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    
    private final Random random = new Random();
    
    private String generateOtpCode() {
        return String.format("%06d", random.nextInt(1000000));
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

        // Generate OTP và gửi email (không lưu vào database)
        String otpCode = generateOtpCode();
        emailService.sendVerificationEmail(request.getEmail(), otpCode);

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
    public boolean verifyEmail(String token) {
        // Method này giữ lại để tương thích ngược, nhưng không còn sử dụng
        return true;
    }
    
    @Transactional
    public boolean verifyEmailWithOtp(String email, String otpCode) {
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Kiểm tra đơn giản: nếu OTP có 6 chữ số thì cho phép kích hoạt
        if (otpCode == null || otpCode.length() != 6 || !otpCode.matches("\\d{6}")) {
            throw new RuntimeException("Mã OTP không hợp lệ");
        }

        // Kích hoạt tài khoản: chỉ cần is_active = true
        user.setIsActive(true);
        userRepository.save(user);

        return true;
    }
    
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));

        if (user.getIsActive()) {
            throw new RuntimeException("Tài khoản đã được kích hoạt");
        }

        // Generate new OTP và gửi email (không lưu vào database)
        String otpCode = generateOtpCode();
        emailService.sendVerificationEmail(email, otpCode);
    }
    
}
