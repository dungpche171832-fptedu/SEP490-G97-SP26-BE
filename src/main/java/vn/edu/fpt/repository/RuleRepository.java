package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.entity.Rule;
import vn.edu.fpt.ultis.enums.CarType;

import java.util.List;

public interface RuleRepository extends JpaRepository<Rule, Long> {

    List<Rule> findByCarTypeOrderByMinKmAsc(CarType carType);

    void deleteByCarType(CarType carType);
}