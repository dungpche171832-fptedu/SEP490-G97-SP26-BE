package vn.edu.fpt.service.station;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.dto.request.station.AddStationRequest;
import vn.edu.fpt.dto.response.station.StationResponse;
import vn.edu.fpt.entity.City;
import vn.edu.fpt.entity.Station;
import vn.edu.fpt.exception.AppException;
import vn.edu.fpt.repository.CityRepository;
import vn.edu.fpt.repository.StationRepository;
import vn.edu.fpt.service.station.StationServiceImpl;
import vn.edu.fpt.ultis.errorCode.StationErrorCode;

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService {

    private final StationRepository stationRepository;
    private final CityRepository cityRepository;

    @Override
    public StationResponse addStation(AddStationRequest request) {

        // 1. Validate required
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new AppException(StationErrorCode.STATION_NAME_REQUIRED);
        }

        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw new AppException(StationErrorCode.STATION_CODE_REQUIRED);
        }

        if (request.getCityId() == null) {
            throw new AppException(StationErrorCode.CITY_ID_REQUIRED);
        }

        // 2. Validate latitude
        if (request.getLatitude() == null ||
                request.getLatitude().doubleValue() < -90 ||
                request.getLatitude().doubleValue() > 90) {
            throw new AppException(StationErrorCode.INVALID_LATITUDE);
        }

        // 3. Validate longitude
        if (request.getLongitude() == null ||
                request.getLongitude().doubleValue() < -180 ||
                request.getLongitude().doubleValue() > 180) {
            throw new AppException(StationErrorCode.INVALID_LONGITUDE);
        }

        // 5. Check trùng name
        if (stationRepository.findByName(request.getName()).isPresent()) {
            throw new AppException(StationErrorCode.STATION_NAME_EXISTS);
        }

        // 6. Check trùng code
        if (stationRepository.findByCode(request.getCode()).isPresent()) {
            throw new AppException(StationErrorCode.STATION_CODE_EXISTS);
        }

        // 7. Lấy city
        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new AppException(StationErrorCode.CITY_NOT_FOUND));

        // 8. Tạo station
        Station station = Station.builder()
                .name(request.getName().trim())
                .code(request.getCode().trim())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .city(city)
                .build();

        stationRepository.save(station);

        // 9. Map response
        return StationResponse.builder()
                .id(station.getId())
                .name(station.getName())
                .code(station.getCode())
                .latitude(station.getLatitude())
                .longitude(station.getLongitude())
                .address(station.getAddress())
                .cityName(city.getName())
                .build();
    }
}