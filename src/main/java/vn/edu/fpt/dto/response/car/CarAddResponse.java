package vn.edu.fpt.dto.response.car;

import lombok.Builder;
import lombok.Data;
import vn.edu.fpt.ultis.enums.CarStatus;
import vn.edu.fpt.ultis.enums.CarType;

@Data
@Builder
public class CarAddResponse {

    private Long id;
    private String licensePlate;
    private Long branchId;
    private CarType carType;
    private Integer totalSeat;
    private CarStatus status;
    private Integer manufactureYear;
    private String description;
}