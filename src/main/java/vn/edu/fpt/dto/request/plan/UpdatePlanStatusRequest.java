package vn.edu.fpt.dto.request.plan;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePlanStatusRequest {

    @NotBlank
    private String status;
}