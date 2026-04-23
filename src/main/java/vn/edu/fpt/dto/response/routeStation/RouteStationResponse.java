package vn.edu.fpt.dto.response.routeStation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RouteStationResponse {
    private Long stationId;
    private String stationName;
    private Integer order;
}