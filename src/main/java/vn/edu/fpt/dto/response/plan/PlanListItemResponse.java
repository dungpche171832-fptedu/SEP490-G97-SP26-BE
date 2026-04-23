package vn.edu.fpt.dto.response.plan;

import lombok.*;
import vn.edu.fpt.dto.response.planStation.PlanStationResponse;
import vn.edu.fpt.dto.response.routeStation.RouteStationResponse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanListItemResponse {
    private Long id;
    private String code;
    private Long carId;
    private String carLicensePlate;
    private Long accountId;
    private String driverName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    private List<RouteStationResponse> stations;
}