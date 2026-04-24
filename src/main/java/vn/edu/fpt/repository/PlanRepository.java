package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
}