package vn.edu.fpt.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.dto.request.station.AddStationRequest;
import vn.edu.fpt.dto.response.station.StationDetailResponse;
import vn.edu.fpt.dto.response.station.StationListResponse;
import vn.edu.fpt.dto.response.station.StationResponse;
import vn.edu.fpt.dto.response.station.UpdateStationRequest;
import vn.edu.fpt.service.station.StationService;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @PostMapping
    public ResponseEntity<StationResponse> addStation(
            @RequestBody AddStationRequest request) {
        StationResponse response = stationService.addStation(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<StationListResponse> getStations(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Long cityId
    ) {
        return ResponseEntity.ok(stationService.getStations(name, code, cityId));
    }

    @GetMapping("/{stationId}")
    public ResponseEntity<StationDetailResponse> getStationDetail(@PathVariable Long stationId) {
        return ResponseEntity.ok(stationService.getStationDetail(stationId));
    }

    @PutMapping("/{stationId}")
    public ResponseEntity<StationResponse> updateStation(
            @PathVariable Long stationId,
            @Valid @RequestBody UpdateStationRequest request
    ) {
        return ResponseEntity.ok(stationService.updateStation(stationId, request));
    }
}