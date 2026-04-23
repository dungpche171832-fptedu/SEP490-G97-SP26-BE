package vn.edu.fpt.service.route;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.dto.request.route.CreateRouteRequest;
import vn.edu.fpt.dto.request.station.StationOrderRequest;
import vn.edu.fpt.dto.response.RouteResponse;
import vn.edu.fpt.dto.response.routeStation.RouteStationResponse;
import vn.edu.fpt.entity.Route;
import vn.edu.fpt.entity.RouteStation;
import vn.edu.fpt.entity.Station;
import vn.edu.fpt.repository.RouteRepository;
import vn.edu.fpt.repository.RouteStationRepository;
import vn.edu.fpt.repository.StationRepository;
import vn.edu.fpt.service.route.RouteService;
import vn.edu.fpt.ultis.errorCode.RouteErrorCode;
import vn.edu.fpt.exception.AppException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final RouteStationRepository routeStationRepository;
    private final StationRepository stationRepository;

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
}