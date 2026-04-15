package vn.edu.fpt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.dto.request.station.AddStationRequest;
import vn.edu.fpt.dto.response.station.StationResponse;
import vn.edu.fpt.service.station.StationService;

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
}