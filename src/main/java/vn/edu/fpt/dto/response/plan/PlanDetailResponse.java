package vn.edu.fpt.dto.response.plan;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDetailResponse {
    private Long id;
    private Long carId;
    private String carLicensePlate;
    private Long accountId;
    private String driverName;
    private LocalDateTime startTime;
    private String status;
    private List<vn.edu.fpt.dto.response.planStation.PlanStationResponse> stations;
}