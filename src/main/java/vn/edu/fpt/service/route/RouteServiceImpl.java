package vn.edu.fpt.service.route;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.dto.request.route.CreateRouteRequest;
import vn.edu.fpt.dto.request.route.UpdateRouteRequest;
import vn.edu.fpt.dto.request.station.StationOrderRequest;
import vn.edu.fpt.dto.response.route.RouteResponse;
import vn.edu.fpt.dto.response.routeStation.RouteStationResponse;
import vn.edu.fpt.entity.Route;
import vn.edu.fpt.entity.RouteStation;
import vn.edu.fpt.entity.Station;
import vn.edu.fpt.repository.PlanRepository;
import vn.edu.fpt.repository.RouteRepository;
import vn.edu.fpt.repository.RouteStationRepository;
import vn.edu.fpt.repository.StationRepository;
import vn.edu.fpt.ultis.errorCode.RouteErrorCode;
import vn.edu.fpt.exception.AppException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final RouteStationRepository routeStationRepository;
    private final StationRepository stationRepository;
    private final PlanRepository planRepository;

    @Override
    @Transactional
    public RouteResponse createRoute(CreateRouteRequest request) {

        // ===== VALIDATE =====
        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new AppException(RouteErrorCode.ROUTE_CODE_REQUIRED);
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new AppException(RouteErrorCode.ROUTE_NAME_REQUIRED);
        }

        if (request.getStations() == null || request.getStations().isEmpty()) {
            throw new AppException(RouteErrorCode.STATION_LIST_EMPTY);
        }

        if (routeRepository.existsByCode(request.getCode())) {
            throw new AppException(RouteErrorCode.ROUTE_CODE_EXISTS);
        }

        // ===== CREATE ROUTE X =====
        Route route = Route.builder()
                .code(request.getCode())
                .name(request.getName())
                .isActive(true)
                .build();

        routeRepository.save(route);

        List<RouteStation> routeStations = new ArrayList<>();

        for (StationOrderRequest s : request.getStations()) {
            Station station = stationRepository.findById(s.getStationId())
                    .orElseThrow(() -> new AppException(RouteErrorCode.STATION_NOT_FOUND));

            routeStations.add(
                    RouteStation.builder()
                            .route(route)
                            .station(station)
                            .stationOrder(s.getOrder())
                            .build()
            );
        }

        routeStationRepository.saveAll(routeStations);

        // ===== CREATE ROUTE X' =====
        Route reverseRoute = Route.builder()
                .code(request.getCode() + "_R")
                .name(request.getName() + " (Reverse)")
                .isActive(true)
                .build();

        routeRepository.save(reverseRoute);

        List<RouteStation> reverseStations = new ArrayList<>();

        List<StationOrderRequest> reversedList = new ArrayList<>(request.getStations());
        Collections.reverse(reversedList);

        int order = 1;
        for (StationOrderRequest s : reversedList) {
            Station station = stationRepository.findById(s.getStationId())
                    .orElseThrow(() -> new AppException(RouteErrorCode.STATION_NOT_FOUND));

            reverseStations.add(
                    RouteStation.builder()
                            .route(reverseRoute)
                            .station(station)
                            .stationOrder(order++)
                            .build()
            );
        }

        routeStationRepository.saveAll(reverseStations);

        // ===== LINK =====
        route.setRouteRevert(reverseRoute);
        reverseRoute.setRouteRevert(route);

        routeRepository.save(route);
        routeRepository.save(reverseRoute);

        // ===== MAP RESPONSE =====
        List<RouteStationResponse> stationResponses = routeStations.stream()
                .sorted(Comparator.comparing(RouteStation::getStationOrder))
                .map(rs -> RouteStationResponse.builder()
                        .stationId(rs.getStation().getId())
                        .stationName(rs.getStation().getName())
                        .order(rs.getStationOrder())
                        .build()
                )
                .toList();

        return RouteResponse.builder()
                .id(route.getId())
                .code(route.getCode())
                .name(route.getName())
                .routeRevertId(reverseRoute.getId())
                .stations(stationResponses)
                .build();
    }

    @Override
    public List<RouteResponse> getRoutes(String code, String name) {

        Specification<Route> spec = (root, query, cb) -> cb.conjunction();

        // chỉ lấy active
        spec = spec.and((root, query, cb) ->
                cb.isTrue(root.get("isActive"))
        );

        if (code != null && !code.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%")
            );
        }

        if (name != null && !name.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%")
            );
        }

        List<Route> routes = routeRepository.findAll(spec);

        return routes.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private RouteResponse mapToResponse(Route route) {

        List<RouteStation> routeStations =
                routeStationRepository.findByRouteIdOrderByStationOrder(route.getId());

        List<RouteStationResponse> stations = routeStations.stream()
                .map(rs -> RouteStationResponse.builder()
                        .stationId(rs.getStation().getId())
                        .stationName(rs.getStation().getName())
                        .order(rs.getStationOrder())
                        .build()
                )
                .toList();

        return RouteResponse.builder()
                .id(route.getId())
                .code(route.getCode())
                .name(route.getName())
                .routeRevertId(
                        route.getRouteRevert() != null
                                ? route.getRouteRevert().getId()
                                : null
                )
                .stations(stations)
                .build();
    }

    @Override
    @Transactional
    public RouteResponse updateRoute(Long routeId, UpdateRouteRequest request) {

        // ===== LẤY ROUTE =====
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new AppException(RouteErrorCode.ROUTE_NOT_FOUND));

        if (planRepository.existsByRouteId(routeId)) {
            throw new AppException(RouteErrorCode.ROUTE_ALREADY_USED_IN_PLAN);
        }

        Route reverseRoute = route.getRouteRevert();

        // ===== VALIDATE =====
        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new AppException(RouteErrorCode.ROUTE_CODE_REQUIRED);
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new AppException(RouteErrorCode.ROUTE_NAME_REQUIRED);
        }

        if (request.getStations() == null || request.getStations().isEmpty()) {
            throw new AppException(RouteErrorCode.STATION_LIST_EMPTY);
        }

        // ===== UPDATE ROUTE CHÍNH =====
        route.setCode(request.getCode());
        route.setName(request.getName());

        // ===== UPDATE ROUTE REVERSE =====
        if (reverseRoute != null) {
            reverseRoute.setCode(request.getCode() + "_R");
            reverseRoute.setName(request.getName() + " (Reverse)");
        }

        // ===== DELETE STATIONS CŨ =====
        routeStationRepository.deleteByRouteId(routeId);

        if (reverseRoute != null) {
            routeStationRepository.deleteByRouteId(reverseRoute.getId());
        }
        routeStationRepository.flush();
        // ===== CREATE STATIONS MỚI =====
        List<RouteStation> newStations = new ArrayList<>();

        for (StationOrderRequest s : request.getStations()) {

            Station station = stationRepository.findById(s.getStationId())
                    .orElseThrow(() -> new AppException(RouteErrorCode.STATION_NOT_FOUND));

            newStations.add(
                    RouteStation.builder()
                            .route(route)
                            .station(station)
                            .stationOrder(s.getOrder())
                            .build()
            );
        }

        routeStationRepository.saveAll(newStations);

        // ===== CREATE REVERSE STATIONS =====
        if (reverseRoute != null) {

            List<StationOrderRequest> reversedList = new ArrayList<>(request.getStations());
            Collections.reverse(reversedList);

            List<RouteStation> reverseStations = new ArrayList<>();

            int order = 1;
            for (StationOrderRequest s : reversedList) {

                Station station = stationRepository.findById(s.getStationId())
                        .orElseThrow(() -> new AppException(RouteErrorCode.STATION_NOT_FOUND));

                reverseStations.add(
                        RouteStation.builder()
                                .route(reverseRoute)
                                .station(station)
                                .stationOrder(order++)
                                .build()
                );
            }

            routeStationRepository.saveAll(reverseStations);
        }

        routeRepository.save(route);
        if (reverseRoute != null) {
            routeRepository.save(reverseRoute);
        }

        return mapToResponse(route);
    }

    @Override
    public RouteResponse getRouteDetail(Long routeId) {

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new AppException(RouteErrorCode.ROUTE_NOT_FOUND));

        List<RouteStation> routeStations =
                routeStationRepository.findByRouteIdOrderByStationOrder(routeId);

        List<RouteStationResponse> stationResponses = routeStations.stream()
                .map(rs -> RouteStationResponse.builder()
                        .stationId(rs.getStation().getId())
                        .stationName(rs.getStation().getName())
                        .order(rs.getStationOrder())
                        .build())
                .toList();

        return RouteResponse.builder()
                .id(route.getId())
                .code(route.getCode())
                .name(route.getName())
                .routeRevertId(
                        route.getRouteRevert() != null
                                ? route.getRouteRevert().getId()
                                : null
                )
                .stations(stationResponses)
                .build();
    }
}