package vn.edu.fpt.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.edu.fpt.repository.PasswordResetOtpRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class OtpCleanupJob {

    private final PasswordResetOtpRepository otpRepository;

    // chạy mỗi ngày lúc 2h sáng
    @Scheduled(cron = "0 0 2 * * ?")
    public void deleteExpiredOtp() {
        int deleted = otpRepository.deleteExpiredOtp();
        log.info("Deleted {} expired OTP records", deleted);
    }
}