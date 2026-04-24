package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.entity.RouteStation;

import java.util.List;

public interface RouteStationRepository extends JpaRepository<RouteStation, Long> {
    List<RouteStation> findByRouteIdOrderByStationOrder(Long routeId);
}