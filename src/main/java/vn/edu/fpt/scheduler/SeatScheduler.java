package vn.edu.fpt.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.edu.fpt.entity.PlanSeat;
import vn.edu.fpt.repository.PlanSeatRepository;
import vn.edu.fpt.ultis.enums.PlanSeatStatus;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatScheduler {

    private final PlanSeatRepository planSeatRepository;

    @Scheduled(fixedRate = 60000) // mỗi 1 phút
    public void releaseExpiredSeats() {

        List<PlanSeat> seats =
                planSeatRepository.findExpiredSeats(LocalDateTime.now());

        for (PlanSeat ps : seats) {
            ps.setStatus(PlanSeatStatus.AVAILABLE);
            ps.setTicket(null);
            ps.setHoldExpiredAt(null);
        }

        planSeatRepository.saveAll(seats);
    }
}