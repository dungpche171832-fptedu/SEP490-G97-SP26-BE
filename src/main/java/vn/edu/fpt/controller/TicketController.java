package vn.edu.fpt.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.dto.request.station.AddStationRequest;
import vn.edu.fpt.dto.request.ticket.CreateTicketRequest;
import vn.edu.fpt.dto.response.station.StationResponse;
import vn.edu.fpt.dto.response.ticket.TicketResponse;
import vn.edu.fpt.service.ticket.TicketService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ticket")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@RequestBody CreateTicketRequest request) {
        // Gọi service để tạo ticket
        TicketResponse response = ticketService.createTicket(request);

        return ResponseEntity.ok(response);
    }
}
