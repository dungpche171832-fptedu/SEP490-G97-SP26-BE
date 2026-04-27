package vn.edu.fpt.service.plan;

import vn.edu.fpt.dto.request.plan.AddPlanRequest;
import vn.edu.fpt.dto.request.plan.UpdatePlanStatusRequest;
import vn.edu.fpt.dto.response.plan.PlanDetailResponse;
import vn.edu.fpt.dto.response.plan.PlanListResponse;
import vn.edu.fpt.dto.response.plan.PlanPairResponse;
import vn.edu.fpt.dto.response.plan.PlanResponse;
import vn.edu.fpt.dto.response.station.StationResponse;

import java.util.Date;
import java.util.List;

public interface PlanService {
    PlanPairResponse addPlan(AddPlanRequest request);

    PlanListResponse getPlans(
            String code,
            Long departureStationId,
            Long destinationStationId,
            String status,
            Date startTime,
            Long accountId,
            Long branchId
    );

    PlanDetailResponse getPlanDetail(Long planId);

    PlanResponse updatePlanStatus(Long planId, UpdatePlanStatusRequest request);

    List<StationResponse> getStationsByPlan(Long planId);

    void changeDriver(Long planId, Long newDriverId);

    void changeCar(Long planId, Long newCarId);
}