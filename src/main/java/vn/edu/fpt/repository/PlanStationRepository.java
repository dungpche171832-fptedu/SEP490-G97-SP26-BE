package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.entity.PlanStation;

import java.util.List;
import java.util.Optional;

public interface PlanStationRepository extends JpaRepository<PlanStation, Long> {

    List<PlanStation> findByPlanIdOrderByStationOrderAsc(Long planId);

    Optional<PlanStation> findFirstByPlanIdOrderByStationOrderAsc(Long planId);

    Optional<PlanStation> findFirstByPlanIdOrderByStationOrderDesc(Long planId);
}