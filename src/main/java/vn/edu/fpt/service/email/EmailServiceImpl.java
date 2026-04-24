package vn.edu.fpt.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.edu.fpt.entity.*;
import vn.edu.fpt.service.email.EmailService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final MailChangeSender mailChangeSender;

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
            mailChangeSender.send(oldDriver.getEmail(), subject, oldContent);
        }

        if (newDriver.getEmail() != null) {
            mailChangeSender.send(newDriver.getEmail(), subject, newContent);
        }
    }

    @Override
    public void sendChangeCarDriver(Account driver, Plan plan, Car oldCar, Car newCar) {

        String content = """
                THÔNG BÁO ĐỔI XE

                Mã lịch trình: %s
                Thời gian xuất phát: %s

                Xe cũ: %s
                Xe mới: %s
                """.formatted(
                plan.getCode(),
                plan.getStartTime(),
                oldCar.getLicensePlate(),
                newCar.getLicensePlate()
        );

        mailChangeSender.send(driver.getEmail(), "Đổi xe", content);
    }

    @Override
    public void sendChangeCarPassenger(Account passenger,
                                       Plan plan,
                                       Car oldCar,
                                       Car newCar,
                                       Ticket ticket,
                                       List<PlanSeat> seats) {

        String seatNames = seats.stream()
                .map(s -> s.getSeat().getSeatNumber())
                .collect(Collectors.joining(", "));

        String content = """
                THÔNG BÁO THAY ĐỔI XE
                
                Do gặp 1 số sự cố ngoài ý muốn, công ty xe limo Việt Trung xin phép được đổi xe trong: 
                
                Mã vé: %s
                Mã lịch trình: %s
                Thời gian: %s

                Ghế: %s
                Từ xe: %s
                Sang xe mới: %s
                
                Xin thứ lỗi về sự bất tiện này, chúc quý khách có một chuyến đi vui vẻ
                Trân trọng
                """.formatted(
                ticket.getBookingCode(),
                plan.getCode(),
                plan.getStartTime(),
                seatNames,
                oldCar.getLicensePlate(),
                newCar.getLicensePlate()
        );

        mailChangeSender.send(passenger.getEmail(), "Thông báo đổi xe", content);
    }
}