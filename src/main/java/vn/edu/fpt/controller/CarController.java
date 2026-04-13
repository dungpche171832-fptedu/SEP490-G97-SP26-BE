package vn.edu.fpt.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.dto.request.car.CarAddRequest;
import vn.edu.fpt.dto.request.car.CarEditRequest;
import vn.edu.fpt.dto.response.car.CarAddResponse;
import vn.edu.fpt.dto.response.car.CarListResponse;
import vn.edu.fpt.dto.response.car.CarViewResponse;
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
    public ResponseEntity<CarListResponse> getAllCars(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String licensePlate
    ) {
        List<Car> cars = carService.getAllCars(branchId, licensePlate);
        return ResponseEntity.ok(new CarListResponse(cars, "Danh sách xe", cars.size()));
    }

    @PostMapping
    public ResponseEntity<CarAddResponse> createCar(@Valid @RequestBody CarAddRequest request) {
        return ResponseEntity.ok(carService.addCar(request));
    }

    @GetMapping("/cars/{carId}")
    public ResponseEntity<CarViewResponse> getCarDetail(@PathVariable Long carId) {
        CarViewResponse carViewResponse = carService.viewCar(carId);
        return ResponseEntity.ok(carViewResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarViewResponse> updateCar(
            @PathVariable Long id,
            @RequestBody CarEditRequest request
    ) {
        return ResponseEntity.ok(carService.editCar(id, request));
    }
}