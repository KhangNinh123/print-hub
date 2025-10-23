package com.iuh.printshop.printshop_be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${app.email.from}")
    private String fromEmail;
    
    public void sendVerificationEmail(String email, String otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Mã xác thực OTP - Print Shop");
            helper.setText(buildVerificationEmailContent(otpCode), true); // true = HTML content
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email xác nhận: " + e.getMessage());
        }
    }
    
    private String buildVerificationEmailContent(String otpCode) {
        return """
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 8px; }
                    .otp-container { background-color: #e3f2fd; padding: 30px; margin: 20px 0; text-align: center; border-radius: 8px; border: 2px solid #2196f3; }
                    .otp-code { font-size: 48px; font-weight: bold; color: #1976d2; letter-spacing: 8px; margin: 20px 0; font-family: 'Courier New', monospace; }
                    .otp-label { font-size: 18px; color: #1976d2; font-weight: bold; margin-bottom: 10px; }
                    .content { padding: 20px; }
                    .footer { background-color: #f8f9fa; padding: 15px; text-align: center; border-radius: 8px; margin-top: 20px; font-size: 14px; color: #666; }
                    .warning { background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 15px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1 style="color: #1976d2; margin: 0;">Print Shop</h1>
                        <p style="margin: 10px 0 0 0; color: #666;">Xác thực địa chỉ email</p>
                    </div>
                    
                    <div class="content">
                        <h2>Xin chào!</h2>
                        <p>Cảm ơn bạn đã đăng ký tài khoản tại <strong>Print Shop</strong>.</p>
                        
                        <p>Để kích hoạt tài khoản của bạn, vui lòng sử dụng mã OTP bên dưới:</p>
                        
                        <div class="otp-container">
                            <div class="otp-label">MÃ KÍCH HOẠT TÀI KHOẢN</div>
                            <div class="otp-code">%s</div>
                            <p style="margin: 0; color: #666; font-size: 14px;">Nhập mã này vào ứng dụng để kích hoạt tài khoản</p>
                        </div>
                        
                        <div class="warning">
                            <strong>⚠️ Lưu ý quan trọng:</strong>
                            <ul style="margin: 10px 0; padding-left: 20px;">
                                <li>Mã OTP này sẽ hết hạn sau <strong>10 phút</strong></li>
                                <li>Mỗi mã chỉ có thể sử dụng <strong>một lần</strong></li>
                                <li>Không chia sẻ mã này với bất kỳ ai</li>
                            </ul>
                        </div>
                        
                        <p>Sau khi nhập mã thành công, tài khoản của bạn sẽ được kích hoạt và bạn có thể đăng nhập.</p>
                        
                        <p>Nếu bạn không đăng ký tài khoản này, vui lòng bỏ qua email này.</p>
                    </div>
                    
                    <div class="footer">
                        <p><strong>Trân trọng,</strong><br>Đội ngũ Print Shop</p>
                        <p style="font-size: 12px; color: #999;">Email này được gửi tự động, vui lòng không trả lời.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(otpCode);
    }
}


