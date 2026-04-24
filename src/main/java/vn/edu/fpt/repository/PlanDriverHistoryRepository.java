package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.entity.PlanDriverHistory;

public interface PlanDriverHistoryRepository extends JpaRepository<PlanDriverHistory, Long> {
}