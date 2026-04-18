package vn.edu.fpt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.dto.request.ticket.CreateTicketRequest;
import vn.edu.fpt.dto.response.ticket.TicketAddResponse;
import vn.edu.fpt.dto.response.ticket.TicketListResponse;
import vn.edu.fpt.dto.response.ticket.TicketResponse;
import vn.edu.fpt.service.ticket.TicketService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ticket")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketAddResponse> createTicket(@RequestBody CreateTicketRequest request) {
        // Gọi service để tạo ticket
        TicketAddResponse response = ticketService.createTicket(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<TicketListResponse> getTickets(
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long accountId
    ) {
        // Gọi service để lấy danh sách vé và trả về kết quả
        TicketListResponse response = ticketService.getTickets(planId, branchId, accountId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> getTicketDetail(@PathVariable Long ticketId) {
        // Gọi service để lấy thông tin chi tiết vé
        TicketResponse ticketResponse = ticketService.getTicketDetail(ticketId);
        return ResponseEntity.ok(ticketResponse);
    }

    @PutMapping("/{ticketId}/status")
    public ResponseEntity<TicketResponse> updateTicketStatus(@PathVariable Long ticketId, @RequestBody String newStatus) {
        TicketResponse updatedTicket = ticketService.updateTicketStatus(ticketId, newStatus);
        return ResponseEntity.ok(updatedTicket);
    }
}
