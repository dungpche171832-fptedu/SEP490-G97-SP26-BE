package vn.edu.fpt.dto.response.car;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarViewResponse {

    private long id;
    private String licensePlate;
    private String branchName;  // Tên chi nhánh
    private String branchCode;  // Mã chi nhánh
    private String branchEmail; // Email chi nhánh
    private String carType;
    private Integer totalSeat;
    private String status;
    private Integer manufactureYear;
    private String description;
    private Boolean isActive;
}