package vn.edu.fpt.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.dto.request.plan.AddPlanRequest;
import vn.edu.fpt.dto.response.plan.PlanDetailResponse;
import vn.edu.fpt.dto.response.plan.PlanListResponse;
import vn.edu.fpt.dto.response.plan.PlanResponse;
import vn.edu.fpt.service.plan.PlanService;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @PostMapping
    public ResponseEntity<PlanResponse> addPlan(@Valid @RequestBody AddPlanRequest request) {
        return ResponseEntity.ok(planService.addPlan(request));
    }

    @GetMapping
    public ResponseEntity<PlanListResponse> getPlans(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Long departureStationId,
            @RequestParam(required = false) Long destinationStationId
    ) {
        return ResponseEntity.ok(planService.getPlans(code, departureStationId, destinationStationId));
    }

    @GetMapping("/{planId}")
    public ResponseEntity<PlanDetailResponse> getPlanDetail(@PathVariable Long planId) {
        return ResponseEntity.ok(planService.getPlanDetail(planId));
    }
}