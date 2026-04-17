package vn.edu.fpt.dto.request.rule;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplaceRuleItemRequest {

    @NotNull
    private BigDecimal minKm;

    private BigDecimal maxKm;

    @NotNull
    private BigDecimal price;
}