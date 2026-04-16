package vn.edu.fpt.dto.response.plan;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanListResponse {
    private List<PlanListItemResponse> plans;
    private String message;
    private int totalCount;
}