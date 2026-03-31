package vn.edu.fpt.service.car;

import vn.edu.fpt.dto.request.car.CarAddRequest;
import vn.edu.fpt.dto.response.car.CarAddResponse;
import vn.edu.fpt.entity.Car;
import java.util.List;

public interface CarService {
    // Phương thức lấy tất cả các xe
    List<Car> getAllCars();

    // Thêm mới xe
    CarAddResponse addCar(CarAddRequest request);
}