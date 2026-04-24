package vn.edu.fpt.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.edu.fpt.entity.Account;
import vn.edu.fpt.entity.Plan;
import vn.edu.fpt.service.email.EmailService;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final MailChangeDriverSender mailChangeDriverSender;

    @Override
    public void sendOtp(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset Password OTP");
        message.setText("Your OTP is: " + otp);
        mailSender.send(message);
    }

    @Override
    @Async
    public void sendChangeDriverEmail(Account oldDriver, Account newDriver, Plan plan) {

        String subject = "Thông báo thay đổi tài xế";

        String oldContent = """
                Bạn đã bị hủy chuyến:
                Mã: %s
                Thời gian: %s
                """.formatted(plan.getCode(), plan.getStartTime());

        String newContent = """
                Bạn được phân công chuyến:
                Mã: %s
                Thời gian: %s
                """.formatted(plan.getCode(), plan.getStartTime());

        if (oldDriver.getEmail() != null) {
            mailChangeDriverSender.send(oldDriver.getEmail(), subject, oldContent);
        }

        if (newDriver.getEmail() != null) {
            mailChangeDriverSender.send(newDriver.getEmail(), subject, newContent);
        }
    }
}