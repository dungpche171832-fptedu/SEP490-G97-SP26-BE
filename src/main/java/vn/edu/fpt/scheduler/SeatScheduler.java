package vn.edu.fpt.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.entity.PlanSeat;
import vn.edu.fpt.entity.Ticket;
import vn.edu.fpt.repository.PlanSeatRepository;
import vn.edu.fpt.repository.TicketRepository;
import vn.edu.fpt.ultis.enums.PlanSeatStatus;
import vn.edu.fpt.ultis.enums.TicketStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SeatScheduler {

    private final PlanSeatRepository planSeatRepository;
    private final TicketRepository ticketRepository;

    @Scheduled(fixedRate = 60000) // mỗi 1 phút
    @Transactional
    public void releaseExpiredSeats() {

        List<PlanSeat> expiredSeats =
                planSeatRepository.findExpiredSeats(LocalDateTime.now());

        // lấy danh sách ticket unique
        Set<Ticket> tickets = expiredSeats.stream()
                .map(PlanSeat::getTicket)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (Ticket ticket : tickets) {

            // chỉ xử lý ticket pending
            if (ticket.getStatus() == TicketStatus.PENDING) {

                // 1. cancel ticket
                ticket.setStatus(TicketStatus.CANCELLED);
                ticketRepository.save(ticket);

                // 2. release toàn bộ ghế của ticket
                List<PlanSeat> seats =
                        planSeatRepository.findByTicketId(ticket.getId());

                for (PlanSeat ps : seats) {
                    ps.setStatus(PlanSeatStatus.AVAILABLE);
                    ps.setTicket(null);
                    ps.setHoldExpiredAt(null);
                }

                planSeatRepository.saveAll(seats);
            }
        }
    }
}