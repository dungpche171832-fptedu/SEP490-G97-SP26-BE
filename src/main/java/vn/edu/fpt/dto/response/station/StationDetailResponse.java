package vn.edu.fpt.dto.response.station;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationDetailResponse {
    private long id;
    private String name;
    private String code;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private Long cityId;
    private String cityName;
}