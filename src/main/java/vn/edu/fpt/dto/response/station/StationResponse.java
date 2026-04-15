package vn.edu.fpt.dto.response.station;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class StationResponse {
    private Long id;
    private String name;
    private String code;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private String cityName;
}