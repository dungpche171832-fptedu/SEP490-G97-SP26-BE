package vn.edu.fpt.service.station;

import vn.edu.fpt.dto.request.station.AddStationRequest;
import vn.edu.fpt.dto.response.station.StationResponse;

public interface StationService {
    StationResponse addStation(AddStationRequest request);
}