package vn.edu.fpt.service.ticket;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.dto.request.ticket.CreateTicketRequest;
import vn.edu.fpt.dto.response.ticket.TicketResponse;
import vn.edu.fpt.entity.*;
import vn.edu.fpt.exception.AppException;
import vn.edu.fpt.repository.*;
import vn.edu.fpt.ultis.enums.PlanSeatStatus;
import vn.edu.fpt.ultis.enums.TicketStatus;
import vn.edu.fpt.ultis.errorCode.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final PlanRepository planRepository;
    private final CarRepository carRepository;
    private final StationRepository stationRepository;
    private final AccountRepository accountRepository;
    private final PlanSeatRepository planSeatRepository;

    @Override
    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request) {

        // 1. Lấy account từ token
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        // 2. Lấy plan
        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new AppException(PlanErrorCode.PLAN_NOT_FOUND));

        // 3. Lấy car
        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new AppException(CarErrorCode.CAR_NOT_FOUND));

        // 4. Lấy branch từ car
        Branch branch = car.getBranch();

        // 5. Lấy station
        Station startStation = stationRepository.findById(request.getStartStationId())
                .orElseThrow(() -> new AppException(StationErrorCode.STATION_NOT_FOUND));

        Station endStation = null;
        if (request.getEndStationId() != null) {
            endStation = stationRepository.findById(request.getEndStationId())
                    .orElseThrow(() -> new AppException(StationErrorCode.STATION_NOT_FOUND));
        }

        // 6. Generate booking code
        String bookingCode = "TICKET-" + System.currentTimeMillis();

        // 7. Tạo ticket
        Ticket ticket = Ticket.builder()
                .bookingCode(bookingCode)
                .plan(plan)
                .car(car)
                .branch(branch)
                .account(account)
                .startStation(startStation)
                .endStation(endStation)
                .distanceKm(request.getDistanceKm())
                .totalAmount(request.getTotalAmount())
                .status(TicketStatus.PENDING)
                .note(request.getNote())
                .build();

        ticketRepository.save(ticket);
        // 8. Update plan seat
        List<PlanSeat> planSeats = planSeatRepository
                .findAllByPlanIdAndSeatIdIn(plan.getId(), request.getSeatIds());

        if (planSeats.size() != request.getSeatIds().size()) {
            throw new AppException(PlanErrorCode.SEAT_NOT_FOUND_IN_PLAN);
        }

        for (PlanSeat ps : planSeats) {
            if (ps.getTicket() != null) {
                throw new AppException(PlanErrorCode.SEAT_ALREADY_BOOKED);
            }
        }

        for (PlanSeat ps : planSeats) {
            ps.setTicket(ticket);
            ps.setStatus(PlanSeatStatus.BOOKED);
        }

        planSeatRepository.saveAll(planSeats);

        return mapToResponse(ticket);
    }

    // ===== PRIVATE MAPPER =====
    private TicketResponse mapToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .bookingCode(ticket.getBookingCode())
                .planId(ticket.getPlan().getId())
                .carId(ticket.getCar().getId())
                .accountId(ticket.getAccount().getAccountId())
                .branchId(ticket.getBranch().getId())
                .startStationId(ticket.getStartStation().getId())
                .endStationId(ticket.getEndStation() != null ? ticket.getEndStation().getId() : null)
                .distanceKm(ticket.getDistanceKm())
                .totalAmount(ticket.getTotalAmount())
                .status(ticket.getStatus().name())
                .build();
    }
}
