package vn.edu.fpt.dto.projection;

import java.math.BigDecimal;

public interface TicketStatProjection {
    Long getTotalTickets();
    BigDecimal getTotalRevenue();
}