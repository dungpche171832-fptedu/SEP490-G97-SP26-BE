package vn.edu.fpt.dto.request.ticket;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateTicketRequest {

    private Long planId;
    private Long carId;
    private Long startStationId;
    private Long endStationId;

    private Double distanceKm;
    private BigDecimal totalAmount;
    private String note;
    private List<Long> seatIds;

}