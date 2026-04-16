package vn.edu.fpt.service.plan;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.dto.request.plan.AddPlanRequest;
import vn.edu.fpt.dto.request.planStation.PlanStationRequest;
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

    @Override
    @Transactional
    public PlanResponse addPlan(AddPlanRequest request) {

        if (request.getStations() == null || request.getStations().size() < 2) {
            throw new AppException(PlanErrorCode.PLAN_INVALID_STATION_LIST);
        }

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new AppException(PlanErrorCode.INVALID_TIME_RANGE);
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