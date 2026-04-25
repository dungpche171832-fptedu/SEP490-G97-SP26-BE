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

    void sendChangeTicketPlan(Account passenger,
                              Ticket ticket,
                              Plan oldPlan,
                              Plan newPlan,
                              List<PlanSeat> oldSeats,
                              List<PlanSeat> newSeats);

    void sendTicketBooked(Ticket ticket);

    void sendTicketCancelled(Ticket ticket);
}