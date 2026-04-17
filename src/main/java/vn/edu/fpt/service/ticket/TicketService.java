package vn.edu.fpt.service.ticket;

import vn.edu.fpt.dto.request.ticket.CreateTicketRequest;
import vn.edu.fpt.dto.response.ticket.TicketResponse;
import vn.edu.fpt.entity.Account;

public interface TicketService {
    TicketResponse createTicket(CreateTicketRequest request);
}