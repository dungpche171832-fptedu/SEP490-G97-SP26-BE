package vn.edu.fpt.service.email;

import vn.edu.fpt.entity.*;

import java.util.List;

public interface EmailService {
    void sendOtp(String to, String otp);

    void sendChangeDriverEmail(Account oldDriver, Account newDriver, Plan plan);

    void sendChangeCarDriver(Account driver, Plan plan, Car oldCar, Car newCar);

    void sendChangeCarPassenger(Account passenger,
                                Plan plan,
                                Car oldCar,
                                Car newCar,
                                Ticket ticket,
                                List<PlanSeat> seats);
}