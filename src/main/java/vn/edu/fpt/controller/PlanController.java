package vn.edu.fpt.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.dto.request.plan.AddPlanRequest;
import vn.edu.fpt.dto.request.plan.UpdatePlanStatusRequest;
import vn.edu.fpt.dto.response.plan.PlanDetailResponse;
import vn.edu.fpt.dto.response.plan.PlanListResponse;
import vn.edu.fpt.dto.response.plan.PlanResponse;
import vn.edu.fpt.dto.response.station.StationResponse;
import vn.edu.fpt.service.plan.PlanService;

import java.time.LocalDateTime;
import java.util.List;

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
            @RequestParam(required = false) Long destinationStationId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime

    ) {
        return ResponseEntity.ok(planService.getPlans(code, departureStationId, destinationStationId, status, startTime));
    }

    @GetMapping("/{planId}")
    public ResponseEntity<PlanDetailResponse> getPlanDetail(@PathVariable Long planId) {
        return ResponseEntity.ok(planService.getPlanDetail(planId));
    }

    @PatchMapping("/{planId}/status")
    public ResponseEntity<PlanResponse> updatePlanStatus(
            @PathVariable Long planId,
            @Valid @RequestBody UpdatePlanStatusRequest request
    ) {
        return ResponseEntity.ok(planService.updatePlanStatus(planId, request));
    }

    @GetMapping("/{planId}/stations")
    public ResponseEntity<List<StationResponse>> getStationsByPlan(
            @PathVariable Long planId
    ) {
        return ResponseEntity.ok(planService.getStationsByPlan(planId));
    }
}