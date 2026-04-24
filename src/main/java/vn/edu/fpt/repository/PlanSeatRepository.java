package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.entity.PlanSeat;

import java.util.List;
import java.util.Optional;

public interface PlanSeatRepository extends JpaRepository<PlanSeat, Long> {

    Optional<PlanSeat> findByPlanIdAndSeatId(Long planId, Long seatId);

    List<PlanSeat> findAllByPlanIdAndSeatIdIn(Long planId, List<Long> seatIds);

    List<PlanSeat> findByTicketId(Long ticketId);
}