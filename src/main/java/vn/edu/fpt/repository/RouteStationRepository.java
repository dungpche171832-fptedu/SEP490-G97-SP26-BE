package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.entity.RouteStation;

public interface RouteStationRepository extends JpaRepository<RouteStation, Long> {
}