package vn.edu.fpt.dto.response.rule;

import lombok.*;
import vn.edu.fpt.ultis.enums.CarType;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleResponse {

    private Long id;
    private CarType carType;
    private BigDecimal minKm;
    private BigDecimal maxKm;
    private BigDecimal price;
}