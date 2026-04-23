package vn.edu.fpt.service.plan;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.dto.request.plan.AddPlanRequest;
import vn.edu.fpt.dto.request.plan.UpdatePlanStatusRequest;
import vn.edu.fpt.dto.request.planStation.PlanStationRequest;
import vn.edu.fpt.dto.response.plan.*;
import vn.edu.fpt.dto.response.planSeat.PlanSeatResponse;
import vn.edu.fpt.dto.response.planStation.PlanStationResponse;
import vn.edu.fpt.dto.response.routeStation.RouteStationResponse;
import vn.edu.fpt.dto.response.station.StationResponse;
import vn.edu.fpt.entity.*;
import vn.edu.fpt.exception.AppException;
import vn.edu.fpt.repository.*;
import vn.edu.fpt.ultis.enums.PlanSeatStatus;
import vn.edu.fpt.ultis.errorCode.BranchErrorCode;
import vn.edu.fpt.ultis.errorCode.PlanErrorCode;
import vn.edu.fpt.ultis.errorCode.RouteErrorCode;
import vn.edu.fpt.ultis.errorCode.StationErrorCode;

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

                .branchId(plan.getBranch().getId())
                .branchName(plan.getBranch().getName())

                .routeId(plan.getRoute().getId())
                .routeName(plan.getRoute().getName())

                .startTime(plan.getStartTime())

                .status(plan.getStatus())
                .stations(stations)
                .build();
    }

//    @Transactional(readOnly = true)
//    public PlanListResponse getPlans(String code, Long departureStationId, Long destinationStationId, String status, Date startTime, Long accountId) {
//
//        Specification<Plan> spec = (root, query, cb) -> {
//            query.distinct(true);
//            return cb.conjunction();
//        };
//
//        if (code != null && !code.isBlank()) {
//            spec = spec.and((root, query, cb) ->
//                    cb.like(cb.lower(root.get("code")), "%" + code.trim().toLowerCase() + "%")
//            );
//        }
//
//        if (departureStationId != null) {
//            spec = spec.and((root, query, cb) -> {
//                var joinPlanStation = root.join("planStations");
//                return cb.and(
//                        cb.equal(joinPlanStation.get("station").get("id"), departureStationId),
//                        cb.equal(joinPlanStation.get("stationOrder"), 1)
//                );
//            });
//        }
//
//        if (destinationStationId != null) {
//            spec = spec.and((root, query, cb) -> {
//                var joinPlanStation = root.join("planStations");
//                return cb.and(
//                        cb.equal(joinPlanStation.get("station").get("id"), destinationStationId),
//                        cb.greaterThan(joinPlanStation.get("stationOrder"), 1)
//                );
//            });
//        }
//
//        if (status != null && !status.isBlank()) {
//            String normalizedStatus = status.trim().toUpperCase();
//            spec = spec.and((root, query, cb) ->
//                    cb.equal(cb.upper(root.get("status")), normalizedStatus)
//            );
//        }
//
//        if (startTime != null) {
//            // Chuyển đổi startTime thành LocalDateTime
//            LocalDateTime startOfDay = startTime.toInstant()
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDate()
//                    .atStartOfDay(); // 00:00:00 của ngày truyền vào
//
//            // Kết thúc ngày (23:59:59)
//            LocalDateTime endOfDay = startTime.toInstant()
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDate()
//                    .atTime(LocalTime.MAX); // 23:59:59
//
//            // So sánh startTime nằm trong khoảng từ 00:00:00 đến 23:59:59
//            spec = spec.and((root, query, cb) ->
//                    cb.and(
//                            cb.greaterThanOrEqualTo(root.get("startTime"), startOfDay), // Bắt đầu ngày
//                            cb.lessThanOrEqualTo(root.get("startTime"), endOfDay)     // Kết thúc ngày
//                    )
//            );
//        }
//
//        if (accountId != null) {
//            // Thêm điều kiện lọc theo accountId
//            spec = spec.and((root, query, cb) ->
//                    cb.equal(root.get("account").get("id"), accountId) // Lọc theo accountId
//            );
//        }
//
//        List<Plan> plans = planRepository.findAll(spec);
//
//        if (plans.isEmpty()) {
//            throw new AppException(PlanErrorCode.PLAN_NOT_FOUND);
//        }
//
//        List<PlanListItemResponse> items = plans.stream()
//                .map(this::mapToPlanListItemResponse)
//                .toList();
//
//        return PlanListResponse.builder()
//                .plans(items)
//                .message("Danh sách plan")
//                .totalCount(items.size())
//                .build();
//    }
//
//    private PlanListItemResponse mapToPlanListItemResponse(Plan plan) {
//        List<PlanStation> orderedStations = planStationRepository.findByPlanIdOrderByStationOrderAsc(plan.getId());
//
//        List<PlanStationResponse> stationResponses = orderedStations.stream()
//                .map(planStation -> PlanStationResponse.builder()
//                        .stationId(planStation.getStation().getId())
//                        .stationName(planStation.getStation().getName())
//                        .stationOrder(planStation.getStationOrder())
//                        .build())
//                .toList();
//
//        return PlanListItemResponse.builder()
//                .id(plan.getId())
//                .code(plan.getCode())
//                .carId(plan.getCar().getId())
//                .carLicensePlate(plan.getCar().getLicensePlate())
//                .accountId(plan.getAccount().getAccountId())
//                .driverName(plan.getAccount().getFullName())
//                .startTime(plan.getStartTime())
//                .endTime(plan.getEndTime())
//                .status(plan.getStatus())
//                .stations(stationResponses)
//                .build();
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public PlanDetailResponse getPlanDetail(Long planId) {
//        Plan plan = planRepository.findById(planId)
//                .orElseThrow(() -> new AppException(PlanErrorCode.PLAN_NOT_FOUND));
//
//        return mapToPlanDetailResponse(plan);
//    }
//
//    private PlanDetailResponse mapToPlanDetailResponse(Plan plan) {
//        List<PlanStationResponse> stationResponses = plan.getPlanStations().stream()
//                .sorted((a, b) -> Integer.compare(a.getStationOrder(), b.getStationOrder()))
//                .map(planStation -> PlanStationResponse.builder()
//                        .stationId(planStation.getStation().getId())
//                        .stationName(planStation.getStation().getName())
//                        .stationOrder(planStation.getStationOrder())
//                        .build())
//                .toList();
//
//        List<PlanSeatResponse> seatResponses = plan.getPlanSeats().stream()
//                .sorted((a, b) -> Long.compare(a.getSeat().getId(), b.getSeat().getId()))
//                .map(planSeat -> PlanSeatResponse.builder()
//                        .seatId(planSeat.getSeat().getId())
//                        .seatNumber(planSeat.getSeat().getSeatNumber())
//                        .status(planSeat.getStatus().name())
//                        .build())
//                .toList();
//
//        return PlanDetailResponse.builder()
//                .id(plan.getId())
//                .code(plan.getCode())
//                .carId(plan.getCar().getId())
//                .carLicensePlate(plan.getCar().getLicensePlate())
//                .accountId(plan.getAccount().getAccountId())
//                .driverName(plan.getAccount().getFullName())
//                .startTime(plan.getStartTime())
//                .endTime(plan.getEndTime())
//                .status(plan.getStatus())
//                .stations(stationResponses)
//                .seats(seatResponses)
//                .build();
//    }
//
//    @Override
//    @Transactional
//    public PlanResponse updatePlanStatus(Long planId, UpdatePlanStatusRequest request) {
//        Plan plan = planRepository.findById(planId)
//                .orElseThrow(() -> new AppException(PlanErrorCode.PLAN_NOT_FOUND));
//
//        String normalizedStatus = request.getStatus().trim().toUpperCase();
//
//        plan.setStatus(normalizedStatus);
//
//        Plan updatedPlan = planRepository.save(plan);
//
//        return mapToResponse(updatedPlan);
//    }
//
//    @Override
//    public List<StationResponse> getStationsByPlan(Long planId) {
//
//        // 1. Check plan tồn tại
//        Plan plan = planRepository.findById(planId)
//                .orElseThrow(() -> new AppException(PlanErrorCode.PLAN_NOT_FOUND));
//
//        // 2. Lấy danh sách station
//        List<PlanStation> planStations = plan.getPlanStations();
//
//        // 3. Map sang response + sort theo order
//        return planStations.stream()
//                .sorted(Comparator.comparing(PlanStation::getStationOrder))
//                .map(ps -> StationResponse.builder()
//                        .id(ps.getStation().getId())
//                        .name(ps.getStation().getName())
//                        .code(ps.getStation().getCode())
//                        .address(ps.getStation().getAddress())
//                        .latitude(ps.getStation().getLatitude())
//                        .longitude(ps.getStation().getLongitude())
//                        .cityName(ps.getStation().getCity().getName())
//                        .build()
//                )
//                .toList();
//    }
}