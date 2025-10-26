package com.iuh.printshop.printshop_be.service;

public interface OtpServiceInterface {
    
    /**
     * Tạo và lưu mã OTP cho email
     */
    String generateAndStoreOtp(String email);
    
    /**
     * Xác thực mã OTP
     */
    boolean verifyOtp(String email, String inputOtp);
    
    /**
     * Kiểm tra OTP có tồn tại và chưa hết hạn không
     */
    boolean hasActiveOtp(String email);
    
    /**
     * Lấy số lần thử sai
     */
    int getAttempts(String email);
    
    /**
     * Kiểm tra có bị khóa do thử sai quá nhiều không
     */
    boolean isBlocked(String email);
    
    /**
     * Lấy thời gian còn lại của OTP (giây)
     */
    long getOtpTimeToLive(String email);
    
    /**
     * Xóa OTP
     */
    void deleteOtp(String email);
    
    /**
     * Xóa attempts
     */
    void deleteAttempts(String email);
    
    /**
     * Reset attempts về 0
     */
    void resetAttempts(String email);
}
