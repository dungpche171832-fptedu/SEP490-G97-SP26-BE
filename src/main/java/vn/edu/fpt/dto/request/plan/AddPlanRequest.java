package vn.edu.fpt.dto.request.plan;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AddPlanRequest {
    private String code;
    private Long routeId;
    private Long carId;
    private Long accountId;
    private Long branchId;
    private LocalDateTime startTime;
    private String status;
}