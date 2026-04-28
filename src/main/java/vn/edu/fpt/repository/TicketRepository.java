package vn.edu.fpt.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.dto.projection.TicketStatProjection;
import vn.edu.fpt.dto.projection.TopRouteProjection;
import vn.edu.fpt.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    List<Ticket> findByPlanId(Long planId);

    @Query("""
    SELECT 
        COUNT(t) AS totalTickets,
        COALESCE(SUM(t.totalAmount), 0) AS totalRevenue
    FROM Ticket t
    WHERE t.status = 'BOOKED'
    AND t.createdAt BETWEEN :start AND :end
""")
    TicketStatProjection getTicketStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
    SELECT 
        p.route.id AS routeId,
        p.route.name AS routeName,
        COUNT(t) AS totalTickets
    FROM Ticket t
    JOIN t.plan p
    WHERE t.status = 'BOOKED'
    AND t.createdAt BETWEEN :start AND :end
    GROUP BY p.route.id, p.route.name
    ORDER BY COUNT(t) DESC
""")
    List<TopRouteProjection> getTopRoutes(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}