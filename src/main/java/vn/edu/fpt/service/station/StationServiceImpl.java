package vn.edu.fpt.service.station;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.edu.fpt.dto.request.station.AddStationRequest;
import vn.edu.fpt.dto.response.station.StationDetailResponse;
import vn.edu.fpt.dto.response.station.StationListResponse;
import vn.edu.fpt.dto.response.station.StationResponse;
import vn.edu.fpt.entity.City;
import vn.edu.fpt.entity.Station;
import vn.edu.fpt.exception.AppException;
import vn.edu.fpt.repository.CityRepository;
import vn.edu.fpt.repository.StationRepository;
import vn.edu.fpt.service.station.StationServiceImpl;
import vn.edu.fpt.ultis.errorCode.StationErrorCode;
import jakarta.persistence.criteria.Predicate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


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

    @Override
    @Transactional(readOnly = true)
    public StationListResponse getStations(String name, String code, Long cityId) {
        Specification<Station> spec = (root, query, cb) -> cb.conjunction();

        if (name != null && !name.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%")
            );
        }

        if (code != null && !code.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("code")), "%" + code.trim().toLowerCase() + "%")
            );
        }

        if (cityId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("city").get("id"), cityId)
            );
        }

        List<Station> stations = stationRepository.findAll(spec);

        if (stations.isEmpty()) {
            throw new AppException(StationErrorCode.STATION_NOT_FOUND);
        }

        List<StationResponse> stationResponses = stations.stream()
                .map(this::mapToResponse)
                .toList();

        return StationListResponse.builder()
                .stations(stationResponses)
                .message("Danh sách station")
                .totalCount(stationResponses.size())
                .build();
    }

    private void validateCoordinate(java.math.BigDecimal latitude, java.math.BigDecimal longitude) {
        if (latitude.compareTo(java.math.BigDecimal.valueOf(-90)) < 0
                || latitude.compareTo(java.math.BigDecimal.valueOf(90)) > 0) {
            throw new AppException(StationErrorCode.INVALID_LATITUDE);
        }

        if (longitude.compareTo(java.math.BigDecimal.valueOf(-180)) < 0
                || longitude.compareTo(java.math.BigDecimal.valueOf(180)) > 0) {
            throw new AppException(StationErrorCode.INVALID_LONGITUDE);
        }
    }

    private StationResponse mapToResponse(Station station) {
        return StationResponse.builder()
                .id(station.getId())
                .name(station.getName())
                .code(station.getCode())
                .latitude(station.getLatitude())
                .longitude(station.getLongitude())
                .address(station.getAddress())
                .cityName(station.getCity() != null ? station.getCity().getName() : null)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public StationDetailResponse getStationDetail(Long stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new AppException(StationErrorCode.STATION_NOT_FOUND));

        return mapToStationDetailResponse(station);
    }

    private StationDetailResponse mapToStationDetailResponse(Station station) {
        return StationDetailResponse.builder()
                .id(station.getId())
                .name(station.getName())
                .code(station.getCode())
                .latitude(station.getLatitude())
                .longitude(station.getLongitude())
                .address(station.getAddress())
                .cityId(station.getCity() != null ? station.getCity().getId() : null)
                .cityName(station.getCity() != null ? station.getCity().getName() : null)
                .build();
    }
}