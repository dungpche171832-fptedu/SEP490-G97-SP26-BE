package vn.edu.fpt.service.plan;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.dto.request.plan.AddPlanRequest;
import vn.edu.fpt.dto.request.planStation.PlanStationRequest;
import vn.edu.fpt.dto.response.plan.PlanListItemResponse;
import vn.edu.fpt.dto.response.plan.PlanListResponse;
import vn.edu.fpt.dto.response.plan.PlanResponse;
import vn.edu.fpt.dto.response.planSeat.PlanSeatResponse;
import vn.edu.fpt.dto.response.planStation.PlanStationResponse;
import vn.edu.fpt.entity.*;
import vn.edu.fpt.exception.AppException;
import vn.edu.fpt.repository.*;
import vn.edu.fpt.ultis.enums.PlanSeatStatus;
import vn.edu.fpt.ultis.errorCode.PlanErrorCode;
import vn.edu.fpt.ultis.errorCode.StationErrorCode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final CarRepository carRepository;
    private final AccountRepository accountRepository;
    private final StationRepository stationRepository;
    private final SeatRepository seatRepository;
    private final PlanStationRepository planStationRepository;

    @Override
    @Transactional
    public PlanResponse addPlan(AddPlanRequest request) {

        if (request.getStations() == null || request.getStations().size() < 2) {
            throw new AppException(PlanErrorCode.PLAN_INVALID_STATION_LIST);
        }

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new AppException(PlanErrorCode.INVALID_TIME_RANGE);
        }

        String normalizedCode = request.getCode().trim();

        if (planRepository.existsByCode(normalizedCode)) {
            throw new AppException(PlanErrorCode.PLAN_CODE_ALREADY_EXISTS);
        }

        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new AppException(PlanErrorCode.PLAN_CAR_NOT_FOUND));

        Account driver = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AppException(PlanErrorCode.PLAN_DRIVER_NOT_FOUND));

        if (planRepository.existsByCarAndStartTime(car, request.getStartTime())) {
            throw new AppException(PlanErrorCode.PLAN_ALREADY_EXISTS);
        }

        Integer totalSeat = car.getTotalSeat();
        if (totalSeat == null || totalSeat <= 0) {
            throw new AppException(PlanErrorCode.INVALID_TOTAL_SEAT);
        }

        Set<Long> stationIds = new HashSet<>();
        Set<Integer> stationOrders = new HashSet<>();

        for (PlanStationRequest item : request.getStations()) {
            if (!stationIds.add(item.getStationId())) {
                throw new AppException(PlanErrorCode.PLAN_DUPLICATE_STATION);
            }
            if (!stationOrders.add(item.getStationOrder())) {
                throw new AppException(PlanErrorCode.PLAN_DUPLICATE_ORDER);
            }
        }

        Plan plan = Plan.builder()
                .code(normalizedCode)
                .car(car)
                .account(driver)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(request.getStatus().trim())
                .build();

        List<PlanStation> planStations = request.getStations().stream()
                .map(item -> {
                    Station station = stationRepository.findById(item.getStationId())
                            .orElseThrow(() -> new AppException(StationErrorCode.STATION_NOT_FOUND));

                    return PlanStation.builder()
                            .plan(plan)
                            .station(station)
                            .stationOrder(item.getStationOrder())
                            .build();
                })
                .toList();

        List<Seat> seatTemplates = seatRepository.findAllByOrderByIdAsc(PageRequest.of(0, totalSeat));

        if (seatTemplates.size() < totalSeat) {
            throw new AppException(PlanErrorCode.PLAN_SEAT_TEMPLATE_NOT_ENOUGH);
        }

        List<PlanSeat> planSeats = seatTemplates.stream()
                .map(seat -> PlanSeat.builder()
                        .plan(plan)
                        .seat(seat)
                        .status(PlanSeatStatus.AVAILABLE)
                        .build())
                .toList();

        plan.setPlanStations(planStations);
        plan.setPlanSeats(planSeats);

        Plan savedPlan = planRepository.save(plan);

        return mapToResponse(savedPlan);
    }

    private PlanResponse mapToResponse(Plan plan) {

        List<PlanStationResponse> stationResponses = plan.getPlanStations().stream()
                .sorted((a, b) -> Integer.compare(a.getStationOrder(), b.getStationOrder()))
                .map(ps -> PlanStationResponse.builder()
                        .stationId(ps.getStation().getId())
                        .stationName(ps.getStation().getName())
                        .stationOrder(ps.getStationOrder())
                        .build())
                .toList();

        List<PlanSeatResponse> seatResponses = plan.getPlanSeats().stream()
                .sorted((a, b) -> Long.compare(a.getSeat().getId(), b.getSeat().getId()))
                .map(ps -> PlanSeatResponse.builder()
                        .seatId(ps.getSeat().getId())
                        .seatNumber(ps.getSeat().getSeatNumber())
                        .status(ps.getStatus().name())
                        .build())
                .toList();

        return PlanResponse.builder()
                .id(plan.getId())
                .code(plan.getCode())
                .carId(plan.getCar().getId())
                .carLicensePlate(plan.getCar().getLicensePlate())
                .accountId(plan.getAccount().getAccountId())
                .driverName(plan.getAccount().getFullName())
                .startTime(plan.getStartTime())
                .endTime(plan.getEndTime())
                .status(plan.getStatus())
                .stations(stationResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PlanListResponse getPlans(String code, Long departureStationId, Long destinationStationId) {

        Specification<Plan> spec = (root, query, cb) -> {
            query.distinct(true);
            return cb.conjunction();
        };

        if (code != null && !code.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("code")), "%" + code.trim().toLowerCase() + "%")
            );
        }

        if (departureStationId != null) {
            spec = spec.and((root, query, cb) -> {
                var joinPlanStation = root.join("planStations");
                return cb.and(
                        cb.equal(joinPlanStation.get("station").get("id"), departureStationId),
                        cb.equal(joinPlanStation.get("stationOrder"), 1)
                );
            });
        }

        if (destinationStationId != null) {
            spec = spec.and((root, query, cb) -> {
                var joinPlanStation = root.join("planStations");
                return cb.and(
                        cb.equal(joinPlanStation.get("station").get("id"), destinationStationId),
                        cb.greaterThan(joinPlanStation.get("stationOrder"), 1)
                );
            });
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
                .message("Danh sách plan")
                .totalCount(items.size())
                .build();
    }

    private PlanListItemResponse mapToPlanListItemResponse(Plan plan) {
        List<PlanStation> orderedStations = planStationRepository.findByPlanIdOrderByStationOrderAsc(plan.getId());

        List<PlanStationResponse> stationResponses = orderedStations.stream()
                .map(planStation -> PlanStationResponse.builder()
                        .stationId(planStation.getStation().getId())
                        .stationName(planStation.getStation().getName())
                        .stationOrder(planStation.getStationOrder())
                        .build())
                .toList();

        return PlanListItemResponse.builder()
                .id(plan.getId())
                .code(plan.getCode())
                .carId(plan.getCar().getId())
                .carLicensePlate(plan.getCar().getLicensePlate())
                .accountId(plan.getAccount().getAccountId())
                .driverName(plan.getAccount().getFullName())
                .startTime(plan.getStartTime())
                .endTime(plan.getEndTime())
                .status(plan.getStatus())
                .stations(stationResponses)
                .build();
    }
}