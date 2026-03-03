package vn.edu.fpt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.dto.response.car.CarListResponse;
import vn.edu.fpt.entity.Car;
import vn.edu.fpt.service.car.CarService;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    @Autowired
    private CarService carService;

    // Endpoint lấy danh sách tất cả các xe
    @GetMapping
    public ResponseEntity<CarListResponse> getAllCars() {
        List<Car> cars = carService.getAllCars();  // Lấy tất cả các xe từ service

        if (cars.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new CarListResponse(cars, "Không có xe nào", 0));
        }

        return ResponseEntity.ok(new CarListResponse(cars, "Danh sách xe", cars.size()));
    }
}