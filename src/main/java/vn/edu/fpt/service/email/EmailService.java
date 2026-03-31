package vn.edu.fpt.service.email;

public interface EmailService {
    void sendOtp(String to, String otp);
}