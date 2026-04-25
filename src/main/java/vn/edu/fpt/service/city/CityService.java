package vn.edu.fpt.service.city;

import vn.edu.fpt.dto.response.city.CityResponse;

import java.util.List;

public interface CityService {
    List<CityResponse> getAllCities();
}