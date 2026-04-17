package vn.edu.fpt.dto.response.ticket;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class TicketResponse {
    private Long id;
    private String bookingCode;
    private Long planId;
    private Long carId;
    private Long accountId;
    private Long branchId;
    private Long startStationId;
    private Long endStationId;
    private Double distanceKm;
    private BigDecimal totalAmount;
    private String status;
}
