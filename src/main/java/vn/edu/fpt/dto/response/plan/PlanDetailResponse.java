package vn.edu.fpt.dto.response.plan;

import lombok.*;
import vn.edu.fpt.dto.response.planSeat.PlanSeatResponse;
import vn.edu.fpt.dto.response.planStation.PlanStationResponse;
import vn.edu.fpt.dto.response.routeStation.RouteStationResponse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDetailResponse {
    private Long id;
    private String code;

    private Long carId;
    private String carLicensePlate;

    private Long accountId;
    private String driverName;
    private String driverPhone;

    private Long branchId;
    private String branchName;

    private Long routeId;
    private String routeName;

    private LocalDateTime startTime;

    private String status;

    private List<RouteStationResponse> stations;
    private List<PlanSeatResponse> seats;
}