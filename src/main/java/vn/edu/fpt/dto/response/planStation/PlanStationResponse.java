package vn.edu.fpt.dto.response.planStation;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanStationResponse {

    private Long stationId;
    private String stationName;
    private Integer stationOrder;
}