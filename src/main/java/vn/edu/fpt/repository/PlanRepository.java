package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.entity.Car;
import vn.edu.fpt.entity.Plan;
import vn.edu.fpt.entity.PlanStation;

import java.time.LocalDateTime;
import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long>, JpaSpecificationExecutor<Plan> {

    boolean existsByCarAndStartTime(Car car, LocalDateTime startTime);

    boolean existsByCode(String code);

    @Query("""
    SELECT ps
    FROM PlanStation ps
    JOIN FETCH ps.station s
    JOIN FETCH s.city
    WHERE ps.plan.id = :planId
    ORDER BY ps.stationOrder ASC
""")
    List<PlanStation> findByPlanId(@Param("planId") Long planId);
}