package vn.edu.fpt.service.city;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.dto.response.CityResponse;
import vn.edu.fpt.entity.City;
import vn.edu.fpt.repository.CityRepository;
import vn.edu.fpt.service.city.CityService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    @Override
    public List<CityResponse> getAllCities() {
        List<City> cities = cityRepository.findAll();

        return cities.stream()
                .map(city -> CityResponse.builder()
                        .id(city.getId())
                        .name(city.getName())
                        .build())
                .collect(Collectors.toList());
    }
}