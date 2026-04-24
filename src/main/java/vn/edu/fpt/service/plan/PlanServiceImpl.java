package vn.edu.fpt.service.plan;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.dto.request.plan.AddPlanRequest;
import vn.edu.fpt.dto.request.plan.UpdatePlanStatusRequest;
import vn.edu.fpt.dto.response.plan.*;
import vn.edu.fpt.dto.response.planSeat.PlanSeatResponse;
import vn.edu.fpt.dto.response.routeStation.RouteStationResponse;
import vn.edu.fpt.dto.response.station.StationResponse;
import vn.edu.fpt.entity.*;
import vn.edu.fpt.exception.AppException;
import vn.edu.fpt.repository.*;
import vn.edu.fpt.ultis.enums.PlanSeatStatus;
import vn.edu.fpt.ultis.errorCode.BranchErrorCode;
import vn.edu.fpt.ultis.errorCode.PlanErrorCode;
import vn.edu.fpt.ultis.errorCode.RouteErrorCode;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final CarRepository carRepository;
    private final AccountRepository accountRepository;
    private final StationRepository stationRepository;
    private final SeatRepository seatRepository;
    private final RouteRepository routeRepository;
    private final BranchRepository branchRepository;
    private final RouteStationRepository routeStationRepository;


    @Override
    @Transactional
    public PlanPairResponse addPlan(AddPlanRequest request) {

        if (planRepository.existsByCode(request.getCode().trim())) {
            throw new AppException(PlanErrorCode.PLAN_CODE_ALREADY_EXISTS);
        }

        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new AppException(PlanErrorCode.PLAN_CAR_NOT_FOUND));

        Account driver = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AppException(PlanErrorCode.PLAN_DRIVER_NOT_FOUND));

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new AppException(RouteErrorCode.ROUTE_NOT_FOUND));

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new AppException(BranchErrorCode.BRANCH_NOT_FOUND));

        LocalDateTime startOfDay = request.getStartTime().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = request.getStartTime().toLocalDate().atTime(LocalTime.MAX);

        if (planRepository.existsByCarAndStartTimeBetween(car, startOfDay, endOfDay)) {
            throw new AppException(PlanErrorCode.PLAN_ALREADY_EXISTS);
        }
        if (planRepository.existsByAccountAndStartTimeBetween(driver, startOfDay, endOfDay)) {
            throw new AppException(PlanErrorCode.DRIVER_ALREADY_ASSIGNED);
        }
        Plan planA = buildPlan(
                request,
                car,
                driver,
                branch,
                route,
                request.getCode() + "_OUT"
        );

        // ===== TẠO PLAN B (CHIỀU VỀ) =====
        Plan planB = buildPlan(
                request,
                car,
                driver,
                branch,
                route.getRouteRevert(),
                request.getCode() + "_BACK"
        );

        // ===== SAVE =====
        planRepository.saveAll(List.of(planA, planB));

        return PlanPairResponse.builder()
                .outbound(mapToResponse(planA))
                .inbound(mapToResponse(planB))
                .build();
    }

    // ================= BUILD PLAN =================
    private Plan buildPlan(AddPlanRequest request,
                           Car car,
                           Account driver,
                           Branch branch,
                           Route route,
                           String code) {

        Plan plan = Plan.builder()
                .code(code)
                .car(car)
                .account(driver)
                .branch(branch)
                .route(route)
                .startTime(request.getStartTime())
                .status(request.getStatus().trim())
                .build();

        // ===== TẠO PLAN SEAT =====
        List<Seat> seats = seatRepository.findAllByOrderByIdAsc(
                PageRequest.of(0, car.getTotalSeat())
        );

        List<PlanSeat> planSeats = seats.stream()
                .map(seat -> PlanSeat.builder()
                        .plan(plan)
                        .seat(seat)
                        .status(PlanSeatStatus.AVAILABLE)
                        .build())
                .toList();

        plan.setPlanSeats(planSeats);

        return plan;
    }

    // ================= MAP RESPONSE =================
    private PlanResponse mapToResponse(Plan plan) {

        List<RouteStationResponse> stations = plan.getRoute().getRouteStations().stream()
                .sorted(Comparator.comparing(RouteStation::getStationOrder))
                .map(rs -> RouteStationResponse.builder()
                        .stationId(rs.getStation().getId())
                        .stationName(rs.getStation().getName())
                        .order(rs.getStationOrder())
                        .build())
                .toList();

        return PlanResponse.builder()
                .id(plan.getId())
                .code(plan.getCode())

                .carId(plan.getCar().getId())
                .carLicensePlate(plan.getCar().getLicensePlate())

                .accountId(plan.getAccount().getAccountId())
                .driverName(plan.getAccount().getFullName())
                .driverPhone(plan.getAccount().getPhone())

                .branchId(plan.getBranch().getId())
                .branchName(plan.getBranch().getName())

                .routeId(plan.getRoute().getId())
                .routeName(plan.getRoute().getName())

                .startTime(plan.getStartTime())

                .status(plan.getStatus())
                .stations(stations)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PlanListResponse getPlans(
            String code,
            Long departureStationId,
            Long destinationStationId,
            String status,
            Date startTime,
            Long accountId,
            Long branchId
    ) {

        Specification<Plan> spec = (root, query, cb) -> {
            query.distinct(true);
            return cb.conjunction();
        };

        // ===== FILTER CODE =====
        if (code != null && !code.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("code")), "%" + code.trim().toLowerCase() + "%")
            );
        }

        // ===== FILTER DEPARTURE + DESTINATION =====
        if (departureStationId != null && destinationStationId != null) {
            spec = spec.and((root, query, cb) -> {

                var route = root.join("route");

                var rsDeparture = route.join("routeStations");
                var rsDestination = route.join("routeStations");

                return cb.and(
                        // A là điểm đầu
                        cb.equal(rsDeparture.get("station").get("id"), departureStationId),
                        cb.equal(rsDeparture.get("stationOrder"), 1),

                        // B là điểm phía sau
                        cb.equal(rsDestination.get("station").get("id"), destinationStationId),
                        cb.greaterThan(rsDestination.get("stationOrder"), 1)
                );
            });
        }

        // ===== CHỈ DEPARTURE =====
        if (departureStationId != null && destinationStationId == null) {
            spec = spec.and((root, query, cb) -> {
                var route = root.join("route");
                var rs = route.join("routeStations");

                return cb.and(
                        cb.equal(rs.get("station").get("id"), departureStationId),
                        cb.equal(rs.get("stationOrder"), 1)
                );
            });
        }

        // ===== CHỈ DESTINATION =====
        if (destinationStationId != null && departureStationId == null) {
            spec = spec.and((root, query, cb) -> {
                var route = root.join("route");
                var rs = route.join("routeStations");

                return cb.and(
                        cb.equal(rs.get("station").get("id"), destinationStationId),
                        cb.greaterThan(rs.get("stationOrder"), 1)
                );
            });
        }

        // ===== FILTER STATUS =====
        if (status != null && !status.isBlank()) {
            String normalizedStatus = status.trim().toUpperCase();
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.upper(root.get("status")), normalizedStatus)
            );
        }

        // ===== FILTER DATE =====
        if (startTime != null) {
            LocalDateTime startOfDay = startTime.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .atStartOfDay();

            LocalDateTime endOfDay = startTime.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .atTime(LocalTime.MAX);

            spec = spec.and((root, query, cb) ->
                    cb.between(root.get("startTime"), startOfDay, endOfDay)
            );
        }

        // ===== FILTER ACCOUNT =====
        if (accountId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("account").get("accountId"), accountId)
            );
        }

        // ===== FILTER BRANCH =====
        if (branchId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("branch").get("id"), branchId)
            );
        }

        List<Plan> plans = planRepository.findAll(spec);

        if (plans.isEmpty()) {
            throw new AppException(PlanErrorCode.PLAN_NOT_FOUND);
        }

        List<PlanListItemResponse> items = plans.stream()
                .map(this::mapToPlanListItemResponse)
                .toList();

        return PlanListResponse.builder()
                .plans(items)
                .totalCount(items.size())
                .message("Danh sách plan")
                .build();
    }

    // ================= MAP RESPONSE =================
    private PlanListItemResponse mapToPlanListItemResponse(Plan plan) {

        List<RouteStationResponse> stations = plan.getRoute().getRouteStations().stream()
                .sorted(Comparator.comparing(RouteStation::getStationOrder))
                .map(rs -> RouteStationResponse.builder()
                        .stationId(rs.getStation().getId())
                        .stationName(rs.getStation().getName())
                        .order(rs.getStationOrder())
                        .build())
                .toList();

        return PlanListItemResponse.builder()
                .id(plan.getId())
                .code(plan.getCode())

                .carId(plan.getCar().getId())
                .carLicensePlate(plan.getCar().getLicensePlate())

                .accountId(plan.getAccount().getAccountId())
                .driverName(plan.getAccount().getFullName())
                .driverPhone(plan.getAccount().getPhone())

                .branchId(plan.getBranch().getId())
                .branchName(plan.getBranch().getName())

                .routeId(plan.getRoute().getId())
                .routeName(plan.getRoute().getName())

                .startTime(plan.getStartTime())
                .status(plan.getStatus())

                .stations(stations)
                .build();
    }

    @Override
    public PlanResponse getPlanDetail(Long planId) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new AppException(PlanErrorCode.PLAN_NOT_FOUND));

        Route route = plan.getRoute();

        // lấy danh sách station theo route
        List<RouteStation> routeStations =
                routeStationRepository.findByRouteIdOrderByStationOrder(route.getId());

        // map sang response
        List<RouteStationResponse> stationResponses = routeStations.stream()
                .map(rs -> RouteStationResponse.builder()
                        .stationId(rs.getStation().getId())
                        .stationName(rs.getStation().getName())
                        .order(rs.getStationOrder())
                        .build())
                .toList();

        return PlanResponse.builder()
                .id(plan.getId())
                .code(plan.getCode())

                .carId(plan.getCar().getId())
                .carLicensePlate(plan.getCar().getLicensePlate())

                .accountId(plan.getAccount().getAccountId())
                .driverName(plan.getAccount().getFullName())
                .driverPhone(plan.getAccount().getPhone())

                .branchId(plan.getBranch().getId())
                .branchName(plan.getBranch().getName())

                .routeId(route.getId())
                .routeName(route.getName())

                .startTime(plan.getStartTime())
                .status(plan.getStatus())

                .stations(stationResponses)
                .build();
    }

    @Override
    @Transactional
    public PlanResponse updatePlanStatus(Long planId, UpdatePlanStatusRequest request) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new AppException(PlanErrorCode.PLAN_NOT_FOUND));

        String normalizedStatus = request.getStatus().trim().toUpperCase();

        plan.setStatus(normalizedStatus);

        Plan updatedPlan = planRepository.save(plan);

        return mapToResponse(updatedPlan);
    }

    @Override
    public List<StationResponse> getStationsByPlan(Long planId) {

        // 1. Check plan tồn tại
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new AppException(PlanErrorCode.PLAN_NOT_FOUND));

        // 2. Lấy route từ plan
        Route route = plan.getRoute();

        // 3. Lấy danh sách route station (đã có order)
        List<RouteStation> routeStations =
                routeStationRepository.findByRouteIdOrderByStationOrder(route.getId());

        // 4. Map sang response
        return routeStations.stream()
                .map(rs -> StationResponse.builder()
                        .id(rs.getStation().getId())
                        .name(rs.getStation().getName())
                        .code(rs.getStation().getCode())
                        .address(rs.getStation().getAddress())
                        .latitude(rs.getStation().getLatitude())
                        .longitude(rs.getStation().getLongitude())
                        .cityName(rs.getStation().getCity().getName())
                        .build()
                )
                .toList();
    }
}