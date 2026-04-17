package vn.edu.fpt.dto.response.ticket;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.edu.fpt.entity.Ticket;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class TicketResponse {
    private Long id;
    private String bookingCode;
    private Long planId;
    private Long carId;
    private Long branchId;
    private Long accountId;
    private Double distanceKm;
    private BigDecimal totalAmount;
    private String status;
    private String note;

    // Constructor từ Ticket entity
    public TicketResponse(Ticket ticket) {
        this.id = ticket.getId();
        this.bookingCode = ticket.getBookingCode();
        this.planId = ticket.getPlan() != null ? ticket.getPlan().getId() : null;
        this.carId = ticket.getCar() != null ? ticket.getCar().getId() : null;
        this.branchId = ticket.getBranch() != null ? ticket.getBranch().getId() : null;
        this.accountId = ticket.getAccount() != null ? ticket.getAccount().getAccountId() : null;
        this.distanceKm = ticket.getDistanceKm();
        this.totalAmount = ticket.getTotalAmount();
        this.status = ticket.getStatus() != null ? ticket.getStatus().toString() : null;
        this.note = ticket.getNote();
    }

    // Constructor nhận các tham số riêng biệt
    public TicketResponse(Long id, String bookingCode, Long planId, Long carId, Long branchId, Long accountId, Double distanceKm, BigDecimal totalAmount, String status, String note) {
        this.id = id;
        this.bookingCode = bookingCode;
        this.planId = planId;
        this.carId = carId;
        this.branchId = branchId;
        this.accountId = accountId;
        this.distanceKm = distanceKm;
        this.totalAmount = totalAmount;
        this.status = status;
        this.note = note;
    }
}