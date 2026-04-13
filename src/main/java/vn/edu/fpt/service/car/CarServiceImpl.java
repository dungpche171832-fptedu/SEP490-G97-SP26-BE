package vn.edu.fpt.service.car;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.edu.fpt.dto.request.car.CarAddRequest;
import vn.edu.fpt.dto.request.car.CarEditRequest;
import vn.edu.fpt.dto.response.car.CarAddResponse;
import vn.edu.fpt.dto.response.car.CarViewResponse;
import vn.edu.fpt.entity.Branch;
import vn.edu.fpt.entity.Car;
import vn.edu.fpt.exception.AppException;
import vn.edu.fpt.repository.BranchRepository;
import vn.edu.fpt.repository.CarRepository;
import vn.edu.fpt.ultis.errorCode.BranchErrorCode;
import vn.edu.fpt.ultis.errorCode.CarErrorCode;

import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private BranchRepository branchRepository;

    // Triển khai phương thức lấy tất cả các xe
    @Override
    public List<Car> getAllCars(Long branchId, String licensePlate) {

        Specification<Car> spec = (root, query, cb) -> cb.conjunction();

        if (branchId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("branch").get("id"), branchId)
            );
        }

        if (licensePlate != null && !licensePlate.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(
                            cb.lower(root.get("licensePlate")),
                            "%" + licensePlate.trim().toLowerCase() + "%"
                    )
            );
        }

        List<Car> cars = carRepository.findAll(spec);

        if (cars.isEmpty()) {
            throw new AppException(CarErrorCode.CAR_NOT_FOUND);
        }

        return cars;
    }

    @Transactional
    public CarAddResponse addCar(CarAddRequest request) {

        // normalize
        String licensePlate = request.getLicensePlate().trim().toUpperCase();

        // 1. validate duplicate
        if (carRepository.findByLicensePlate(licensePlate).isPresent()) {
            throw new AppException(CarErrorCode.LICENSE_PLATE_ALREADY_EXISTS);
        }

        // 2. validate branch
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new AppException(CarErrorCode.BRANCH_NOT_FOUND));

        // 3. validate business
        if (request.getTotalSeat() <= 0) {
            throw new AppException(CarErrorCode.INVALID_TOTAL_SEAT);
        }

        if (request.getManufactureYear() != null &&
                request.getManufactureYear() > java.time.Year.now().getValue()) {
            throw new AppException(CarErrorCode.INVALID_MANUFACTURE_YEAR);
        }

        // 4. mapping
        Car car = Car.builder()
                .licensePlate(licensePlate)
                .branch(branch)
                .carType(request.getCarType())
                .totalSeat(request.getTotalSeat())
                .status(request.getStatus())
                .manufactureYear(request.getManufactureYear())
                .description(request.getDescription())
                .isActive(true)
                .build();

        Car savedCar = carRepository.save(car);

        // 5. response
        return CarAddResponse.builder()
                .id(savedCar.getId())
                .licensePlate(savedCar.getLicensePlate())
                .branchId(savedCar.getBranch().getId())
                .carType(savedCar.getCarType())
                .totalSeat(savedCar.getTotalSeat())
                .status(savedCar.getStatus())
                .manufactureYear(savedCar.getManufactureYear())
                .description(savedCar.getDescription())
                .build();
    }

    @Override
    public CarViewResponse viewCar(Long carId) {

        // Tìm kiếm xe theo ID. Nếu không tìm thấy, ném ra lỗi CAR_NOT_FOUND.
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new AppException(CarErrorCode.CAR_NOT_FOUND));

        // Kiểm tra trạng thái xe, nếu không hoạt động, ném ra lỗi CAR_NOT_ACTIVE.
        if (!Boolean.TRUE.equals(car.getIsActive())) {
            throw new AppException(CarErrorCode.CAR_NOT_ACTIVE);
        }

        // Xây dựng đối tượng CarViewResponse để trả về chi tiết xe.
        return CarViewResponse.builder()
                .id(car.getId())
                .licensePlate(car.getLicensePlate())
                .branchName(car.getBranch().getName())
                .branchCode(car.getBranch().getCode())
                .branchEmail(car.getBranch().getEmail())
                .carType(String.valueOf(car.getCarType()))
                .totalSeat(car.getTotalSeat())
                .status(String.valueOf(car.getStatus()))
                .manufactureYear(car.getManufactureYear())
                .description(car.getDescription())
                .isActive(car.getIsActive())
                .build();
    }

    @Override
    public CarViewResponse editCar(Long id, CarEditRequest request) {

        Car car = carRepository.findById(id)
                .orElseThrow(() -> new AppException(CarErrorCode.CAR_NOT_FOUND));

        // Check branch nếu có update
        if (request.getBranchId() != null) {
            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new AppException(BranchErrorCode.BRANCH_NOT_FOUND));

            if (!Boolean.TRUE.equals(branch.getIsActive())) {
                throw new AppException(BranchErrorCode.BRANCH_NOT_ACTIVE);
            }

            car.setBranch(branch);
        }

        // Update từng field (chỉ update nếu != null)
        if (request.getLicensePlate() != null) {
            car.setLicensePlate(request.getLicensePlate());
        }

        if (request.getCarType() != null) {
            car.setCarType(request.getCarType());
        }

        if (request.getTotalSeat() != null) {
            car.setTotalSeat(request.getTotalSeat());
        }

        if (request.getStatus() != null) {
            car.setStatus(request.getStatus());
        }

        if (request.getManufactureYear() != null) {
            car.setManufactureYear(request.getManufactureYear());
        }

        if (request.getDescription() != null) {
            car.setDescription(request.getDescription());
        }

        if (request.getIsActive() != null) {
            car.setIsActive(request.getIsActive());
        }

        carRepository.save(car);

        return CarViewResponse.builder()
                .id(car.getId())
                .licensePlate(car.getLicensePlate())
                .branchName(car.getBranch().getName())
                .branchCode(car.getBranch().getCode())
                .branchEmail(car.getBranch().getEmail())
                .carType(car.getCarType().name())
                .totalSeat(car.getTotalSeat())
                .status(car.getStatus().name())
                .manufactureYear(car.getManufactureYear())
                .description(car.getDescription())
                .isActive(car.getIsActive())
                .build();
    }
}