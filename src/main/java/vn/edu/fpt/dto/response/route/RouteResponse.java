package vn.edu.fpt.dto.response.route;

import lombok.Builder;
import lombok.Data;
import vn.edu.fpt.dto.response.routeStation.RouteStationResponse;

import java.util.List;

@Data
@Builder
public class RouteResponse {
    private Long id;
    private String code;
    private String name;
    private Long routeRevertId;
    private List<RouteStationResponse> stations;
}