package com.iuh.printshop.printshop_be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.otp.fallback", havingValue = "true", matchIfMissing = true)
public class OtpServiceFallback implements OtpServiceInterface {

    private final Random random = new Random();
    
    // Constants
    private static final int OTP_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 5;
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int ATTEMPTS_EXPIRY_MINUTES = 30;
    
    // In-memory storage (thay thế Redis)
    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, AttemptsData> attemptsStorage = new ConcurrentHashMap<>();
    
    // Inner classes for data storage
    private static class OtpData {
        private final String otpCode;
        private final LocalDateTime expiryTime;
        
        public OtpData(String otpCode, LocalDateTime expiryTime) {
            this.otpCode = otpCode;
            this.expiryTime = expiryTime;
        }
        
        public String getOtpCode() { return otpCode; }
        public LocalDateTime getExpiryTime() { return expiryTime; }
        public boolean isExpired() { return LocalDateTime.now().isAfter(expiryTime); }
    }
    
    private static class AttemptsData {
        private final int attempts;
        private final LocalDateTime expiryTime;
        
        public AttemptsData(int attempts, LocalDateTime expiryTime) {
            this.attempts = attempts;
            this.expiryTime = expiryTime;
        }
        
        public int getAttempts() { return attempts; }
        public LocalDateTime getExpiryTime() { return expiryTime; }
        public boolean isExpired() { return LocalDateTime.now().isAfter(expiryTime); }
    }
    
    /**
     * Tạo và lưu mã OTP vào memory
     */
    public String generateAndStoreOtp(String email) {
        // Xóa OTP cũ nếu có
        deleteOtp(email);
        
        // Tạo mã OTP mới
        String otpCode = generateOtpCode();
        
        // Lưu OTP vào memory với thời gian hết hạn
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        otpStorage.put(email, new OtpData(otpCode, expiryTime));
        
        // Reset số lần thử sai
        resetAttempts(email);
        
        return otpCode;
    }
    
    /**
     * Xác thực mã OTP
     */
    public boolean verifyOtp(String email, String inputOtp) {
        System.out.println("=== DEBUG OTP SERVICE FALLBACK ===");
        System.out.println("Email: " + email);
        System.out.println("Input OTP: " + inputOtp);
        
        OtpData otpData = otpStorage.get(email);
        
        // Kiểm tra OTP có tồn tại không
        if (otpData == null) {
            System.out.println("No OTP data found for email: " + email);
            incrementAttempts(email);
            return false;
        }
        
        System.out.println("OTP data found: " + otpData.getOtpCode());
        System.out.println("OTP expiry time: " + otpData.getExpiryTime());
        System.out.println("Current time: " + LocalDateTime.now());
        System.out.println("Is OTP expired: " + otpData.isExpired());
        
        // Kiểm tra OTP có hết hạn không
        if (otpData.isExpired()) {
            System.out.println("OTP is expired, deleting...");
            deleteOtp(email);
            incrementAttempts(email);
            return false;
        }
        
        // Kiểm tra số lần thử sai
        if (getAttempts(email) >= MAX_ATTEMPTS) {
            System.out.println("User exceeded max attempts");
            return false;
        }
        
        // So sánh OTP
        boolean isValid = otpData.getOtpCode().equals(inputOtp);
        System.out.println("OTP comparison result: " + isValid);
        System.out.println("Stored OTP: " + otpData.getOtpCode());
        System.out.println("Input OTP: " + inputOtp);
        
        if (isValid) {
            // Xóa OTP và attempts khi xác thực thành công
            System.out.println("OTP verification successful, cleaning up...");
            deleteOtp(email);
            deleteAttempts(email);
        } else {
            // Tăng số lần thử sai
            System.out.println("OTP verification failed, incrementing attempts...");
            incrementAttempts(email);
        }
        
        System.out.println("=== END DEBUG OTP SERVICE FALLBACK ===");
        return isValid;
    }
    
    /**
     * Kiểm tra OTP có tồn tại và chưa hết hạn không
     */
    public boolean hasActiveOtp(String email) {
        OtpData otpData = otpStorage.get(email);
        return otpData != null && !otpData.isExpired();
    }
    
    /**
     * Lấy số lần thử sai
     */
    public int getAttempts(String email) {
        AttemptsData attemptsData = attemptsStorage.get(email);
        if (attemptsData == null || attemptsData.isExpired()) {
            return 0;
        }
        return attemptsData.getAttempts();
    }
    
    /**
     * Kiểm tra có bị khóa do thử sai quá nhiều không
     */
    public boolean isBlocked(String email) {
        return getAttempts(email) >= MAX_ATTEMPTS;
    }
    
    /**
     * Lấy thời gian còn lại của OTP (giây)
     */
    public long getOtpTimeToLive(String email) {
        OtpData otpData = otpStorage.get(email);
        if (otpData == null || otpData.isExpired()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), otpData.getExpiryTime()).getSeconds();
    }
    
    /**
     * Xóa OTP
     */
    public void deleteOtp(String email) {
        otpStorage.remove(email);
    }
    
    /**
     * Xóa attempts
     */
    public void deleteAttempts(String email) {
        attemptsStorage.remove(email);
    }
    
    /**
     * Reset attempts về 0
     */
    public void resetAttempts(String email) {
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(ATTEMPTS_EXPIRY_MINUTES);
        attemptsStorage.put(email, new AttemptsData(0, expiryTime));
    }
    
    /**
     * Tăng số lần thử sai
     */
    private void incrementAttempts(String email) {
        int currentAttempts = getAttempts(email);
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(ATTEMPTS_EXPIRY_MINUTES);
        attemptsStorage.put(email, new AttemptsData(currentAttempts + 1, expiryTime));
    }
    
    /**
     * Tạo mã OTP ngẫu nhiên
     */
    private String generateOtpCode() {
        return String.format("%0" + OTP_LENGTH + "d", random.nextInt((int) Math.pow(10, OTP_LENGTH)));
    }
}
