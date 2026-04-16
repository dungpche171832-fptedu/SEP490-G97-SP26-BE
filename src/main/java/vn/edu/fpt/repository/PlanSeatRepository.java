package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.entity.PlanSeat;

import java.util.List;

public interface PlanSeatRepository extends JpaRepository<PlanSeat, Long> {
    List<PlanSeat> findByPlanIdOrderBySeatIdAsc(Long planId);
}