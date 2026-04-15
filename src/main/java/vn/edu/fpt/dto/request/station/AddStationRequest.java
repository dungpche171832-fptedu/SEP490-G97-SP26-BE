package vn.edu.fpt.dto.request.station;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AddStationRequest {
    private String name;
    private String code;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private Long cityId;
}