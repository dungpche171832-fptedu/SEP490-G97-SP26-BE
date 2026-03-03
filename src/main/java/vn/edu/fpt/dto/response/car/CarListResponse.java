package vn.edu.fpt.dto.response.car;

import vn.edu.fpt.entity.Car;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CarListResponse {
    private List<Car> cars;  // Danh sách các xe
    private String message;  // Thông báo trả về
    private int totalCount;  // Tổng số xe
}