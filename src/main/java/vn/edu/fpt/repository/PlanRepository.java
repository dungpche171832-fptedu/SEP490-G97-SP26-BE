package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fpt.entity.Car;
import vn.edu.fpt.entity.Plan;

import java.time.LocalDateTime;

public interface PlanRepository extends JpaRepository<Plan, Long>, JpaSpecificationExecutor<Plan> {

    boolean existsByCarAndStartTime(Car car, LocalDateTime startTime);

    boolean existsByCode(String code);

}