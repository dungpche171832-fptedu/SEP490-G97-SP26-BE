package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.fpt.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long>, JpaSpecificationExecutor<Station> {
    Optional<Station> findByName(String name);
    Optional<Station> findByCode(String code);
    boolean existsByName(String name);

    boolean existsByCode(String code);
}