package vn.edu.fpt.service.ticket;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.dto.request.ticket.CreateTicketRequest;
import vn.edu.fpt.dto.response.ticket.*;
import vn.edu.fpt.entity.*;
import vn.edu.fpt.exception.AppException;
import vn.edu.fpt.repository.*;
import vn.edu.fpt.service.email.EmailService;
import vn.edu.fpt.ultis.enums.PlanSeatStatus;
import vn.edu.fpt.ultis.enums.TicketStatus;
import vn.edu.fpt.ultis.errorCode.*;

import java.time.LocalDateTime;
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
    private final EmailService emailService;

    @Override
    @Transactional
    public TicketAddResponse createTicket(CreateTicketRequest request) {

        Account account = getCurrentAccount();

        Plan plan = getPlan(request.getPlanId());
        Car car = getCar(request.getCarId());

        Station startStation = getStation(request.getStartStationId());
        Station endStation = getOptionalStation(request.getEndStationId());

        // LOCK GHẾ
        List<PlanSeat> seats = planSeatRepository
                .findAllForUpdate(plan.getId(), request.getSeatIds());

        validateSeatsExist(seats, request.getSeatIds());
        validateSeatsAvailable(seats);

        // CREATE TICKET
        Ticket ticket = Ticket.builder()
                .bookingCode(generateBookingCode())
                .plan(plan)
                .car(car)
                .branch(car.getBranch())
                .account(account)
                .startStation(startStation)
                .endStation(endStation)
                .distanceKm(request.getDistanceKm())
                .totalAmount(request.getTotalAmount())
                .status(TicketStatus.PENDING)
                .note(request.getNote())
                .build();

        ticketRepository.save(ticket);

        // HOLD GHẾ
        holdSeats(seats, ticket);

        return mapToResponse(ticket);
    }

    @Override
    @Transactional
    public TicketResponse updateTicketStatus(Long ticketId, String newStatus) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new AppException(TicketErrorCode.TICKET_NOT_FOUND));

        TicketStatus status = parseStatus(newStatus);
        ticket.setStatus(status);

        List<PlanSeat> seats = planSeatRepository.findByTicketId(ticketId);

        switch (status) {
            case BOOKED -> confirmSeats(seats);
            case CANCELLED -> releaseSeats(seats);
            default -> {}
        }

        planSeatRepository.saveAll(seats);

        Ticket saved = ticketRepository.save(ticket);

        handleAfterStatusChange(saved);

        return new TicketResponse(saved);
    }

    @Override
    @Transactional
    public void changeTicketPlan(Long ticketId, Long newPlanId, List<Long> newSeatIds) {

        Ticket ticket = getTicket(ticketId);

        if (ticket.getStatus() != TicketStatus.BOOKED) {
            throw new AppException(TicketErrorCode.SEAT_IS_NOT_BOOKED);
        }

        Plan oldPlan = ticket.getPlan();
        Plan newPlan = getPlan(newPlanId);

        validatePlanChange(oldPlan, newPlan);

        //  LOCK GHẾ MỚI
        List<PlanSeat> newSeats = planSeatRepository
                .findAllForUpdate(newPlanId, newSeatIds);

        validateSeatsExist(newSeats, newSeatIds);
        validateSeatsAvailable(newSeats);

        List<PlanSeat> oldSeats =
                planSeatRepository.findByTicketIdForUpdate(ticketId);
        if (oldSeats.size() != newSeatIds.size()) {
            throw new AppException(PlanErrorCode.SEAT_COUNT_NOT_MATCH);
        }

        // CLEAR GHẾ CŨ
        releaseSeats(oldSeats);

        // GÁN GHẾ MỚI
        for (PlanSeat seat : newSeats) {
            seat.setTicket(ticket);
            seat.setStatus(PlanSeatStatus.BOOKED);
        }

        planSeatRepository.saveAll(newSeats);

        // UPDATE TICKET
        ticket.setPlan(newPlan);
        ticket.setCar(newPlan.getCar());
        ticketRepository.save(ticket);

        // EMAIL
        emailService.sendChangeTicketPlan(
                ticket.getAccount(), ticket, oldPlan, newPlan, oldSeats, newSeats
        );
    }

    @Override
    public TicketListResponse getTickets(Long planId, Long branchId, Long accountId) {

        Specification<Ticket> spec = (root, query, cb) -> cb.conjunction();

        if (planId != null) {
            spec = spec.and((r, q, cb) -> cb.equal(r.get("plan").get("id"), planId));
        }

        if (branchId != null) {
            spec = spec.and((r, q, cb) -> cb.equal(r.get("branch").get("id"), branchId));
        }

        if (accountId != null) {
            spec = spec.and((r, q, cb) -> cb.equal(r.get("account").get("accountId"), accountId));
        }

        List<Ticket> tickets = ticketRepository.findAll(spec);

        if (tickets.isEmpty()) {
            throw new AppException(TicketErrorCode.TICKET_NOT_FOUND);
        }

        return TicketListResponse.builder()
                .tickets(tickets.stream().map(TicketResponse::new).toList())
                .message("Danh sách vé")
                .totalCount(tickets.size())
                .build();
    }

    @Override
    public TicketResponse getTicketDetail(Long ticketId) {
        return new TicketResponse(getTicket(ticketId));
    }

    // =====================================================
    // PRIVATE METHODS
    // =====================================================

    private Account getCurrentAccount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND));
    }

    private Plan getPlan(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new AppException(PlanErrorCode.PLAN_NOT_FOUND));
    }

    private Car getCar(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new AppException(CarErrorCode.CAR_NOT_FOUND));
    }

    private Station getStation(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new AppException(StationErrorCode.STATION_NOT_FOUND));
    }

    private Station getOptionalStation(Long id) {
        if (id == null) return null;
        return getStation(id);
    }

    private Ticket getTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new AppException(TicketErrorCode.TICKET_NOT_FOUND));
    }

    private String generateBookingCode() {
        return "TICKET-" + System.currentTimeMillis();
    }

    private TicketStatus parseStatus(String status) {
        try {
            return TicketStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new AppException(TicketErrorCode.TICKET_STATUS_NOT_FOUND);
        }
    }

    private void validateSeatsExist(List<PlanSeat> seats, List<Long> seatIds) {
        if (seats.size() != seatIds.size()) {
            throw new AppException(PlanErrorCode.SEAT_NOT_FOUND_IN_PLAN);
        }
    }

    private void validateSeatsAvailable(List<PlanSeat> seats) {

        boolean needUpdate = false;

        for (PlanSeat ps : seats) {

            // xử lý HOLD hết hạn
            if (ps.getStatus() == PlanSeatStatus.HOLD &&
                    ps.getHoldExpiredAt() != null &&
                    ps.getHoldExpiredAt().isBefore(LocalDateTime.now())) {

                ps.setStatus(PlanSeatStatus.AVAILABLE);
                ps.setTicket(null);
                ps.setHoldExpiredAt(null);

                needUpdate = true;
            }

            if (ps.getStatus() != PlanSeatStatus.AVAILABLE) {
                throw new AppException(PlanErrorCode.SEAT_ALREADY_BOOKED);
            }
        }

        if (needUpdate) {
            planSeatRepository.saveAll(seats);
        }
    }

    private void holdSeats(List<PlanSeat> seats, Ticket ticket) {
        for (PlanSeat ps : seats) {
            ps.setTicket(ticket);
            ps.setStatus(PlanSeatStatus.HOLD);
            ps.setHoldExpiredAt(LocalDateTime.now().plusMinutes(15));
        }
        planSeatRepository.saveAll(seats);
    }

    private void confirmSeats(List<PlanSeat> seats) {
        for (PlanSeat ps : seats) {
            ps.setStatus(PlanSeatStatus.BOOKED);
            ps.setHoldExpiredAt(null);
        }
    }

    private void releaseSeats(List<PlanSeat> seats) {
        for (PlanSeat ps : seats) {
            ps.setStatus(PlanSeatStatus.AVAILABLE);
            ps.setTicket(null);
            ps.setHoldExpiredAt(null);
        }
    }

    private void validatePlanChange(Plan oldPlan, Plan newPlan) {

        if (!oldPlan.getRoute().getId().equals(newPlan.getRoute().getId())) {
            throw new AppException(RouteErrorCode.ROUTE_NOT_MATCH);
        }

        if (!oldPlan.getCar().getCarType()
                .equals(newPlan.getCar().getCarType())) {
            throw new AppException(CarErrorCode.CAR_TYPE_NOT_MATCH);
        }

        if (!oldPlan.getStartTime().toLocalDate()
                .equals(newPlan.getStartTime().toLocalDate())) {
            throw new AppException(PlanErrorCode.START_DATE_NOT_MATCH);
        }
    }

    private void handleAfterStatusChange(Ticket ticket) {
        switch (ticket.getStatus()) {
            case BOOKED -> emailService.sendTicketBooked(ticket);
            case CANCELLED -> emailService.sendTicketCancelled(ticket);
            default -> {}
        }
    }

    private TicketAddResponse mapToResponse(Ticket ticket) {
        return TicketAddResponse.builder()
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