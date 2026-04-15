package vn.edu.fpt.repository;

import vn.edu.fpt.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long> {
    Optional<Station> findByName(String name);
    Optional<Station> findByCode(String code);
}