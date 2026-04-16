package vn.edu.fpt.service.plan;

import vn.edu.fpt.dto.request.plan.AddPlanRequest;
import vn.edu.fpt.dto.response.plan.PlanDetailResponse;
import vn.edu.fpt.dto.response.plan.PlanListResponse;
import vn.edu.fpt.dto.response.plan.PlanResponse;

public interface PlanService {
    PlanResponse addPlan(AddPlanRequest request);

    PlanListResponse getPlans(String code, Long departureStationId, Long destinationStationId);

    PlanDetailResponse getPlanDetail(Long planId);
}