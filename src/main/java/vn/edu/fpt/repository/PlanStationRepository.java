package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.entity.PlanStation;
import vn.edu.fpt.entity.Station;

import java.util.List;
import java.util.Optional;

public interface PlanStationRepository extends JpaRepository<PlanStation, Long> {

    List<PlanStation> findByPlanIdOrderByStationOrderAsc(Long planId);

    @Query("""
        SELECT ps.station
        FROM PlanStation ps
        WHERE ps.plan.id = :planId
        ORDER BY ps.stationOrder ASC
    """)
    List<Station> findStationsByPlanId(@Param("planId") Long planId);
}