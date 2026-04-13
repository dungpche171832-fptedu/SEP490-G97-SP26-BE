package vn.edu.fpt.dto.request.car;

import lombok.*;
import vn.edu.fpt.ultis.enums.CarStatus;
import vn.edu.fpt.ultis.enums.CarType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarEditRequest {

    private String licensePlate;
    private Long branchId;
    private CarType carType;
    private Integer totalSeat;
    private CarStatus status;
    private Integer manufactureYear;
    private String description;
    private Boolean isActive;
}