package vn.edu.fpt.dto.request.route;

import lombok.Data;
import vn.edu.fpt.dto.request.station.StationOrderRequest;

import java.util.List;

@Data
public class CreateRouteRequest {
    private String code;
    private String name;
    private String nameRevert;
    private List<vn.edu.fpt.dto.request.station.StationOrderRequest> stations;
    private List<vn.edu.fpt.dto.request.station.StationOrderRequest> reverseStations;
}