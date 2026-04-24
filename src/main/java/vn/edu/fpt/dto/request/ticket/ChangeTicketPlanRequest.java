package vn.edu.fpt.dto.request.ticket;

import lombok.Data;

import java.util.List;

@Data
public class ChangeTicketPlanRequest {

    private Long newPlanId;
    private List<Long> newSeatIds;
}