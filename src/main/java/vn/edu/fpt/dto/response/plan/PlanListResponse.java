package vn.edu.fpt.dto.response.plan;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PlanListResponse {
    private List<PlanListItemResponse> plans;
    private int totalCount;
    private String message;
}