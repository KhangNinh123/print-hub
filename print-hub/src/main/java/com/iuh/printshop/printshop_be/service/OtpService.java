package com.iuh.printshop.printshop_be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
//@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.otp.fallback", havingValue = "false", matchIfMissing = false)
public class OtpService implements OtpServiceInterface {

    private final RedisTemplate<String, Object> redisTemplate;

    public OtpService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private final Random random = new Random();
    
    // Constants
    private static final String OTP_PREFIX = "otp:";
    private static final String ATTEMPTS_PREFIX = "attempts:";
    private static final int OTP_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration OTP_EXPIRY = Duration.ofMinutes(10);
    private static final Duration ATTEMPTS_EXPIRY = Duration.ofMinutes(30);
    
    /**
     * Tạo và lưu mã OTP mới cho email
     */
    public String generateAndStoreOtp(String email) {
        // Xóa OTP cũ nếu có
        deleteOtp(email);
        
        // Tạo mã OTP mới
        String otpCode = generateOtpCode();
        
        // Lưu OTP vào Redis với thời gian hết hạn
        String otpKey = OTP_PREFIX + email;
        redisTemplate.opsForValue().set(otpKey, otpCode, OTP_EXPIRY);
        
        // Reset số lần thử sai
        resetAttempts(email);
        
        return otpCode;
    }
    
    /**
     * Xác thực mã OTP
     */
    public boolean verifyOtp(String email, String inputOtp) {
        String otpKey = OTP_PREFIX + email;
        String storedOtp = (String) redisTemplate.opsForValue().get(otpKey);
        
        // Kiểm tra OTP có tồn tại không
        if (storedOtp == null) {
            incrementAttempts(email);
            return false;
        }
        
        // Kiểm tra số lần thử sai
        if (getAttempts(email) >= MAX_ATTEMPTS) {
            return false;
        }
        
        // So sánh OTP
        boolean isValid = storedOtp.equals(inputOtp);
        
        if (isValid) {
            // Xóa OTP và attempts khi xác thực thành công
            deleteOtp(email);
            deleteAttempts(email);
        } else {
            // Tăng số lần thử sai
            incrementAttempts(email);
        }
        
        return isValid;
    }
    
    /**
     * Kiểm tra OTP có tồn tại và chưa hết hạn không
     */
    public boolean hasActiveOtp(String email) {
        String otpKey = OTP_PREFIX + email;
        return redisTemplate.hasKey(otpKey);
    }
    
    /**
     * Lấy số lần thử sai
     */
    public int getAttempts(String email) {
        String attemptsKey = ATTEMPTS_PREFIX + email;
        Object attempts = redisTemplate.opsForValue().get(attemptsKey);
        return attempts != null ? (Integer) attempts : 0;
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
        String otpKey = OTP_PREFIX + email;
        return redisTemplate.getExpire(otpKey, TimeUnit.SECONDS);
    }
    
    /**
     * Xóa OTP
     */
    public void deleteOtp(String email) {
        String otpKey = OTP_PREFIX + email;
        redisTemplate.delete(otpKey);
    }
    
    /**
     * Xóa attempts
     */
    public void deleteAttempts(String email) {
        String attemptsKey = ATTEMPTS_PREFIX + email;
        redisTemplate.delete(attemptsKey);
    }
    
    /**
     * Reset attempts về 0
     */
    public void resetAttempts(String email) {
        String attemptsKey = ATTEMPTS_PREFIX + email;
        redisTemplate.opsForValue().set(attemptsKey, 0, ATTEMPTS_EXPIRY);
    }
    
    /**
     * Tăng số lần thử sai
     */
    private void incrementAttempts(String email) {
        String attemptsKey = ATTEMPTS_PREFIX + email;
        redisTemplate.opsForValue().increment(attemptsKey);
        redisTemplate.expire(attemptsKey, ATTEMPTS_EXPIRY);
    }
    
    /**
     * Tạo mã OTP ngẫu nhiên
     */
    private String generateOtpCode() {
        return String.format("%0" + OTP_LENGTH + "d", random.nextInt((int) Math.pow(10, OTP_LENGTH)));
    }
}
