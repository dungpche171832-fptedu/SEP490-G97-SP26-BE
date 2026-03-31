package vn.edu.fpt.repository;

import vn.edu.fpt.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findAll();  // Phương thức để lấy tất cả các xe

    Optional<Car> findByLicensePlate(String licensePlate);
}