package vn.edu.fpt.dto.response.plan;

import lombok.Builder;
import lombok.Data;
import vn.edu.fpt.dto.response.routeStation.RouteStationResponse;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PlanResponse {

    private Long id;
    private String code;

    private Long carId;
    private String carLicensePlate;

    private Long accountId;
    private String driverName;

    private Long branchId;
    private String branchName;

    private Long routeId;
    private String routeName;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String status;

    private List<RouteStationResponse> stations;
}