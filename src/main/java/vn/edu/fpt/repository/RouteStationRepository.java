package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.entity.RouteStation;

import java.util.List;

public interface RouteStationRepository extends JpaRepository<RouteStation, Long> {
    void deleteByRouteId(Long routeId);

    @Query("""
    SELECT rs FROM RouteStation rs
    JOIN FETCH rs.station
    WHERE rs.route.id = :routeId
    ORDER BY rs.stationOrder
""")
    List<RouteStation> findByRouteIdOrderByStationOrder(@Param("routeId") Long routeId);
}