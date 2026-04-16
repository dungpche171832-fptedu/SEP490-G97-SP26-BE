package vn.edu.fpt.dto.request.planStation;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanStationRequest {

    @NotNull
    private Long stationId;

    @NotNull
    private Integer stationOrder;
}