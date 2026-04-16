package vn.edu.fpt.service.station;

import vn.edu.fpt.dto.request.station.AddStationRequest;
import vn.edu.fpt.dto.response.station.StationDetailResponse;
import vn.edu.fpt.dto.response.station.StationListResponse;
import vn.edu.fpt.dto.response.station.StationResponse;
import vn.edu.fpt.dto.response.station.UpdateStationRequest;

import java.util.List;

public interface StationService {
    StationResponse addStation(AddStationRequest request);

    StationListResponse getStations(String name, String code, Long cityId);

    StationDetailResponse getStationDetail(Long stationId);

    StationResponse updateStation(Long stationId, UpdateStationRequest request);
}