package vn.edu.fpt.service.ticket;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.dto.request.ticket.CreateTicketRequest;
import vn.edu.fpt.dto.response.ticket.TicketAddResponse;
import vn.edu.fpt.dto.response.ticket.TicketListResponse;
import vn.edu.fpt.dto.response.ticket.TicketResponse;
import vn.edu.fpt.entity.*;
import vn.edu.fpt.exception.AppException;
import vn.edu.fpt.repository.*;
import vn.edu.fpt.service.email.EmailService;
import vn.edu.fpt.ultis.enums.PlanSeatStatus;
import vn.edu.fpt.ultis.enums.TicketStatus;
import vn.edu.fpt.ultis.errorCode.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Override
    @Transactional
    public TicketListResponse getTickets(Long planId, Long branchId, Long accountId) {
        // Sử dụng Specification để tạo truy vấn động
        Specification<Ticket> spec = (root, query, cb) -> cb.conjunction();

        // Lọc theo planId nếu có
        if (planId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("plan").get("id"), planId)
            );
        }

        // Lọc theo branchId nếu có
        if (branchId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("branch").get("id"), branchId)
            );
        }

        // Lọc theo accountId nếu có
        if (accountId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("account").get("id"), accountId)
            );
        }

        // Truy vấn tất cả các vé theo specification
        List<Ticket> tickets = ticketRepository.findAll(spec);

        // Nếu không tìm thấy vé nào, trả về lỗi
        if (tickets.isEmpty()) {
            throw new AppException(TicketErrorCode.TICKET_NOT_FOUND);
        }

        // Chuyển đổi danh sách vé thành danh sách `TicketResponse`
        List<TicketResponse> ticketResponses = tickets.stream()
                .map(TicketResponse::new)
                .toList();

        // Trả về TicketListResponse bao gồm danh sách vé, thông báo và tổng số vé
        return TicketListResponse.builder()
                .tickets(ticketResponses)
                .message("Danh sách vé")
                .totalCount(ticketResponses.size())
                .build();
    }

    @Override
    public TicketResponse getTicketDetail(Long ticketId) {
        // Lấy thông tin Ticket từ cơ sở dữ liệu bằng ID
        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);

        // Kiểm tra nếu vé không tồn tại, ném AppException
        if (ticketOptional.isEmpty()) {
            throw new AppException(TicketErrorCode.TICKET_NOT_FOUND);
        }

        // Nếu vé tồn tại, chuyển đối tượng Ticket thành TicketResponse
        Ticket ticket = ticketOptional.get();
        return new TicketResponse(ticket);  // Chuyển đổi Ticket thành TicketResponse
    }

    @Override
    public TicketResponse updateTicketStatus(Long ticketId, String newStatus) {
        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);

        if (ticketOptional.isEmpty()) {
            throw new AppException(TicketErrorCode.TICKET_NOT_FOUND);
        }

        Ticket ticket = ticketOptional.get();

        // Chuyển String thành TicketStatus enum, đảm bảo luôn chuyển thành chữ hoa
        try {
            ticket.setStatus(TicketStatus.valueOf(newStatus.toUpperCase()));  // Chuyển String thành TicketStatus enum
        } catch (IllegalArgumentException e) {
            throw new AppException(TicketErrorCode.TICKET_STATUS_NOT_FOUND);
        }

        // Lưu lại vé với status đã thay đổi
        Ticket updatedTicket = ticketRepository.save(ticket);

        // Trả về TicketResponse đã được cập nhật
        return new TicketResponse(updatedTicket);
    }

    @Transactional
    public void changeTicketPlan(Long ticketId,
                                 Long newPlanId,
                                 List<Long> newSeatIds) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new AppException(TicketErrorCode.TICKET_NOT_FOUND));

        Plan oldPlan = ticket.getPlan();

        Plan newPlan = planRepository.findById(newPlanId)
                .orElseThrow(() -> new AppException(PlanErrorCode.PLAN_NOT_FOUND));

        // ===== VALIDATE =====

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

        // ===== GHẾ CŨ =====
        List<PlanSeat> oldSeats = planSeatRepository.findByTicketId(ticketId);

        // check số lượng ghế
        if (oldSeats.size() != newSeatIds.size()) {
            throw new AppException(PlanErrorCode.SEAT_COUNT_NOT_MATCH);
        }

        // ===== GHẾ MỚI =====
        List<PlanSeat> newSeats = planSeatRepository
                .findAllByPlanIdAndSeatIdIn(newPlanId, newSeatIds);

        if (newSeats.size() != newSeatIds.size()) {
            throw new AppException(PlanErrorCode.SEAT_COUNT_NOT_MATCH);
        }

        // check ghế đã bị book chưa
        for (PlanSeat seat : newSeats) {
            if (seat.getTicket() != null) {
                throw new AppException(TicketErrorCode.SEAT_ALREADY_BOOKED);
            }
        }

        // ===== CLEAR GHẾ CŨ =====
        oldSeats.forEach(s -> {
            s.setTicket(null);
            s.setStatus(PlanSeatStatus.AVAILABLE);
        });
        planSeatRepository.saveAll(oldSeats);

        // ===== GÁN GHẾ MỚI =====
        for (PlanSeat seat : newSeats) {
            seat.setTicket(ticket);
            seat.setStatus(PlanSeatStatus.BOOKED);
        }
        planSeatRepository.saveAll(newSeats);

        // ===== UPDATE TICKET =====
        ticket.setPlan(newPlan);
        ticket.setCar(newPlan.getCar());
        ticketRepository.save(ticket);

        // ===== EMAIL =====
        emailService.sendChangeTicketPlan(
                ticket.getAccount(),
                ticket,
                oldPlan,
                newPlan,
                oldSeats,
                newSeats
        );
    }
}

