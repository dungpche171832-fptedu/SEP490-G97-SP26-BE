package vn.edu.fpt.dto.request.route;

import lombok.Data;
import vn.edu.fpt.dto.request.station.StationOrderRequest;

import java.util.List;

@Data
public class UpdateRouteRequest {
    private String code;
    private String name;
    private List<StationOrderRequest> stations;
}