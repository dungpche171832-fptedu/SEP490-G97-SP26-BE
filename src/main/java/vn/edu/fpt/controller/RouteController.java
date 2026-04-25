package vn.edu.fpt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.dto.request.route.CreateRouteRequest;
import vn.edu.fpt.dto.request.route.UpdateRouteRequest;
import vn.edu.fpt.dto.response.route.RouteResponse;
import vn.edu.fpt.service.route.RouteService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping
    public ResponseEntity<RouteResponse> createRoute(@Valid @RequestBody CreateRouteRequest request) {
        return ResponseEntity.ok(routeService.createRoute(request));
    }

    @GetMapping
    public List<RouteResponse> getRoutes(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name
    ) {
        return routeService.getRoutes(code, name);
    }

    @PutMapping("/{id}")
    public RouteResponse updateRoute(
            @PathVariable Long id,
            @RequestBody UpdateRouteRequest request
    ) {
        return routeService.updateRoute(id, request);
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<RouteResponse> getRouteDetail(@PathVariable Long routeId) {
        return ResponseEntity.ok(routeService.getRouteDetail(routeId));
    }
}