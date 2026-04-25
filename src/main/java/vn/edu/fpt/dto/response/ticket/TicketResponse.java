package vn.edu.fpt.dto.response.ticket;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class TicketResponse {

    private Long id;
    private String bookingCode;

    private Long planId;
    private String planCode;

    private Long carId;
    private String carLicensePlate;

    private Long branchId;
    private String branchName;

    private Long accountId;
    private String accountName;

    private Double distanceKm;
    private BigDecimal totalAmount;

    private String status;
    private String note;

    private LocalDateTime startTime;

    private List<String> seatNumbers;
}