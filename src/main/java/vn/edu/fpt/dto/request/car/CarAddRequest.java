package vn.edu.fpt.dto.request.car;

import lombok.Data;
import vn.edu.fpt.ultis.enums.CarStatus;
import vn.edu.fpt.ultis.enums.CarType;

import jakarta.validation.constraints.*;

@Data
public class CarAddRequest {

    @NotBlank
    @Size(max = 50)
    private String licensePlate;

    @NotNull
    private Long branchId;

    @NotNull
    private CarType carType;

    @NotNull
    @Min(1)
    private Integer totalSeat;

    @NotNull
    private CarStatus status;

    private Integer manufactureYear;

    @Size(max = 255)
    private String description;
}