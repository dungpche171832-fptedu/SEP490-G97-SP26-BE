package vn.edu.fpt.dto.request.route;

import lombok.Data;
import java.util.List;

@Data
public class CreateRouteRequest {
    private String code;
    private String name;
    private List<vn.edu.fpt.dto.request.station.StationOrderRequest> stations;
}