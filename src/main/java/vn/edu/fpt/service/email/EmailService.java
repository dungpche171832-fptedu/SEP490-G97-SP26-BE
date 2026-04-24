package vn.edu.fpt.service.email;

import vn.edu.fpt.entity.Account;
import vn.edu.fpt.entity.Plan;

public interface EmailService {
    void sendOtp(String to, String otp);

    void sendChangeDriverEmail(Account oldDriver, Account newDriver, Plan plan);
}