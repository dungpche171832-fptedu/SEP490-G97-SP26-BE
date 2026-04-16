package vn.edu.fpt.dto.request.plan;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddPlanRequest {

    @NotNull
    private Long carId;

    @NotNull
    private Long accountId; // driver

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotBlank
    private String status;

    @NotEmpty
    private List<vn.edu.fpt.dto.request.planStation.PlanStationRequest> stations;
}