package vn.edu.fpt.service.ticket;

import vn.edu.fpt.dto.request.ticket.CreateTicketRequest;
import vn.edu.fpt.dto.response.ticket.TicketAddResponse;
import vn.edu.fpt.dto.response.ticket.TicketListResponse;
import vn.edu.fpt.dto.response.ticket.TicketResponse;

import java.util.List;

public interface TicketService {
    TicketAddResponse createTicket(CreateTicketRequest request);

    TicketListResponse getTickets(Long planId, Long branchId, Long accountId);

    TicketResponse getTicketDetail(Long ticketId);

    TicketResponse updateTicketStatus(Long ticketId, String newStatus);
}