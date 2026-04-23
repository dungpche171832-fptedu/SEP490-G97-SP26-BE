package vn.edu.fpt.dto.response.plan;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanPairResponse {
    private PlanResponse outbound; // chiều đi
    private PlanResponse inbound;  // chiều về
}