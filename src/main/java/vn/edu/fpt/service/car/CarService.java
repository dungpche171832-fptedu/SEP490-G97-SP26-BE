package vn.edu.fpt.service.car;

import vn.edu.fpt.entity.Car;
import java.util.List;

public interface CarService {
    // Phương thức lấy tất cả các xe
    List<Car> getAllCars();
}