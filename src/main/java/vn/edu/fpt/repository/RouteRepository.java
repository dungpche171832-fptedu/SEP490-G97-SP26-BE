package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.entity.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {
    boolean existsByCode(String code);
}