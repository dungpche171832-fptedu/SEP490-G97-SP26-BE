package vn.edu.fpt.dto.response.planSeat;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanSeatResponse {
    private Long seatId;
    private String seatNumber;
    private String status;
}