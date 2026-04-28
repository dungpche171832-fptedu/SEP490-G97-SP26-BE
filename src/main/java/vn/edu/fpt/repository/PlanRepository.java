package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.dto.projection.PlanStatProjection;
import vn.edu.fpt.entity.Account;
import vn.edu.fpt.entity.Car;
import vn.edu.fpt.entity.Plan;


import java.time.LocalDateTime;
import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long>, JpaSpecificationExecutor<Plan> {

    boolean existsByCarAndStartTimeBetween(Car car, LocalDateTime startOfDay, LocalDateTime endOfDay);

    boolean existsByCode(String code);

    boolean existsByAccountAndStartTimeBetween(Account account, LocalDateTime startOfDay, LocalDateTime endOfDay);

    boolean existsByAccountAccountIdAndStartTime(Long accountId, LocalDateTime startTime);

    boolean existsByRouteId(Long routeId);

    @Query("""
    SELECT COUNT(p) > 0
    FROM Plan p
    WHERE p.car.id = :carId
    AND p.id <> :planId
    AND p.startTime BETWEEN :startOfDay AND :endOfDay
""")
    boolean existsByCarAndDate(
            Long carId,
            Long planId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );

    @Query("""
    SELECT COUNT(p)
    FROM Plan p
    WHERE p.startTime BETWEEN :start AND :end
""")
    Long countPlans(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
    SELECT COUNT(p) AS totalPlans
    FROM Plan p
    WHERE p.startTime BETWEEN :start AND :end
""")
    PlanStatProjection getPlanStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}