package vn.edu.fpt.service.route;

import vn.edu.fpt.dto.request.route.CreateRouteRequest;
import vn.edu.fpt.dto.request.route.UpdateRouteRequest;
import vn.edu.fpt.dto.response.route.RouteResponse;

import java.util.List;

public interface RouteService {
    RouteResponse createRoute(CreateRouteRequest request);

    List<RouteResponse> getRoutes(String code, String name);

    RouteResponse updateRoute(Long routeId, UpdateRouteRequest request);

    RouteResponse getRouteDetail(Long routeId);
}