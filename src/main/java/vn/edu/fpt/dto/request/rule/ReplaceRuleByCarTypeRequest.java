package vn.edu.fpt.dto.request.rule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import vn.edu.fpt.ultis.enums.CarType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplaceRuleByCarTypeRequest {

    @NotNull
    private CarType carType;

    @Valid
    @NotEmpty
    private List<ReplaceRuleItemRequest> rules;
}