package vn.edu.fpt.repository;

import vn.edu.fpt.entity.City;
import vn.edu.fpt.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
}