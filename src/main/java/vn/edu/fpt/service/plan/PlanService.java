package vn.edu.fpt.service.plan;

import vn.edu.fpt.dto.request.plan.AddPlanRequest;
import vn.edu.fpt.dto.response.plan.PlanResponse;

public interface PlanService {
    PlanResponse addPlan(AddPlanRequest request);
}