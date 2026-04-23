package vn.edu.fpt.dto.request.station;

import lombok.Data;

@Data
public class StationOrderRequest {
    private Long stationId;
    private Integer order;
}