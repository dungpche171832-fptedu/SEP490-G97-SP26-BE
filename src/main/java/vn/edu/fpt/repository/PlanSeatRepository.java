package vn.edu.fpt.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.entity.PlanSeat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PlanSeatRepository extends JpaRepository<PlanSeat, Long> {

    Optional<PlanSeat> findByPlanIdAndSeatId(Long planId, Long seatId);

    List<PlanSeat> findAllByPlanIdAndSeatIdIn(Long planId, List<Long> seatIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT ps FROM PlanSeat ps
        WHERE ps.plan.id = :planId
        AND ps.seat.id IN :seatIds
    """)
    List<PlanSeat> findAllForUpdate(
            @Param("planId") Long planId,
            @Param("seatIds") List<Long> seatIds
    );

    // dùng cho email
    List<PlanSeat> findByTicketId(Long ticketId);

    @Query("""
    SELECT ps FROM PlanSeat ps
    WHERE ps.status = 'HOLD'
    AND ps.holdExpiredAt < :now
""")
    List<PlanSeat> findExpiredSeats(@Param("now") LocalDateTime now);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT ps FROM PlanSeat ps
    WHERE ps.ticket.id = :ticketId
""")
    List<PlanSeat> findByTicketIdForUpdate(@Param("ticketId") Long ticketId);
}