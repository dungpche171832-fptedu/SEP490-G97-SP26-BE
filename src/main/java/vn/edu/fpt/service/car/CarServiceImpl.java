package vn.edu.fpt.service.car;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.fpt.entity.Car;
import vn.edu.fpt.repository.CarRepository;

import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository;

    // Triển khai phương thức lấy tất cả các xe
    @Override
    public List<Car> getAllCars() {
        return carRepository.findAll();  // Lấy tất cả các xe từ repository
    }
}