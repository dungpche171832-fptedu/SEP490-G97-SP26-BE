package vn.edu.fpt.service.route;

import vn.edu.fpt.dto.request.route.CreateRouteRequest;
import vn.edu.fpt.dto.response.RouteResponse;

public interface RouteService {
    RouteResponse createRoute(CreateRouteRequest request);
}